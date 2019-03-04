package com.globo.to_na_globo.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.globo.to_na_globo.services.CampaignService;

@WebServlet("/cancel_campaign")
public class CampaignCancellationServlet extends HttpServlet {

	private static final long serialVersionUID = 6084207801445195561L;
	
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
			long campaign;
			try {
				campaign = params.getLong("campaign");
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Send name parameter");
				response.getWriter().flush();
				return;
			}
			CampaignService.instance.cancel(campaign);
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
