package com.ferguson.cs.product.task.inventory.model.manhattan;

import java.io.Serializable;
import java.util.Date;

public class ManhattanInventoryLocationData implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer currentCount;
	private Integer currentItemPageCount;
	private Integer totalItemPageCount;
	private Integer manhattanInventoryJobId;
	private Date createdDateTime;
	private Date modifiedDateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(Integer currentCount) {
		this.currentCount = currentCount;
	}

	public Integer getCurrentItemPageCount() {
		return currentItemPageCount;
	}

	public void setCurrentItemPageCount(Integer currentItemPageCount) {
		this.currentItemPageCount = currentItemPageCount;
	}

	public Integer getTotalItemPageCount() {
		return totalItemPageCount;
	}

	public void setTotalItemPageCount(Integer totalItemPageCount) {
		this.totalItemPageCount = totalItemPageCount;
	}

	public Integer getManhattanInventoryJobId() {
		return manhattanInventoryJobId;
	}

	public void setManhattanInventoryJobId(Integer manhattanInventoryJobId) {
		this.manhattanInventoryJobId = manhattanInventoryJobId;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getModifiedDateTime() {
		return modifiedDateTime;
	}

	public void setModifiedDateTime(Date modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}
}
