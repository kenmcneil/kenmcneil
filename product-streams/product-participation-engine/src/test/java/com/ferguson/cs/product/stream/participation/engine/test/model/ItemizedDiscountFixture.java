package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.Arrays;
import java.util.List;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an itemized discount, where the price field is a double representing the new sale price of a product at
 * a certain pricebook id. This is the exact value entered by the Participation author.
 *
 * This fixture is used for both v1 and v2 type of itemized discounts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemizedDiscountFixture {
	private Integer uniqueId;
	private Double pricebook1Price;
	private Double pricebook22Price;

	/**
	 * Converts a single row of itemized discounts reduced from the mongo format (uniqueId, pb1DiscountPriced,
	 * pb22DiscountPrice) to 2 rows in the SQL format (participationId, uniqueId, pricebookId, discountPrice).
	 *
	 * In v2 itemized discounts the pb1 price is used for the pb22 price so they match; a null indicates that it's
	 * a v2 itemized discount.
	 */
	public List<ParticipationItemizedDiscount> toParticipationItemizedDiscounts(Integer participationId) {
		return Arrays.asList(
				new ParticipationItemizedDiscount(participationId, uniqueId, 1, pricebook1Price),
				new ParticipationItemizedDiscount(participationId, uniqueId, 22,
						pricebook22Price == null ? pricebook1Price : pricebook22Price)
		);
	}
}
