package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.ArrayList;
import java.util.List;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an itemized discount, where the price field is a double representing the new sale price of a product at
 * a certain pricebook id. This is the exact value entered by the Participation author.
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
	 * pb22DiscountPrice) to 2 rows in the SQL format (participationId, uniqueId, pricebookId, discountePrice)
	 */
	public List<ParticipationItemizedDiscount> toParticipationItemizedDiscounts(Integer participationId) {
		List<ParticipationItemizedDiscount> discounts = new ArrayList<>();
		discounts.add(new ParticipationItemizedDiscount(
				participationId,
				uniqueId,
				1,
				pricebook1Price));
		discounts.add(new ParticipationItemizedDiscount(
				participationId,
				uniqueId,
				22,
				pricebook22Price));
		return discounts;
	}
}
