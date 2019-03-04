package com.globo.to_na_globo.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.globo.to_na_globo.models.Campaign;
import com.globo.to_na_globo.models.CampaignBean;

public class CampaignService {
	
	public final static CampaignService instance;
	
	static {
		instance = new CampaignService();
	}
	
	private CampaignService() {}
	
	public Campaign create(String name, String keyword, Date startDate, Date endDate) throws Exception {
		if (name == null || name.trim().isEmpty())
			throw new Exception("The param name cannot be null");
		if (keyword == null || keyword.trim().isEmpty())
			throw new Exception("The param keyword cannot be null");
		if (startDate == null)
			throw new Exception("The param startDate cannot be null");
		String sql = "insert into Campaigns(name, keyword, start_date, end_date) values (?, ?, ?, ?)";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, name);
		stmt.setString(2, keyword);
		stmt.setDate(3, new java.sql.Date(startDate.getTime()));
		stmt.setDate(4, endDate == null ? null : new java.sql.Date(endDate.getTime()));
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if (!keys.next())
			throw new Exception("Could not get generated key");
		long lastId = keys.getLong(1);
		stmt.close();
		Campaign result = new CampaignBean();
		result.setId(lastId);
		result.setName(name);
		result.setKeyWord(keyword);
		result.setStartDate(startDate);
		result.setEndDate(endDate);
		conn.close();
		return result;
	}
	
	public void cancel(long campaignId) throws Exception {
		String sql = "update Campaigns set canceled=1 where id=?";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, campaignId);
		stmt.executeUpdate();
		conn.close();
	}
	
	public void save(Campaign campaign) throws Exception {
		if (campaign == null)
			throw new Exception("The param campaign cannot be null");
		if (campaign.getName() == null || campaign.getName().trim().isEmpty())
			throw new Exception("The param name cannot be null");
		if (campaign.getStartDate() == null)
			throw new Exception("The param startDate cannot be null");
		String sql = "update Campaigns set name=?, keyword=?, start_date=?, end_date=? where id=? and canceled=0";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, campaign.getName());
		stmt.setString(2, campaign.getKeyWord());
		stmt.setDate(3, new java.sql.Date(campaign.getStartDate().getTime()));
		stmt.setDate(4, campaign.getEndDate() == null ? null : new java.sql.Date(campaign.getEndDate().getTime()));
		stmt.setLong(5, campaign.getId());
		stmt.executeUpdate();
		stmt.close();
		conn.close();
	}
	
	public Campaign get(long id) throws Exception {
		String sql = "select name, keyword, start_date, end_date from Campaigns where id=? and canceled=0";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, id);
		ResultSet set = stmt.executeQuery();
		if (!set.next())
			return null;
		String name = set.getString(1);
		String keyword = set.getString(2);
		java.sql.Date startDateSql = set.getDate(3);
		java.sql.Date endDateSql = set.getDate(4);
		Date startDate = startDateSql == null ? null : new Date(startDateSql.getTime());
		Date endDate = endDateSql == null ? null : new Date(endDateSql.getTime());
		Campaign result = new CampaignBean();
		result.setName(name);
		result.setKeyWord(keyword);
		result.setStartDate(startDate);
		result.setEndDate(endDate);
		conn.close();
		return result;
	}
	
	public List<Campaign> all() throws Exception {
		String sql = "select id, name, keyword, start_date, end_date from Campaigns where canceled=0";
		Connection conn = DataService.createConnection();
		Statement stmt = conn.createStatement();
		ResultSet set = stmt.executeQuery(sql);
		List<Campaign> list = new ArrayList<>();
		while (set.next()) {
			long id = set.getLong(1);
			String name = set.getString(2);
			String keyword = set.getString(3);
			java.sql.Date startDateSql = set.getDate(4);
			java.sql.Date endDateSql = set.getDate(5);
			Date startDate = startDateSql == null ? null : new Date(startDateSql.getTime());
			Date endDate = endDateSql == null ? null : new Date(endDateSql.getTime());
			Campaign campaign = new CampaignBean();
			campaign.setId(id);
			campaign.setName(name);
			campaign.setKeyWord(keyword);
			campaign.setStartDate(startDate);
			campaign.setEndDate(endDate);
			list.add(campaign);
		}
		conn.close();
		return list;
	}

}
