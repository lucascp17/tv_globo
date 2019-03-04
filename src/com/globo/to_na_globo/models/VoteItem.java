package com.globo.to_na_globo.models;

public interface VoteItem extends Comparable<VoteItem> {
	
	long getId();
	void setId(long id);
	
	long getOwner();
	void setOwner(long id);
	
	String getKeyWord();
	void setKeyWord(String keyWord);
	
	long getVotes();
	void setVotes(long votes);

}
