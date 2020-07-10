package com.ferguson.cs.product.stream.participation.engine.test.effects;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.ParticipationTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.PricebookCost;

import lombok.RequiredArgsConstructor;

/**
 * Verify that itemized discounts effects e.g. price changes are correct.
 */
@RequiredArgsConstructor
public class ItemizedDiscountsTestEffectLifecycle implements ParticipationTestEffectLifecycle {
	private final ParticipationTestUtilities participationTestUtilities;

	/**
	 * Verify any itemized discounts in fixture were published to SQL.
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		List<ParticipationItemizedDiscount> discountsFromFixture = fixture.getItemizedDiscountFixtures().stream()
				.map(itemizedDiscountFixture -> itemizedDiscountFixture.toParticipationItemizedDiscounts(fixture.getParticipationId()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());

		List<ParticipationItemizedDiscount> discountsFromDb = participationTestUtilities
				.getParticipationItemizedDiscounts(fixture.getParticipationId());

		Assertions.assertThat(discountsFromDb).containsExactlyInAnyOrderElementsOf(discountsFromFixture);
	}

	/**
	 * Verify no pricebook_cost rows have this participationId.
	 */
	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
				.as("Unexpected participation id in pricebook_cost record: " + fixture)
				.isEqualTo(0);
	}

	/**
	 * Verify any discounts were applied. Check the cost, basePrice, userId, and participationId columns for each pricebook price.
	 */
	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		List<ParticipationItemizedDiscount> discountsFromFixture = fixture.getItemizedDiscountFixtures().stream()
				.map(itemizedDiscountFixture -> itemizedDiscountFixture.toParticipationItemizedDiscounts(fixture.getParticipationId()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(discountsFromFixture)) {
			List<Integer> expectedUniqueIds = ParticipationTestLifecycle.getExpectedUniqueIds(fixture);

			// Verify the number of discounted prices is count(pricebookIds) * count(expectedUniqueIds).
			Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
					.isEqualTo(discountsFromFixture.size() * expectedUniqueIds.size());

			// Verify that pricebook_cost record values are correct for each pricebook discount.
			discountsFromFixture.forEach(discount -> {
				List<PricebookCost> pricebookCosts = participationTestUtilities.getPricebookCosts(
						expectedUniqueIds, Collections.singletonList(discount.getPricebookId()));
				pricebookCosts.forEach(pbcost -> {
					Assertions.assertThat(pbcost.getUserId()).isEqualTo(fixture.getLastModifiedUserId());
					Assertions.assertThat(pbcost.getParticipationId()).isEqualTo(fixture.getParticipationId());
					Assertions.assertThat(pbcost.getBasePrice()).isNotEqualTo(0);
					Double expectedCost = discount.getPrice();
					Assertions.assertThat(pbcost.getCost()).isEqualTo(expectedCost);
				});
			});
		}
	}

	/**
	 * Verify the discounts have been removed, and pricebook_cost values indicate it either
	 * reverted to another participation or have expected values.
	 * Verify all prices that were discounted are in the lastOnSale table now.
	 * Verify pending basePrice updates from latestBasePrice table were applied to pricebook_cost.basePrice.
	 */
	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		List<ParticipationItemizedDiscount> discountsFromFixture = fixture.getItemizedDiscountFixtures().stream()
				.map(itemizedDiscountFixture -> itemizedDiscountFixture.toParticipationItemizedDiscounts(fixture.getParticipationId()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(discountsFromFixture)) {
			List<Integer> expectedUniqueIds = ParticipationTestLifecycle.getExpectedUniqueIds(fixture);

			// Verify the number of discounted prices is count(pricebookIds) * count(expectedUniqueIds).
			Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
					.isEqualTo(0);

			// Verify that pricebook_cost record values are correct for each pricebook discount.
			discountsFromFixture.forEach(discount -> {
				List<PricebookCost> pricebookCosts = participationTestUtilities.getPricebookCosts(
						expectedUniqueIds, Collections.singletonList(discount.getPricebookId()));
				pricebookCosts.forEach(pbcost -> {
					Assertions.assertThat(pbcost.getUserId()).isEqualTo(fixture.getLastModifiedUserId());
					Assertions.assertThat(pbcost.getParticipationId()).isEqualTo(0);
					Assertions.assertThat(pbcost.getBasePrice()).isNotEqualTo(0);
					Assertions.assertThat(pbcost.getCost()).isEqualTo(pbcost.getBasePrice());
				});
			});
		}
	}
}
