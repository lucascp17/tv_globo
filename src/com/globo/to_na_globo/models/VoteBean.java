package com.globo.to_na_globo.models;

import java.util.Date;

public class VoteBean implements Vote {
	
	private long id;
	private long owner;
	private long choice;
	private Date time;
	private String timeSignature;

	@Override
	public int compareTo(Vote o) {
		long diff = this.getId() - o.getId();
		if (diff < 0)
			return -1;
		else if (diff > 0)
			return 1;
		else
			return 0;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public long getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(long campaignId) {
		this.owner = campaignId;
	}

	@Override
	public long getChoice() {
		return this.choice;
	}

	@Override
	public void setChoice(long voteItemId) {
		this.choice = voteItemId;
	}

	@Override
	public Date getTime() {
		return this.time;
	}

	@Override
	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String getTimeSignature() {
		return this.timeSignature;
	}

	@Override
	public void setTimeSignature(String signature) {
		this.timeSignature = signature;
	}

}
