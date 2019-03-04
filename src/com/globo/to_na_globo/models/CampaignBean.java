package com.globo.to_na_globo.models;

import java.util.Date;

public class CampaignBean implements Campaign {
	
	private long id;
	private String name;
	private String keyWord;
	private Date startDate;
	private Date endDate;

	@Override
	public int compareTo(Campaign o) {
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
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getStartDate() {
		return this.startDate;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getEndDate() {
		return this.endDate;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Override
	public String getKeyWord() {
		return this.keyWord;
	}
	
	@Override
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

}
