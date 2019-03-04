package com.globo.to_na_globo.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DataService {
	
	static {
		install();
	}
	
	private static void install() {
		System.out.println("Installing database");
		StringBuilder sqlBuilder;
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("create table Campaigns(");
		sqlBuilder.append("id bigint not null primary key generated always as identity (start with 1, increment by 1),");
		sqlBuilder.append("name varchar(255) not null,");
		sqlBuilder.append("keyword varchar(255) not null,");
		sqlBuilder.append("start_date timestamp not null,");
		sqlBuilder.append("end_date timestamp not null,");
		sqlBuilder.append("canceled boolean not null default FALSE");
		sqlBuilder.append(")");
		String sql0 = sqlBuilder.toString();
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("create table VoteItems(");
		sqlBuilder.append("id bigint not null primary key generated always as identity (start with 1, increment by 1),");
		sqlBuilder.append("owner bigint not null references Campaigns(id),");
		sqlBuilder.append("keyword varchar(255) not null");
		sqlBuilder.append(")");
		String sql1 = sqlBuilder.toString();
		try {
			Connection conn = createConnectionFirstTime();
			Statement stmt = conn.createStatement();
			stmt.addBatch(sql0);
			stmt.addBatch(sql1);
			stmt.executeBatch();
			conn.close();
			System.out.println("Database installed successfully!");
		} catch (Exception exc) {
			System.out.println("Database installation failed! Probably it's already installed");
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
