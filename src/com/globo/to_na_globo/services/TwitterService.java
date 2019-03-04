package com.globo.to_na_globo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.globo.to_na_globo.models.Campaign;
import com.globo.to_na_globo.models.VoteItem;
import com.google.common.collect.Lists;
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
	private Map<String, Object> servicesMap;
	
	private TwitterService() {
		msgQueue = new LinkedBlockingQueue<String>(100000);
		eventQueue = new LinkedBlockingQueue<Event>(1000);
		hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN_KEY, ACCESS_TOKEN_SECRET);
	}
	
	public void scheduleService(Campaign campaign) {
		
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
		
	}
	
	private class TwitterStalker extends Thread {
		
		private Client hosebirdClient;
		
		public TwitterStalker(ClientBuilder clientBuilder) {
			hosebirdClient = clientBuilder.build();
		}
		
		@Override
		public void run() {                       
			hosebirdClient.connect();
			while (!hosebirdClient.isDone()) {
			    String msg = msgQueue.take();
			    something(msg);
			    profit();
			}
		}
		
	}

}
