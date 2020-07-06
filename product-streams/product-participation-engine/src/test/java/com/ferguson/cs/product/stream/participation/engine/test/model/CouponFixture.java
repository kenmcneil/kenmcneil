package com.ferguson.cs.product.stream.participation.engine.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a coupon participation, two flags are attached to the participation saleId and uniqueIds
 * This represents the values entered by the Participation author.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponFixture {
	private Boolean isCoupon;
	private Boolean shouldBlockDynamicPricing;

}
