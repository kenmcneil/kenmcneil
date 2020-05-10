package com.ferguson.cs.product.stream.participation.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents one row from the participationProduct table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationProduct {
	Integer participationId;
	Integer uniqueId;
	Boolean isOwner;
}
