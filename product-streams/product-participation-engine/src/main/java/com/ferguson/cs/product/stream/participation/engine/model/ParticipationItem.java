package com.ferguson.cs.product.stream.participation.engine.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the domain class for the source-of-truth for Participation records,
 * owned by Construct. Participation records are created and managed in Construct
 * and saved as ParticipationItem documents.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
