package com.globo.to_na_globo.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.globo.to_na_globo.models.Campaign;
import com.globo.to_na_globo.models.CampaignBean;
import com.globo.to_na_globo.services.CampaignService;

@WebServlet("/campaigns")
public class CampaignServlet extends HttpServlet {

	private static final long serialVersionUID = 3426367542280550693L;
	
	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String reqIdStr = request.getParameter("id");
			Long reqId;
			try {
				reqId = new Long(reqIdStr);
			} catch (Exception exc) {
				reqId = null;
			}
			if (reqId == null) {
				List<Campaign> campList = CampaignService.instance.all();
				List<JSONObject> jsonList = new ArrayList<>();
				for (Campaign camp : campList) {
					long id = camp.getId();
					String name = camp.getName();
					String keyword = camp.getKeyWord();
					String startDateStr;
					try {
						startDateStr = DATE_FORMATTER.format(camp.getStartDate());
					} catch (Exception exc) {
						continue;
					}
					String startTimeStr;
					try {
						startTimeStr = TIME_FORMATTER.format(camp.getStartDate());
					} catch (Exception exc) {
						continue;
					}
					String endDateStr;
					try {
						endDateStr = DATE_FORMATTER.format(camp.getEndDate());
					} catch (Exception exc) {
						endDateStr = null;
					}
					String endTimeStr;
					try {
						endTimeStr = TIME_FORMATTER.format(camp.getEndDate());
					} catch (Exception exc) {
						endTimeStr = null;
					}
					JSONObject object = new JSONObject();
					object.put("id", id);
					object.put("name", name);
					object.put("keyword", keyword);
					object.put("startDate", startDateStr);
					object.put("startTime", startTimeStr);
					object.put("endDate", endDateStr);
					object.put("endTime", endTimeStr);
					jsonList.add(object);
				}
				response.setHeader("content-type", "application/json");
				PrintWriter writer = response.getWriter();
				writer.println(jsonList);
				writer.flush();
			} else {
				Campaign camp;
				try {
					camp = CampaignService.instance.get(reqId);
				} catch (Exception exc) {
					response.setHeader("content-type", "application/json");
					response.getWriter().flush();
					return;
				}
				long id = camp.getId();
				String name = camp.getName();
				String keyword = camp.getKeyWord();
				String startDateStr;
				try {
					startDateStr = DATE_FORMATTER.format(camp.getStartDate());
				} catch (Exception exc) {
					response.setHeader("content-type", "application/json");
					response.getWriter().flush();
					return;
				}
				String startTimeStr;
				try {
					startTimeStr = TIME_FORMATTER.format(camp.getStartDate());
				} catch (Exception exc) {
					response.setHeader("content-type", "application/json");
					response.getWriter().flush();
					return;
				}
				String endDateStr;
				try {
					endDateStr = DATE_FORMATTER.format(camp.getEndDate());
				} catch (Exception exc) {
					endDateStr = null;
				}
				String endTimeStr;
				try {
					endTimeStr = TIME_FORMATTER.format(camp.getEndDate());
				} catch (Exception exc) {
					endTimeStr = null;
				}
				JSONObject object = new JSONObject();
				object.put("id", id);
				object.put("name", name);
				object.put("keyword", keyword);
				object.put("startDate", startDateStr);
				object.put("startTime", startTimeStr);
				object.put("endDate", endDateStr);
				object.put("endTime", endTimeStr);
				response.setHeader("content-type", "application/json");
				PrintWriter writer = response.getWriter();
				writer.println(object);
				writer.flush();
			}
		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("Internal server error");
			response.getWriter().flush();
			e.printStackTrace();
		}
	}
	
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
			response.setHeader("content-type", "application/json");
			PrintWriter writer = response.getWriter();
			writer.println(campaign.getId());
			writer.flush();
		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("Internal server error");
			response.getWriter().flush();
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
			long id;
			try {
				id = params.getLong("id");
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send id parameter");
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
			Campaign campaign = new CampaignBean();
			campaign.setId(id);
			campaign.setName(name);
			campaign.setKeyWord(keyword);
			campaign.setStartDate(startDate);
			campaign.setEndDate(endDate);
			CampaignService.instance.save(campaign);
			response.setHeader("content-type", "application/json");
			PrintWriter writer = response.getWriter();
			writer.println("{}");
			writer.flush();
		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("Internal server error");
			response.getWriter().flush();
			e.printStackTrace();
		}
	}
	
}
