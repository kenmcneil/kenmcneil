package com.ferguson.cs.product.task.omnipriceharmonization.model;

import java.io.Serializable;

public class PriceHarmonizationData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String mpid;
	private Integer uniqueId;
	private String dg;
	private Double pc24;
	private Double masterList;
	private Double imap;

	public String getMpid() {
		return mpid;
	}

	public void setMpid(String mpid) {
		this.mpid = mpid;
	}

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getDg() {
		return dg;
	}

	public void setDg(String dg) {
		this.dg = dg;
	}

	public Double getPc24() {
		return pc24;
	}

	public void setPc24(Double pc24) {
		this.pc24 = pc24;
	}

	public Double getMasterList() {
		return masterList;
	}

	public void setMasterList(Double masterList) {
		this.masterList = masterList;
	}

	public Double getImap() {
		return imap;
	}

	public void setImap(Double imap) {
		this.imap = imap;
	}
}
