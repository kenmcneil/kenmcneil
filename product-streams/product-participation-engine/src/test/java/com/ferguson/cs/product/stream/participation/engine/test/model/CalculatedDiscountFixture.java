package com.ferguson.cs.product.stream.participation.engine.test.model;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a calculated discount, where the discount amount is a positive integer and represents
 * either the percent to lower the price, or a dollar amount to subtract from the price.
 * This represents the values entered by the Participation author.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculatedDiscountFixture {
	private Integer pricebookId;
	private Integer discountAmount;
	private Boolean isPercent;
	private Integer templateId;

	/**
	 * Convert author-valued fixture to values ready to insert into SQL. The participationId
	 * param may be null; if so it will be filled in before inserting into SQL.
	 */
	public ParticipationCalculatedDiscount toParticipationCalculatedDiscount(Integer participationId) {
		return new ParticipationCalculatedDiscount(
				participationId,
				pricebookId,
				isPercent ? (100 - discountAmount) / 100.0 : -1.0 * discountAmount,
				isPercent,
				templateId
		);
	}
}
