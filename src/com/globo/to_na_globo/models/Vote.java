package com.globo.to_na_globo.models;

import java.util.Date;

public interface Vote extends Comparable<Vote> {
	
	long getId();
	void setId(long id);
	
	long getOwner();
	void setOwner(long campaignId);
	
	long getChoice();
	void setChoice(long voteItemId);
	
	Date getTime();
	void setTime(Date time);
	
	String getTimeSignature();
	void setTimeSignature(String signature);

}
