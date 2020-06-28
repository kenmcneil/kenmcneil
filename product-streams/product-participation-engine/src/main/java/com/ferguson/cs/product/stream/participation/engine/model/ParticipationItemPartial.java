package com.ferguson.cs.product.stream.participation.engine.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a row in mmc.product.participationItemPartial. The primary
 * key [id] is intentionally omitted since it is never referenced, and to avoid
 * confusing it with the participationId.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationItemPartial {
	private Integer participationId;
	private Integer saleId;
	private Date startDate;
	private Date endDate;
	private Integer lastModifiedUserId;
	private Boolean isActive;
	private Integer contentTypeId;
	private Boolean isCoupon;
	private Boolean shouldBlockDynamicPricing;
}
