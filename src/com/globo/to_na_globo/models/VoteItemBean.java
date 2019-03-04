package com.globo.to_na_globo.models;

public class VoteItemBean implements VoteItem {
	
	private long id;
	private long owner;
	private String keyWord;

	@Override
	public int compareTo(VoteItem o) {
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
	public String getKeyWord() {
		return this.keyWord;
	}
	
	@Override
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	@Override
	public long getOwner() {
		return owner;
	}

	@Override
	public void setOwner(long id) {
		this.owner = id;
	}

}
