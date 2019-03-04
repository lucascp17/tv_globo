package com.globo.to_na_globo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

import com.globo.to_na_globo.models.Campaign;
import com.globo.to_na_globo.models.VoteItem;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterService {
	
	private static final String CONSUMER_KEY = "ewFUoTmH9OqRNpPHlSQGjDWjy";
	private static final String CONSUMER_SECRET = "1Lx3QoxaJ7IYgGWcCupulsho0hHlIbZBR1VcWGyyBmSEDouhip";
	private static final String ACCESS_TOKEN_KEY = "132913546-g5dTIJtbJmVXH5VrTGHgCUL4kwbloyLwru5f15o9";
	private static final String ACCESS_TOKEN_SECRET = "6MPzelwyjALEbcreh3vbq4yQnIOnIDh50SpMZjtsd0u3F";
	
	public static final TwitterService instance;
	
	static {
		instance = new TwitterService();
	}
	
	private BlockingQueue<String> msgQueue;
	private BlockingQueue<Event> eventQueue;
	private Authentication hosebirdAuth;
	private Map<Long, TwitterStalker> stalkersMap;
	
	private TwitterService() {
		msgQueue = new LinkedBlockingQueue<String>(100000);
		eventQueue = new LinkedBlockingQueue<Event>(1000);
		hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN_KEY, ACCESS_TOKEN_SECRET);
		stalkersMap = new HashMap<>();
	}
	
	public void scheduleService(Campaign campaign, VoteItem... items) {
		long currentTime = System.currentTimeMillis();
		if (campaign.getEndDate() != null && campaign.getEndDate().getTime() < currentTime)
			return;
		if (currentTime > campaign.getStartDate().getTime()) {
			startService(campaign, items);
		} else {
			TimerTask task = new CampaignStarter(campaign, items);
			Timer timer = new Timer();
			timer.schedule(task, campaign.getStartDate());
		}
	}
	
	public void startService(Campaign campaign, VoteItem... items) {
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		List<String> terms = new ArrayList<>();
		terms.add(campaign.getKeyWord());
		for (VoteItem voteItem : items)
			terms.add(voteItem.getKeyWord());
		hosebirdEndpoint.trackTerms(terms);
		ClientBuilder builder = new ClientBuilder()
				  .name("Hosebird-Client-01")                              
				  .hosts(hosebirdHosts)
				  .authentication(hosebirdAuth)
				  .endpoint(hosebirdEndpoint)
				  .processor(new StringDelimitedProcessor(msgQueue))
				  .eventMessageQueue(eventQueue);
		Client client = builder.build();
		TwitterStalker stalker = new TwitterStalker(campaign, items, client);
		stalkersMap.put(campaign.getId(), stalker);
		stalker.start();
		if (campaign.getEndDate() != null) {
			TimerTask finalizer = new CampaignFinalizer(campaign);
			Timer timer = new Timer();
			timer.schedule(finalizer, campaign.getEndDate());
		}
	}
	
	public void stopService(Campaign campaign) {
		stopService(campaign.getId());
	}
	
	public void stopService(long campaignId) {
		TwitterStalker stalker = stalkersMap.get(campaignId);
		if (stalker != null) {
			stalker.interrupt();
			stalkersMap.remove(campaignId);
		}
	}
	
	private class CampaignStarter extends TimerTask {
		
		private Campaign campaign;
		private VoteItem[] voteItems;
		
		public CampaignStarter(Campaign campaign, VoteItem[] voteItems) {
			this.campaign = campaign;
			this.voteItems = voteItems;
		}
		
		@Override
		public void run() {
			TwitterService.this.startService(campaign, voteItems);
		}
		
	}
	
	private class CampaignFinalizer extends TimerTask {
		
		private Campaign campaign;
		
		public CampaignFinalizer(Campaign campaign) {
			this.campaign = campaign;
		}
		
		@Override
		public void run() {
			TwitterService.this.stopService(campaign);
		}
		
	}
	
	private class TwitterStalker extends Thread {
		
		private Client hosebirdClient;
		private Campaign campaign;
		private VoteItem[] items;
		
		public TwitterStalker(Campaign campaign, VoteItem[] items, Client client) {
			super();
			this.campaign = campaign;
			this.items = items;
			this.hosebirdClient = client;
		}
		
		@Override
		public void run() {
			try {
				logServiceStart();
				hosebirdClient.connect();
				while (!hosebirdClient.isDone()) {
				    String msg = msgQueue.take();
				    try {
				    	JSONObject msgObject = new JSONObject(msg);
				    	String text = msgObject.getString("text");
				    	if (text == null)
				    		continue;
				    	if (text.indexOf(campaign.getKeyWord()) < 0)
				    		continue;
				    	logMessageReceived(text);
				    	for (int i = 0; i < items.length; ++i) {
				    		VoteItem item = items[i];
				    		int idx = text.indexOf(item.getKeyWord());
				    		if (idx < 0)
				    			continue;
				    		countVote(item);
				    	}
				    } catch (Exception exc) {
				    	
				    }
				}
			} catch (InterruptedException exc) {
				System.out.println("Interrupted");
			}
		}
		
		@Override
		public void interrupt() {
			logServiceEnd();
			hosebirdClient.stop();
			super.interrupt();
		}
		
		private void countVote(VoteItem item) {
			try {
				logVoteReceived(item);
				VoteItemService.instance.countVote(item.getId());
				VoteService.instance.countVote(campaign.getId(), item.getId());
			} catch (Exception exc) {
				System.out.println("ERROR! Something got wrong when couting votes!");
				exc.printStackTrace();
			}
		}
		
		private void logServiceStart() {
			StringBuilder sb = new StringBuilder();
			sb.append("We are now listening to campaign \"");
			sb.append(campaign.getName());
			sb.append("\", keyword \"");
			sb.append(campaign.getKeyWord());
			sb.append("\", options ");
			for (int i = 0; i < items.length; ++i) {
				if (i > 0)
					sb.append(", ");
				sb.append("\"");
				sb.append(items[i].getKeyWord());
				sb.append("\"");
			}
			String log = sb.toString();
			System.out.println(log);
		}
		
		private void logServiceEnd() {
			StringBuilder sb = new StringBuilder();
			sb.append("We are now interrupting campaign \"");
			sb.append(campaign.getName());
			sb.append("\", keyword \"");
			sb.append(campaign.getKeyWord());
			sb.append("\", options ");
			for (int i = 0; i < items.length; ++i) {
				if (i > 0)
					sb.append(", ");
				sb.append("\"");
				sb.append(items[i].getKeyWord());
				sb.append("\"");
			}
			String log = sb.toString();
			System.out.println(log);
		}
		
		private void logVoteReceived(VoteItem item) {
			StringBuilder sb = new StringBuilder();
			sb.append("Received vote from campaign \"");
			sb.append(campaign.getName());
			sb.append("\", keyword \"");
			sb.append(campaign.getKeyWord());
			sb.append("\", option \"");
			sb.append(item.getKeyWord());
			sb.append("\"");
			String log = sb.toString();
			System.out.println(log);
		}
		
		private void logMessageReceived(String msg) {
			StringBuilder sb = new StringBuilder();
			sb.append("Received message from campaign \"");
			sb.append(campaign.getName());
			sb.append("\", keyword \"");
			sb.append(campaign.getKeyWord());
			sb.append("\", options ");
			for (int i = 0; i < items.length; ++i) {
				if (i > 0)
					sb.append(", ");
				sb.append("\"");
				sb.append(items[i].getKeyWord());
				sb.append("\"");
			}
			sb.append(", content: \"");
			sb.append(msg);
			sb.append("\"");
			String log = sb.toString();
			System.out.println(log);
		}
		
	}

}
