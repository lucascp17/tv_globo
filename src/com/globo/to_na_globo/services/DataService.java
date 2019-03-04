package com.globo.to_na_globo.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.globo.to_na_globo.models.Campaign;
import com.globo.to_na_globo.models.CampaignBean;
import com.globo.to_na_globo.models.VoteItem;
import com.globo.to_na_globo.models.VoteItemBean;

public class DataService {
	
	static {
		install();
	}
	
	private static void install() {
		System.out.println("Installing database...");
		StringBuilder sqlBuilder;
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("create table Campaigns(");
		sqlBuilder.append("id bigint not null primary key generated always as identity (start with 1, increment by 1),");
		sqlBuilder.append("name varchar(255) not null,");
		sqlBuilder.append("keyword varchar(255) not null,");
		sqlBuilder.append("start_date timestamp not null,");
		sqlBuilder.append("end_date timestamp not null,");
		sqlBuilder.append("canceled smallint not null default 0");
		sqlBuilder.append(")");
		String sql0 = sqlBuilder.toString();
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("create table VoteItems(");
		sqlBuilder.append("id bigint not null primary key generated always as identity (start with 1, increment by 1),");
		sqlBuilder.append("owner bigint not null references Campaigns(id),");
		sqlBuilder.append("keyword varchar(255) not null,");
		sqlBuilder.append("votes bigint not null default 0");
		sqlBuilder.append(")");
		String sql1 = sqlBuilder.toString();
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("create table Votes(");
		sqlBuilder.append("id bigint not null primary key generated always as identity (start with 1, increment by 1),");
		sqlBuilder.append("owner bigint not null references Campaigns(id),");
		sqlBuilder.append("choice bigint not null references VoteItems(id),");
		sqlBuilder.append("time_value timestamp not null,");
		sqlBuilder.append("time_signature varchar(15) not null");
		sqlBuilder.append(")");
		String sql2 = sqlBuilder.toString();
		try {
			Connection conn = createConnectionFirstTime();
			Statement stmt = conn.createStatement();
			stmt.addBatch(sql0);
			stmt.addBatch(sql1);
			stmt.addBatch(sql2);
			stmt.executeBatch();
			conn.close();
			System.out.println("Database installed successfully!");
		} catch (Exception exc) {
			System.out.println("Database installation failed! Probably it's already installed. Trying to recover services...");
			recoverServices();
		}
	}
	
	private static void recoverServices() {
		try {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			String sql0 = "select id, name, keyword, start_date, end_date from Campaigns where canceled = 0 and start_date < ? and (end_date is null or end_date > ?)";
			Connection conn = createConnection();
			PreparedStatement stmt0 = conn.prepareStatement(sql0);
			stmt0.setTimestamp(1, now);
			stmt0.setTimestamp(2, now);
			ResultSet set0 = stmt0.executeQuery();
			while (set0.next()) {
				Campaign campaign = new CampaignBean();
				campaign.setId(set0.getLong(1));
				campaign.setName(set0.getString(2));
				campaign.setKeyWord(set0.getString(3));
				campaign.setStartDate(new Date(set0.getTimestamp(4).getTime()));
				campaign.setEndDate(new Date(set0.getTimestamp(5).getTime()));
				String sql1 = "select id, owner, keyword, votes from VoteItems where owner = ?";
				PreparedStatement stmt1 = conn.prepareStatement(sql1);
				stmt1.setLong(1, campaign.getId());
				ResultSet set1 = stmt1.executeQuery();
				List<VoteItem> itemsList = new ArrayList<>();
				while (set1.next()) {
					VoteItem item = new VoteItemBean();
					item.setId(set1.getLong(1));
					item.setOwner(set1.getLong(2));
					item.setKeyWord(set1.getString(3));
					item.setVotes(set1.getLong(4));
					itemsList.add(item);
				}
				VoteItem[] itemsArray = itemsList.toArray(new VoteItem[0]);
				TwitterService.instance.scheduleService(campaign, itemsArray);
			}
			System.out.println("Services recovered successfully");
		} catch (Exception exc) {
			System.out.println("Services could not be recovered. Database is not working!");
		}
	}
	
	public static Connection createConnection() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		Connection connection = DriverManager.getConnection("jdbc:derby:campaigns");
		return connection;
	}
	
	public static Connection createConnectionFirstTime() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		Connection connection = DriverManager.getConnection("jdbc:derby:campaigns;create=true");
		return connection;
	}

}
