package com.ferguson.cs.product.task.inventory.model.manhattan;

import java.io.Serializable;
import java.util.Date;

public class ManhattanInventoryJob implements Serializable {

	private static final long serialVersionUID = 2L;

	private Integer id;
	private String transactionNumber;
	private Integer totalCount;
	private ManhattanInventoryJobStatus manhattanInventoryJobStatus;
	private Date createdDateTime;
	private ManhattanChannel manhattanChannel;
	private Boolean dataIsComplete;

	public ManhattanInventoryJob() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public ManhattanInventoryJobStatus getManhattanInventoryJobStatus() {
		return manhattanInventoryJobStatus;
	}

	public void setManhattanInventoryJobStatus(ManhattanInventoryJobStatus manhattanInventoryJobStatus) {
		this.manhattanInventoryJobStatus = manhattanInventoryJobStatus;
	}

	public ManhattanChannel getManhattanChannel() {
		return manhattanChannel;
	}

	public void setManhattanChannel(ManhattanChannel manhattanChannel) {
		this.manhattanChannel = manhattanChannel;
	}

	public Boolean getDataIsComplete() {
		return dataIsComplete;
	}

	public void setDataIsComplete(Boolean dataIsComplete) {
		this.dataIsComplete = dataIsComplete;
	}
}
