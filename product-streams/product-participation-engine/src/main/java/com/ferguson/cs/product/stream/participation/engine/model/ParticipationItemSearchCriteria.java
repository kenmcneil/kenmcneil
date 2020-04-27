package com.ferguson.cs.product.stream.participation.engine.model;

import java.util.Date;
import java.util.Set;

/**
 * Used to pass criteria around for queries to the participationItem collection in MongoDB.
 */
public class ParticipationItemSearchCriteria extends SortedPagedSearchCriteria<ParticipationItemSortColumn> {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer sourceParticipationItemId;
	private Integer targetParticipationItemId;
	private Integer saleId;
	private String description;
	private Set<ParticipationItemStatus> statuses;
	private Set<Integer> starringUserIds;
	private Integer lastModifiedUserId;
	private ParticipationItemUpdateStatus updateStatus;
	private Date scheduledOn;
	private Boolean isExpired;

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(Boolean expired) {
		isExpired = expired;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSourceParticipationItemId() {
		return sourceParticipationItemId;
	}

	public void setSourceParticipationItemId(Integer sourceParticipationItemId) {
		this.sourceParticipationItemId = sourceParticipationItemId;
	}

	public Integer getTargetParticipationItemId() {
		return targetParticipationItemId;
	}

	public void setTargetParticipationItemId(Integer targetParticipationItemId) {
		this.targetParticipationItemId = targetParticipationItemId;
	}

	public Integer getSaleId() {
		return saleId;
	}

	public void setSaleId(Integer saleId) {
		this.saleId = saleId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<ParticipationItemStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(Set<ParticipationItemStatus> statuses) {
		this.statuses = statuses;
	}

	public Set<Integer> getStarringUserIds() {
		return starringUserIds;
	}

	public void setStarringUserIds(Set<Integer> starringUserIds) {
		this.starringUserIds = starringUserIds;
	}

	public Integer getLastModifiedUserId() {
		return lastModifiedUserId;
	}

	public void setLastModifiedUserId(Integer lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	public ParticipationItemUpdateStatus getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(ParticipationItemUpdateStatus updateStatus) {
		this.updateStatus = updateStatus;
	}

	public Date getScheduledOn() {
		return scheduledOn;
	}

	public void setScheduledOn(Date scheduledOn) {
		this.scheduledOn = scheduledOn;
	}
}
