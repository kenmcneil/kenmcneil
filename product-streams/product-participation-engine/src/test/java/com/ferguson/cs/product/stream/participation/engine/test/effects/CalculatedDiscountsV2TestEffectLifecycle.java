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
public class CalculatedDiscountsV2TestEffectLifecycle implements ParticipationTestEffectLifecycle {
	private final ParticipationTestUtilities participationTestUtilities;

	/**
	 * Verify any calculated discounts in fixture were published to SQL.
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		if (!fixture.getContentType().equals(ParticipationContentType.PARTICIPATION_V2)) {
			return;
		}

		List<ParticipationCalculatedDiscount> discountsFromFixture = nullSafeStream(fixture.getCalculatedDiscountFixtures())
				.map(fixtureDiscount -> fixtureDiscount.toParticipationCalculatedDiscount(fixture.getParticipationId()))
				.collect(Collectors.toList());
		List<ParticipationCalculatedDiscount> discountsFromDb = participationTestUtilities
				.getParticipationCalculatedDiscounts(fixture.getParticipationId());

		discountsFromDb.forEach(discount -> {
			// pb1 and pb22 discounts must be the same in v2
			Assertions.assertThat(discount.getParticipationId()).isEqualTo(discountsFromFixture.get(0).getParticipationId());
			Assertions.assertThat(discount.getChangeValue()).isEqualTo(discountsFromFixture.get(0).getChangeValue());
			Assertions.assertThat(discount.getIsPercent()).isEqualTo(discountsFromFixture.get(0).getIsPercent());
			Assertions.assertThat(discount.getTemplateId()).isEqualTo(discountsFromFixture.get(0).getTemplateId());
		});
	}

	/**
	 * Verify no pricebook_cost rows have this participationId.
	 */
	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		if (!fixture.getContentType().equals(ParticipationContentType.PARTICIPATION_V2)) {
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
		if (!fixture.getContentType().equals(ParticipationContentType.PARTICIPATION_V2)) {
			return;
		}

		List<ParticipationCalculatedDiscount> discountsFromFixture = nullSafeStream(fixture.getCalculatedDiscountFixtures())
				.map(fixtureDiscount -> fixtureDiscount.toParticipationCalculatedDiscount(fixture.getParticipationId()))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(discountsFromFixture)) {
			return;
		}

		List<Integer> expectedUniqueIds = ParticipationTestLifecycle.getExpectedUniqueIds(fixture);

		// V2 has a single discount type and amount that is applied to pb 1 to get the discounted price, and
		// the discounted price is then used for pb1 and pb 22.

		// Verify the number of discounted prices is 2 * count(pricebookIds) * count(expectedUniqueIds).
		Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
				.isEqualTo(2 * discountsFromFixture.size() * expectedUniqueIds.size());

		// Verify that pricebook_cost record values are correct for each pricebook.
		List<PricebookCost> pricebookCostsPb1 = participationTestUtilities.getPricebookCostsInOrder(expectedUniqueIds,
				Collections.singletonList(1));
		List<PricebookCost> pricebookCostsPb22 = participationTestUtilities.getPricebookCostsInOrder(expectedUniqueIds,
				Collections.singletonList(22));
		ParticipationCalculatedDiscount discount = discountsFromFixture.get(0);
		for (int i = 0; i < pricebookCostsPb1.size(); i++) {
			PricebookCost pbcost = pricebookCostsPb1.get(i);
			Assertions.assertThat(pbcost.getUserId()).isEqualTo(fixture.getLastModifiedUserId());
			Assertions.assertThat(pbcost.getParticipationId()).isEqualTo(fixture.getParticipationId());
			Assertions.assertThat(pbcost.getBasePrice()).isNotEqualTo(0);

			Double expectedCost = discount.getIsPercent()
					? Math.round(100.0 * discount.getChangeValue() * pbcost.getBasePrice()) / 100.0
					: discount.getChangeValue() + pbcost.getBasePrice();
			Assertions.assertThat(pbcost.getCost()).isEqualTo(expectedCost);

			// PB 22 cost and wasPrice should be the same as PB 1 cost and wasPrice.
			PricebookCost pbcost22 = pricebookCostsPb22.get(i);
			Assertions.assertThat(pbcost22.getCost()).isEqualTo(pbcost.getCost());
			Assertions.assertThat(pbcost22.getWasPrice()).isEqualTo(pbcost.getWasPrice());

			if (!CollectionUtils.isEmpty(fixture.getExpectedWasPrices())
					&& fixture.getExpectedWasPrices().containsKey(pbcost.getUniqueId())) {
				Assertions.assertThat(pbcost.getWasPrice()).isEqualTo(
						fixture.getExpectedWasPrices().get(pbcost.getUniqueId()).getWasPrice());
			}
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
		if (!fixture.getContentType().equals(ParticipationContentType.PARTICIPATION_V2)) {
			return;
		}

		List<ParticipationCalculatedDiscount> discountsFromFixture = nullSafeStream(fixture.getCalculatedDiscountFixtures())
				.map(fixtureDiscount -> fixtureDiscount.toParticipationCalculatedDiscount(fixture.getParticipationId()))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(discountsFromFixture)) {
			return;
		}

		List<Integer> expectedUniqueIds = ParticipationTestLifecycle.getExpectedUniqueIds(fixture);

		// Verify that this Participation now owns no discounted prices.
		Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
				.isEqualTo(0);

		// Verify that now-unowned pricebook_cost values are correct for each pricebook discounted.
		discountsFromFixture.forEach(discount -> {
			List<PricebookCost> pricebookCosts = participationTestUtilities.getPricebookCostsInOrder(
					expectedUniqueIds, Collections.singletonList(discount.getPricebookId()));
			pricebookCosts.forEach(pbcost -> {
				Assertions.assertThat(pbcost.getUserId()).isEqualTo(fixture.getLastModifiedUserId());
				Assertions.assertThat(pbcost.getBasePrice()).isNotEqualTo(0);

				// If no Participation owns this product now, then verify it was taken off discount correctly, else don't
				// check anything here since the lifecycle tests for the new owning Participation will check the values.
				if (pbcost.getParticipationId() == 0) {
					Assertions.assertThat(pbcost.getCost()).isEqualTo(pbcost.getBasePrice());
					Assertions.assertThat(pbcost.getWasPrice()).isEqualTo(0);
				}
			});
		});
	}
}
