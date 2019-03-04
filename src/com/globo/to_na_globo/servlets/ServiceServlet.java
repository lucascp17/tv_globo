package com.globo.to_na_globo.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.globo.to_na_globo.models.Campaign;
import com.globo.to_na_globo.models.VoteItem;
import com.globo.to_na_globo.services.CampaignService;
import com.globo.to_na_globo.services.TwitterService;
import com.globo.to_na_globo.services.VoteItemService;

@WebServlet("/service")
public class ServiceServlet extends HttpServlet {
	
	private static final long serialVersionUID = 5476018444348442357L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String body = ""; 
			String line = request.getReader().readLine();
			while (line != null) {
				body = body + line;
				line = request.getReader().readLine();
			}
			JSONObject params; 
			try {
				params = new JSONObject(body);
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send a valid JSON object");
				response.getWriter().flush();
				return;
			}
			String name;
			try {
				name = params.getString("name");
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send name parameter");
				response.getWriter().flush();
				return;
			}
			String keyword;
			try {
				keyword = params.getString("keyword");
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send keyword parameter");
				response.getWriter().flush();
				return;
			}
			String[] options;
			try {
				String grossOptions = params.getString("options");
				options = grossOptions.split(";");
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send options parameter");
				response.getWriter().flush();
				return;
			}
			Calendar cal = Calendar.getInstance();
			Date startDate;
			try {
				String startDateStr = params.getString("startDate");
				String startTimeStr = params.getString("startTime");
				String[] tokens = startDateStr.split("-");
				int year = Integer.parseInt(tokens[0]);
				int month = Integer.parseInt(tokens[1]);
				int day = Integer.parseInt(tokens[2]);
				int hour = 0;
				int minute = 0;
				if (startTimeStr != null && !startTimeStr.trim().isEmpty()) {
					tokens = startTimeStr.split(":");
					hour = Integer.parseInt(tokens[0]);
					minute = Integer.parseInt(tokens[1]);
				}
				cal.set(year, month, day, hour, minute);
				startDate = cal.getTime();
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send a valid date for start date parameter");
				response.getWriter().flush();
				exc.printStackTrace();
				return;
			}
			Date endDate;
			try {
				String endDateStr = params.getString("endDate");
				String endTimeStr = params.getString("endTime");
				if (endDateStr != null && !endDateStr.trim().isEmpty()) {
					String[] tokens = endDateStr.split("-");
					int year = Integer.parseInt(tokens[0]);
					int month = Integer.parseInt(tokens[1]);
					int day = Integer.parseInt(tokens[2]);
					int hour = 0;
					int minute = 0;
					if (endTimeStr != null && !endTimeStr.trim().isEmpty()) {
						tokens = endTimeStr.split(":");
						hour = Integer.parseInt(tokens[0]);
						minute = Integer.parseInt(tokens[1]);
					}
					cal.set(year, month, day, hour, minute);
					endDate = cal.getTime();
				} else {
					endDate = null;
				}
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send a valid date for end date parameter");
				response.getWriter().flush();
				return;
			}
			Campaign campaign = CampaignService.instance.create(name, keyword, startDate, endDate);
			VoteItem[] items = VoteItemService.instance.create(campaign.getId(), options);
			TwitterService.instance.scheduleService(campaign, items);
			response.setHeader("content-type", "application/json");
			PrintWriter writer = response.getWriter();
			writer.print("{\"campaignId\":");
			writer.print(campaign.getId());
			writer.print(",\"voteItemIds\":[");
			for (int i = 0; i < items.length; ++i)
				if (items[i] != null) {
					if (i > 0)
						writer.print(",");
					writer.print(items[i].getId());
				}
			writer.println("]}");
			writer.flush();
		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("Internal server error");
			response.getWriter().flush();
			e.printStackTrace();
		}
	}
	
}
