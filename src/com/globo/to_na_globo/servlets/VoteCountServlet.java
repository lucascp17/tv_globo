package com.globo.to_na_globo.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.globo.to_na_globo.services.VoteItemService;

@WebServlet("/votecount")
public class VoteCountServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1373004331233271838L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Long voteItemId;
			try {
				String voteItemIdStr = request.getParameter("voteitem");
				voteItemId = new Long(voteItemIdStr);
			} catch (Exception exc) {
				response.setStatus(400);
				response.getWriter().println("Vote item is required");
				response.getWriter().flush();
				return;
			}
			long count = VoteItemService.instance.getVoteCount(voteItemId);
			response.setHeader("content-type", "application/json");
			PrintWriter writer = response.getWriter();
			writer.println(count);
			writer.flush();
		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("Internal server error");
			response.getWriter().flush();
			e.printStackTrace();
		}
	}

}
