package com.globo.to_na_globo.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VoteService {
	
	public final static VoteService instance;
	
	static {
		instance = new VoteService();
	}
	
	private VoteService() {}
	
	public void countVote(long campaignId, long itemId) throws Exception {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR);
		String timeSignature = String.format("%d-%d-%d-%d", year, month, day, hour);
		String sql = "insert into Votes(owner, choice, time_value, time_signature) values (?, ?, ?, ?)";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, campaignId);
		stmt.setLong(2, itemId);
		stmt.setTimestamp(3, new java.sql.Timestamp(cal.getTime().getTime()));
		stmt.setString(4, timeSignature);
		stmt.executeUpdate();
		conn.close();
	}
	
	public List<Object[]> getVotesStats(long campaignId) throws Exception {
		String sql = "select time_signature, count(id) from Votes where owner = ? group by time_signature";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, campaignId);
		ResultSet set = stmt.executeQuery();
		List<Object[]> list = new ArrayList<>();
		while (set.next()) {
			String signature = set.getString(1);
			int count = set.getInt(2);
			Object[] elem = new Object[] {signature, count};
			list.add(elem);
		}
		conn.close();
		return list;
	}

}
