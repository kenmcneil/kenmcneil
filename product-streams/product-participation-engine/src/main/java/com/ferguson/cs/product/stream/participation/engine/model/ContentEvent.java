package com.ferguson.cs.product.stream.participation.engine.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ContentEvent implements Serializable {
	private static final long serialVersionUID = 3L;

	@Id
	private String id;

	// what, who, when
	private ContentEventType eventType;
	private Integer lastModifiedUserId;
	private Date lastModifiedDate;

	// embed the record pertaining to this event
	private ParticipationItem participationItem;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ContentEventType getEventType() {
		return eventType;
	}

	public void setEventType(ContentEventType eventType) {
		this.eventType = eventType;
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

	public ParticipationItem getParticipationItem() {
		return participationItem;
	}

	public void setParticipationItem(ParticipationItem participationItem) {
		this.participationItem = participationItem;
	}
}
