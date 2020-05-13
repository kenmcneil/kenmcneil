package com.ferguson.cs.product.stream.participation.engine.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an event relating to a content or Participation record,
 * in Construct's MongoDB database. Events are added to the contentEvent collection
 * to form a log of events. User actions in Construct such as Publish, and engine
 * events such as "Activated" are added to the log. Currently there is a tight
 * coupling between the engine and Construct because the engine directly queries
 * Construct's database.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
