package com.ferguson.cs.product.stream.participation.engine.test.effects;

import static com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities.nullSafeStream;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.ParticipationTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.PricebookCost;

import lombok.RequiredArgsConstructor;

/**
 * Verify that calculated discounts effects e.g. price changes are correct.
 */
@RequiredArgsConstructor
public class CalculatedDiscountsV1TestEffectLifecycle implements ParticipationTestEffectLifecycle {
	private final ParticipationTestUtilities participationTestUtilities;

	/**
	 * Verify any calculated discounts in fixture were published to SQL.
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		if (!fixture.getContentType().equals(ParticipationContentType.PARTICIPATION_V1)) {
			return;
		}

		List<ParticipationCalculatedDiscount> discountsFromFixture = nullSafeStream(fixture.getCalculatedDiscountFixtures())
				.map(fixtureDiscount -> fixtureDiscount.toParticipationCalculatedDiscount(fixture.getParticipationId()))
				.collect(Collectors.toList());
		List<ParticipationCalculatedDiscount> discountsFromDb = participationTestUtilities
				.getParticipationCalculatedDiscounts(fixture.getParticipationId());
		Assertions.assertThat(discountsFromDb).containsExactlyInAnyOrderElementsOf(discountsFromFixture);
	}

	/**
	 * Verify no pricebook_cost rows have this participationId.
	 */
	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		if (!fixture.getContentType().equals(ParticipationContentType.PARTICIPATION_V1)) {
			return;
		}

		Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
				.as("Unexpected participation id in pricebook_cost record: " + fixture)
				.isEqualTo(0);
	}

	/**
	 * Verify any discounts were applied. Check the cost, basePrice, userId, and participationId columns for each
	 * pricebook price. Also verify that any expected Was prices match actual pricebook wasPrices.
	 */
	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		if (!fixture.getContentType().equals(ParticipationContentType.PARTICIPATION_V1)) {
			return;
		}

		List<ParticipationCalculatedDiscount> discountsFromFixture = nullSafeStream(fixture.getCalculatedDiscountFixtures())
				.map(fixtureDiscount -> fixtureDiscount.toParticipationCalculatedDiscount(fixture.getParticipationId()))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(discountsFromFixture)) {
			return;
		}

		List<Integer> expectedUniqueIds = ParticipationTestLifecycle.getExpectedUniqueIds(fixture);

		// Verify the number of discounted prices is count(pricebookIds) * count(expectedUniqueIds).
		Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
				.isEqualTo(discountsFromFixture.size() * expectedUniqueIds.size());

		// Verify that pricebook_cost record values are correct for each pricebook discount.
		discountsFromFixture.forEach(discount -> {
			List<PricebookCost> pricebookCosts = participationTestUtilities.getPricebookCostsInOrder(
					expectedUniqueIds, Collections.singletonList(discount.getPricebookId()));
			pricebookCosts.forEach(pbcost -> {
				Assertions.assertThat(pbcost.getUserId()).isEqualTo(fixture.getLastModifiedUserId());
				Assertions.assertThat(pbcost.getParticipationId()).isEqualTo(fixture.getParticipationId());
				Assertions.assertThat(pbcost.getBasePrice()).isNotEqualTo(0);

				Double expectedCost = discount.getIsPercent()
						? Math.round(100.0 * discount.getChangeValue() * pbcost.getBasePrice()) / 100.0
						: discount.getChangeValue() + pbcost.getBasePrice();
				Assertions.assertThat(pbcost.getCost()).isEqualTo(expectedCost);

				if (!CollectionUtils.isEmpty(fixture.getExpectedWasPrices())
						&& fixture.getExpectedWasPrices().containsKey(pbcost.getUniqueId())) {
					Assertions.assertThat(pbcost.getWasPrice()).isEqualTo(
							fixture.getExpectedWasPrices().get(pbcost.getUniqueId()).getWasPrice());
				}
			});
		});
	}

	/**
	 * Verify the discounts have been removed, and pricebook_cost values indicate it either
	 * reverted to another participation or have expected values.
	 * Verify all prices that were discounted are in the lastOnSale table now.
	 * Verify pending basePrice updates from latestBasePrice table were applied to pricebook_cost.basePrice.
	 */
	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		if (!fixture.getContentType().equals(ParticipationContentType.PARTICIPATION_V1)) {
			return;
		}

		List<ParticipationCalculatedDiscount> discountsFromFixture = nullSafeStream(fixture.getCalculatedDiscountFixtures()).map(
				fixtureDiscount -> fixtureDiscount.toParticipationCalculatedDiscount(fixture.getParticipationId())).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(discountsFromFixture)) {
			return;
		}

		List<Integer> expectedUniqueIds = ParticipationTestLifecycle.getExpectedUniqueIds(fixture);

		// Verify that this Participation now owns no discounted prices.
		Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
				.isEqualTo(0);

		// Verify that pricebook_cost record values are correct for each pricebook discount.
		discountsFromFixture.forEach(discount -> {
			List<PricebookCost> pricebookCosts = participationTestUtilities.getPricebookCostsInOrder(
					expectedUniqueIds, Collections.singletonList(discount.getPricebookId()));
			pricebookCosts.forEach(pbcost -> {
				Assertions.assertThat(pbcost.getUserId()).isEqualTo(fixture.getLastModifiedUserId());
				Assertions.assertThat(pbcost.getBasePrice()).isNotEqualTo(0);

				// If no Participation owns this product now, then verify it was taken off discount correctly,
				// else the lifecycle tests for the new owning Participation will check the values so don't check here.
				if (pbcost.getParticipationId() == 0) {
					Assertions.assertThat(pbcost.getCost()).isEqualTo(pbcost.getBasePrice());
					Assertions.assertThat(pbcost.getWasPrice()).isEqualTo(0);
				}
			});
		});
	}
}
