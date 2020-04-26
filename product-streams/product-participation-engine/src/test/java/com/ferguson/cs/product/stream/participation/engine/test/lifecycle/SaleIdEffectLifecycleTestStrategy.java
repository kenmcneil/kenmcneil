package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioLifecycleTestStrategyBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationProduct;

/**
 * Verify that the Participation's saleId is set/unset on its uniqueIds in the product.sale table.
 * Also verifies that the product.modified table is updated, which is required to trigger product storage cache updates.
 *
 * TODO: Check ownership priority when the start dates are exactly the same in two overlapping Participations.
 */
public class SaleIdEffectLifecycleTestStrategy extends ParticipationScenarioLifecycleTestStrategyBase {

	/**
	 * Verify that the product.sale table does not have any records marked with this Participation's id.
	 * The sale id is not checked, because in real usage it is probable that other Participations use the same sale id.
	 */
	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		Assertions.assertThat(participationTestUtilities.getParticipationSaleIdCount(fixture.getParticipationId())).isEqualTo(0);
	}

	/**
	 * Verify that the actual list of owned products match the expected product ownership.
	 */
	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		List<ParticipationProduct> participationProducts = participationTestUtilities
				.getParticipationProducts(fixture.getParticipationId());
		List<Integer> ownedUniqueIds = participationProducts.stream()
				.filter(ParticipationProduct::getIsOwner)
				.map(ParticipationProduct::getUniqueId)
				.collect(Collectors.toList());
		List<Integer> expectedUniqueIds = CollectionUtils.isEmpty(fixture.getExpectedOwnedUniqueIds())
				? fixture.getUniqueIds() : fixture.getExpectedOwnedUniqueIds();
		if (!CollectionUtils.isEmpty(expectedUniqueIds)) {
			Assertions.assertThat(ownedUniqueIds).containsExactlyInAnyOrderElementsOf(expectedUniqueIds);
		}
	}

	/**
	 * Verify that the actual list of owned products match the expected product ownership.
	 */
	@Override
	public void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		List<ParticipationProduct> participationProducts = participationTestUtilities
				.getParticipationProducts(fixture.getParticipationId());
		List<Integer> ownedUniqueIds = participationProducts.stream()
				.filter(ParticipationProduct::getIsOwner)
				.map(ParticipationProduct::getUniqueId)
				.collect(Collectors.toList());
		List<Integer> expectedUniqueIds = CollectionUtils.isEmpty(fixture.getExpectedOwnedUniqueIds())
				? fixture.getUniqueIds() : fixture.getExpectedOwnedUniqueIds();
		if (!CollectionUtils.isEmpty(expectedUniqueIds)) {
			Assertions.assertThat(ownedUniqueIds).containsExactlyInAnyOrderElementsOf(expectedUniqueIds);
		}
	}

	/**
	 * Verify that the product.sale table does not have any records marked with this Participation's id.
	 * The sale id is not checked, because in real usage it is probable that other Participations use the same sale id.
	 */
	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		Assertions.assertThat(participationTestUtilities.getParticipationSaleIdCount(fixture.getParticipationId())).isEqualTo(0);
	}
}
