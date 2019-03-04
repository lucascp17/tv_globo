package com.globo.to_na_globo.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globo.to_na_globo.models.VoteItem;
import com.globo.to_na_globo.services.VoteItemService;

@WebServlet("/voteitems")
public class VoteItemServlet extends HttpServlet {
	
	private static final long serialVersionUID = -3499433221140636975L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Long campaignId;
			try {
				String campaignIdStr = request.getParameter("campaign");
				campaignId = new Long(campaignIdStr);
			} catch (Exception e) {
				response.setStatus(400);
				response.getWriter().println("The campaign param is required");
				response.getWriter().flush();
				e.printStackTrace();
				return;
			}
			List<VoteItem> items = VoteItemService.instance.allByCampaign(campaignId);
			List<JSONObject> list = new ArrayList<>();
			for (VoteItem item : items) {
				JSONObject object = new JSONObject();
				object.put("id", item.getId());
				object.put("campaign", item.getOwner());
				object.put("keyword", item.getKeyWord());
				list.add(object);
			}
			JSONArray array = new JSONArray(list);
			response.setHeader("content-type", "application/json");
			PrintWriter writer = response.getWriter();
			writer.println(array);
			writer.flush();
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
			Long campaign;
			try {
				campaign = params.getLong("campaign");
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("A campaign is required");
				response.getWriter().flush();
				exc.printStackTrace();
				return;
			}
			JSONArray keywordList;
			try {
				keywordList = params.getJSONArray("keywords");
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send keywords parameter");
				response.getWriter().flush();
				return;
			}
			String[] keywords;
			try {
				keywords = new String[keywordList.length()];
				for (int i = 0; i < keywordList.length(); ++i)
					keywords[i] = keywordList.getString(i);
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send keywords parameter");
				response.getWriter().flush();
				return;
			}
			VoteItem[] items = VoteItemService.instance.create(campaign, keywords);
			List<Long> idsList = new ArrayList<>();
			for (VoteItem item : items)
				if (item != null)
					idsList.add(item.getId());
			JSONArray array = new JSONArray(idsList);
			response.setHeader("content-type", "application/json");
			PrintWriter writer = response.getWriter();
			writer.println(array);
			writer.flush();
		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("Internal server error");
			response.getWriter().flush();
			e.printStackTrace();
		}
	}

}
