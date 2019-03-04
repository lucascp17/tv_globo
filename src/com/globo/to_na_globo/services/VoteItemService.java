package com.globo.to_na_globo.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.globo.to_na_globo.models.VoteItem;
import com.globo.to_na_globo.models.VoteItemBean;

public class VoteItemService {
	
	public final static VoteItemService instance;
	
	static {
		instance = new VoteItemService();
	}
	
	private VoteItemService() {}
	
	public VoteItem[] create(long campaignId, String[] keywords) throws Exception {
		if (keywords == null)
			throw new Exception("The param keyword cannot be null");
		String sql = "insert into VoteItems(owner, keyword) values (?, ?)";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < keywords.length; ++i) {
			stmt.setLong(1, campaignId);
			stmt.setString(2, keywords[i]);
			stmt.addBatch();
			stmt.clearParameters();
		}
		stmt.executeBatch();
		VoteItem[] items = new VoteItem[keywords.length];
		ResultSet keys = stmt.getGeneratedKeys();
		for (int i = 0; i < keywords.length; ++i) {
			if (!keys.next())
				break;
			VoteItem item = new VoteItemBean();
			item.setId(keys.getLong(1));
			item.setOwner(campaignId);
			item.setKeyWord(keywords[i]);
			items[i] = item;
		}
		conn.close();
		return items;
	}
	
	public List<VoteItem> allByCampaign(long campaignId) throws Exception {
		String sql = "select id, keyword from VoteItems where owner=?";
		Connection conn = DataService.createConnection();
		Statement stmt = conn.createStatement();
		ResultSet set = stmt.executeQuery(sql);
		List<VoteItem> list = new ArrayList<>();
		while (set.next()) {
			long id = set.getLong(1);
			String keyWord = set.getString(2);
			VoteItem bean = new VoteItemBean();
			bean.setId(id);
			bean.setOwner(campaignId);
			bean.setKeyWord(keyWord);
			list.add(bean);
		}
		conn.close();
		return list;
	}

}
