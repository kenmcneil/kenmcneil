package com.ferguson.cs.product.stream.participation.engine.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This is the domain class for the source-of-truth for Participation records,
 * owned by Construct. Participation records are created and managed in Construct
 * and saved as ParticipationItem documents.
 */
@Document
public class ParticipationItem {
	@Id
	private Integer id;

	private Integer sourceParticipationItemId;
	private Integer targetParticipationItemId;
	private Integer saleId;
	private String description;
	private List<Integer> productUniqueIds; // to maintain insertion order
	private ParticipationItemSchedule schedule;
	private ParticipationItemStatus status;
	private Set<Integer> starringUserIds;
	private Integer lastModifiedUserId;
	private Date lastModifiedDate;
	private Date displayEndDate;
	private Set<Integer> deletedProductUniqueIds;
	private ParticipationItemUpdateStatus updateStatus;
	private Map<String, Object> content;

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

	public List<Integer> getProductUniqueIds() {
		return productUniqueIds;
	}

	public void setProductUniqueIds(List<Integer> productUniqueIds) {
		this.productUniqueIds = productUniqueIds;
	}

	public ParticipationItemSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(ParticipationItemSchedule schedule) {
		this.schedule = schedule;
	}

	public ParticipationItemStatus getStatus() {
		return status;
	}

	public void setStatus(ParticipationItemStatus status) {
		this.status = status;
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

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Date getDisplayEndDate() {
		return displayEndDate;
	}

	public void setDisplayEndDate(Date displayEndDate) {
		this.displayEndDate = displayEndDate;
	}

	public Set<Integer> getDeletedProductUniqueIds() {
		return deletedProductUniqueIds;
	}

	public void setDeletedProductUniqueIds(Set<Integer> deletedProductUniqueIds) {
		this.deletedProductUniqueIds = deletedProductUniqueIds;
	}

	public ParticipationItemUpdateStatus getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(ParticipationItemUpdateStatus updateStatus) {
		this.updateStatus = updateStatus;
	}

	public Map<String, Object> getContent() {
		return content;
	}

	public void setContent(Map<String, Object> content) {
		this.content = content;
	}
}
