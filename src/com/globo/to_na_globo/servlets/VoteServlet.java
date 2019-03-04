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

import com.globo.to_na_globo.services.VoteService;

@WebServlet("/votes")
public class VoteServlet extends HttpServlet {
	
	private static final long serialVersionUID = -922132018531193882L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Long campaignId;
			try {
				String campaignIdStr = request.getParameter("campaign");
				campaignId = new Long(campaignIdStr);
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Campaign is required");
				response.getWriter().flush();
				return;
			}
			List<Object[]> stats = VoteService.instance.getVotesStats(campaignId);
			List<JSONObject> list = new ArrayList<>();
			for (Object[] stat : stats) {
				JSONObject obj = new JSONObject();
				obj.put("time", stat[0]);
				obj.put("count", stat[1]);
				list.add(obj);
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

}
