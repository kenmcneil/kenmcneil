package com.ferguson.cs.product.stream.participation.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents one row from the mmc.product.participationProduct table.
 * The primary key [id] is intentionally omitted since it is never referenced,
 * and to avoid confusing it with the participationId.
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
