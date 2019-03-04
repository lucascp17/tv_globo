package com.globo.to_na_globo.models;

import java.util.Date;

public interface Campaign extends Comparable<Campaign> {
	
	long getId();
	void setId(long id);
	
	String getName();
	void setName(String name);
	
	String getKeyWord();
	void setKeyWord(String keyWord);
	
	Date getStartDate();
	void setStartDate(Date start);
	
	Date getEndDate();
	void setEndDate(Date end);

}
