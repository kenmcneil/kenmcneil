package com.ferguson.cs.product.task.inventory.model.manhattan;

import java.io.Serializable;
import java.util.Date;

public class ManhattanIntakeJob implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String transactionNumber;
	private Integer totalCount;
	private Integer currentCount;
	private ManhattanIntakeJobStatus manhattanIntakeJobStatus;
	private Date createdDateTime;

	public ManhattanIntakeJob() {
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

	public ManhattanIntakeJobStatus getManhattanIntakeJobStatus() {
		return manhattanIntakeJobStatus;
	}

	public void setManhattanIntakeJobStatus(ManhattanIntakeJobStatus manhattanIntakeJobStatus) {
		this.manhattanIntakeJobStatus = manhattanIntakeJobStatus;
	}
}
