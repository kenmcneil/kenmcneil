package com.ferguson.cs.product.stream.participation.engine.test.effects;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.ParticipationTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ItemizedDiscountFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.PricebookCost;

import lombok.RequiredArgsConstructor;

/**
 * Verify that V1 itemized discounts effects e.g. price changes are correct.
 */
@RequiredArgsConstructor
public class ItemizedDiscountsV1TestEffectLifecycle implements ParticipationTestEffectLifecycle {
	private final ParticipationTestUtilities participationTestUtilities;

	/**
	 * Verify any itemized discounts in fixture were published to SQL.
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		if (!ParticipationContentType.PARTICIPATION_ITEMIZED_V1.equals(fixture.getContentType())) {
			return;
		}

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
		if (!ParticipationContentType.PARTICIPATION_ITEMIZED_V1.equals(fixture.getContentType())) {
			return;
		}

		Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
				.as("Unexpected participation id in pricebook_cost record: " + fixture)
				.isEqualTo(0);
	}

	/**
	 * Verify any discounts were applied. Check values for each pricebook price.
	 */
	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		if (!ParticipationContentType.PARTICIPATION_ITEMIZED_V1.equals(fixture.getContentType())) {
			return;
		}

		List<ParticipationItemizedDiscount> discountsFromFixture = fixture.getItemizedDiscountFixtures().stream()
				.map(itemizedDiscountFixture -> itemizedDiscountFixture.toParticipationItemizedDiscounts(fixture.getParticipationId()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(discountsFromFixture)) {
			return;
		}

		List<Integer> expectedUniqueIds = ParticipationTestLifecycle.getExpectedUniqueIds(fixture);

		// Verify the number of discounted prices is (2 pricebooks) * count(expectedUniqueIds), which is
		// the same as the size of fixture.itemizedDiscountFixtures.
		Assertions.assertThat(participationTestUtilities.getPricebookCostParticipationCount(fixture.getParticipationId()))
				.isEqualTo(discountsFromFixture.size());

		List<PricebookCost> pricebookCostsPb1 = participationTestUtilities.getPricebookCostsInOrder(expectedUniqueIds,
				Collections.singletonList(1));
		List<PricebookCost> pricebookCostsPb22 = participationTestUtilities.getPricebookCostsInOrder(expectedUniqueIds,
				Collections.singletonList(22));
		Assertions.assertThat(pricebookCostsPb1.size()).isEqualTo(expectedUniqueIds.size());
		Assertions.assertThat(pricebookCostsPb22.size()).isEqualTo(expectedUniqueIds.size());

		Map<Integer, ItemizedDiscountFixture> discountFixturesByUniqueId = fixture.getItemizedDiscountFixtures().stream()
				.collect(Collectors.toMap(ItemizedDiscountFixture::getUniqueId, discountFixture -> discountFixture));

		// Verify that pricebook_cost record values are correct for each pricebook discount.
		for (int i = 0; i < pricebookCostsPb1.size(); i++) {
			PricebookCost pbcost1 = pricebookCostsPb1.get(i);
			PricebookCost pbcost22 = pricebookCostsPb22.get(i);

			Assertions.assertThat(pbcost1.getUserId()).isEqualTo(fixture.getLastModifiedUserId());
			Assertions.assertThat(pbcost1.getParticipationId()).isEqualTo(fixture.getParticipationId());
			Assertions.assertThat(pbcost1.getBasePrice()).isNotEqualTo(0);
			Assertions.assertThat(pbcost1.getCost()).isEqualTo(
					discountFixturesByUniqueId.get(pbcost1.getUniqueId()).getPricebook1Price()
			);

			// PB22 price values should match PB1 except for the cost.
			Assertions.assertThat(pbcost22.getUniqueId()).isEqualTo(pbcost1.getUniqueId());
			Assertions.assertThat(pbcost22.getUserId()).isEqualTo(pbcost1.getUserId());
			Assertions.assertThat(pbcost22.getParticipationId()).isEqualTo(pbcost1.getParticipationId());
			Assertions.assertThat(pbcost22.getWasPrice()).isEqualTo(pbcost1.getWasPrice());
			Assertions.assertThat(pbcost22.getBasePrice()).isNotEqualTo(0);
			Assertions.assertThat(pbcost22.getCost()).isEqualTo(
					discountFixturesByUniqueId.get(pbcost22.getUniqueId()).getPricebook22Price()
			);

			if (!CollectionUtils.isEmpty(fixture.getExpectedWasPrices())
					&& fixture.getExpectedWasPrices().containsKey(pbcost1.getUniqueId())) {
				double expectedWasPrice = fixture.getExpectedWasPrices().get(pbcost1.getUniqueId()).getWasPrice();
				Assertions.assertThat(pbcost1.getWasPrice()).isEqualTo(expectedWasPrice);
				Assertions.assertThat(pbcost22.getWasPrice()).isEqualTo(expectedWasPrice);
			}
		}
	}

	/**
	 * Verify the discounts have been removed, and pricebook_cost values indicate it either
	 * reverted to another participation or have expected values.
	 * Verify pending basePrice updates from latestBasePrice table were applied to pricebook_cost.basePrice.
	 * TODO Verify all prices that were discounted are in the lastOnSale table now.
	 */
	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		if (!ParticipationContentType.PARTICIPATION_ITEMIZED_V1.equals(fixture.getContentType())) {
			return;
		}

		List<ParticipationItemizedDiscount> discountsFromFixture = fixture.getItemizedDiscountFixtures().stream()
				.map(itemizedDiscountFixture -> itemizedDiscountFixture.toParticipationItemizedDiscounts(fixture.getParticipationId()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
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
				Assertions.assertThat(pbcost.getParticipationId()).isEqualTo(0);
				Assertions.assertThat(pbcost.getBasePrice()).isNotEqualTo(0);
				Assertions.assertThat(pbcost.getCost()).isEqualTo(pbcost.getBasePrice());
			});
		});
	}
}
