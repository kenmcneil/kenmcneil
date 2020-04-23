package com.ferguson.cs.product.stream.participation.engine.test.model;

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
public class ParticipationProductFixture {
	Integer participationId;
	Integer uniqueId;
	Boolean isOwner;
}
