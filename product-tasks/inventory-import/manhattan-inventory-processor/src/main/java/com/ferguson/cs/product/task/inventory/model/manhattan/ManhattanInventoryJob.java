package com.ferguson.cs.product.task.inventory.model.manhattan;

import java.io.Serializable;
import java.util.Date;

public class ManhattanInventoryJob implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String transactionNumber;
	private Integer totalCount;
	private Integer currentCount;
	private ManhattanInventoryJobStatus manhattanInventoryJobStatus;
	private Date createdDateTime;
	private ManhattanChannel manhattanChannel;

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

	public Integer getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(Integer currentCount) {
		this.currentCount = currentCount;
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
}
