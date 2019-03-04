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
		VoteItem[] items = new VoteItem[keywords.length];
		String sql = "insert into VoteItems(owner, keyword) values (?, ?)";
		Connection conn = DataService.createConnection();
		for (int i = 0; i < keywords.length; ++i) {
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setLong(1, campaignId);
			stmt.setString(2, keywords[i]);
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			keys.next();
			long newId = keys.getLong(1);
			VoteItem item = new VoteItemBean();
			item.setId(newId);
			item.setOwner(campaignId);
			item.setKeyWord(keywords[i]);
			items[i] = item;
		}
		conn.close();
		return items;
	}
	
	public List<VoteItem> allByCampaign(long campaignId) throws Exception {
		String sql = "select id, keyword, votes from VoteItems where owner=?";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, campaignId);
		ResultSet set = stmt.executeQuery();
		List<VoteItem> list = new ArrayList<>();
		while (set.next()) {
			long id = set.getLong(1);
			String keyWord = set.getString(2);
			Long votes = set.getLong(3);
			VoteItem bean = new VoteItemBean();
			bean.setId(id);
			bean.setOwner(campaignId);
			bean.setKeyWord(keyWord);
			bean.setVotes(votes);
			list.add(bean);
		}
		conn.close();
		return list;
	}
	
	public void countVote(long voteItemId) throws Exception {
		String sql = "update VoteItems set votes=votes+1 where id=?";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, voteItemId);
		stmt.executeUpdate();
		conn.close();
	}
	
	public long getVoteCount(long voteItemId) throws Exception {
		String sql = "select votes from VoteItems where id=?";
		Connection conn = DataService.createConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, voteItemId);
		ResultSet set = stmt.executeQuery();
		long result;
		if (set.next())
			result = set.getLong(1);
		else
			result = 0l;
		conn.close();
		return result;
	}

}
