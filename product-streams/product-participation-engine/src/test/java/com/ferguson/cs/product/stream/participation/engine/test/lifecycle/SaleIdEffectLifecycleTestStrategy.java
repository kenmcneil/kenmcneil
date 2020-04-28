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
 * Also verify that the product.modified table is updated, which is required to trigger product storage cache updates.
 *
 * TODO: Check ownership priority when the start dates are exactly the same in two overlapping Participations.
 */
public class SaleIdEffectLifecycleTestStrategy extends ParticipationScenarioLifecycleTestStrategyBase {

	/**
	 * Verify that the product.sale table does not have any records marked with this Participation's id.
	 * The sale id is not checked here, because in real usage it is probable that other Participations use the same sale id.
	 */
	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		Assertions.assertThat(participationTestUtilities.getParticipationSaleIdCount(fixture.getParticipationId())).isEqualTo(0);
	}

	/**
	 * Verify that currently-owned products match expected ownership.
	 * Verify that the owned products have the Participation's sale id value.
	 */
	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		List<Integer> ownedUniqueIds = participationTestUtilities
				.getParticipationProducts(fixture.getParticipationId()).stream()
				.filter(ParticipationProduct::getIsOwner)
				.map(ParticipationProduct::getUniqueId)
				.collect(Collectors.toList());

		// Verify product ownership.
		List<Integer> expectedUniqueIds = CollectionUtils.isEmpty(fixture.getExpectedOwnedUniqueIds())
				? fixture.getUniqueIds() : fixture.getExpectedOwnedUniqueIds();
		if (!CollectionUtils.isEmpty(expectedUniqueIds)) {
			Assertions.assertThat(ownedUniqueIds).containsExactlyInAnyOrderElementsOf(expectedUniqueIds);
		}

		// Verify owned products have the right sale id.
		participationTestUtilities.getProductSaleLinks(ownedUniqueIds)
				.forEach(link -> Assertions.assertThat(link.getSaleId()).isEqualTo(fixture.getSaleId()));
	}

	/**
	 * Verify that the product.sale table does not have any records marked with this Participation's id.
	 * The sale id is not checked because in real usage it is probable that other Participations use the same sale id.
	 */
	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		// Verify sale id table doesn't have the participation id on any records.
		Assertions.assertThat(participationTestUtilities.getParticipationSaleIdCount(fixture.getParticipationId())).isEqualTo(0);

		// Verify modified date was updated for the expected owned products.
		List<Integer> expectedUniqueIds = CollectionUtils.isEmpty(fixture.getExpectedOwnedUniqueIds())
				? fixture.getUniqueIds() : fixture.getExpectedOwnedUniqueIds();
		if (!CollectionUtils.isEmpty(expectedUniqueIds)) {
			participationTestUtilities.getProductModifieds(expectedUniqueIds)
					.forEach(modified -> Assertions.assertThat(modified.getModifiedDate().getTime())
							.isGreaterThanOrEqualTo(processingDate.getTime()));
		}
	}
}
