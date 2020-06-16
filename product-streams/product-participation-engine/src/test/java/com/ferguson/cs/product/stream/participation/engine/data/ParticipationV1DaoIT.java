package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationEngineITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductSaleParticipation;

public class ParticipationV1DaoIT extends ParticipationEngineITBase {
	@Autowired
	public ParticipationCoreDao participationCoreDao;
	@Autowired
	public ParticipationV1Dao participationV1Dao;

	/**
	 * Composite test for the following dao methods:
	 *      updateOwnerChangesForActivation(Integer participationId)
	 *      addProductOwnershipForNewOwners(Integer participationId)
	 *      activateProductSaleIds(Integer participationId)
	 *      applyNewCalculatedDiscounts(Date processingDate, Integer userId)
	 *      updateProductModifiedDates(Date processingDate, Integer userId)
	 */
	@Test
	public void participation_owns_products_with_discounts() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(3030)
				.isActive(true)
				.uniqueIds(uniqueIds[0], uniqueIds[1])
				.calculatedDiscounts(
						percentCalculatedDiscount(1, 25),
						amountCalculatedDiscount(22, 25)
				)
				.build();
		participationTestUtilities.insertParticipationFixture(p1);

		int rowsAffected = participationCoreDao.setParticipationIsActive(p1.getParticipationId(), true);
		Assertions.assertThat(rowsAffected).isEqualTo(1);
		participationCoreDao.updateOwnerChangesForActivation(p1.getParticipationId());
		rowsAffected = participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationCoreDao.activateAndDeactivateProductSaleIds();
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationV1Dao.updateLastOnSaleBasePrices(new Date());
		rowsAffected = participationV1Dao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		rowsAffected = participationCoreDao.updateProductModifiedDates(new Date(), 1);
		Assertions.assertThat(rowsAffected).isEqualTo(2);

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(uniqueIds[0]);
		Assertions.assertThat(link.getSaleId()).isEqualTo(3030);

		int calcDiscountsCount = participationTestUtilities.getParticipationCalculatedDiscountCount(p1.getParticipationId());
		Assertions.assertThat(calcDiscountsCount).isEqualTo(2);
	}

	/**
	 * Composite test for the following dao methods:
	 *      takePricesOffSaleAndApplyPendingBasePriceUpdates(Integer userId)
	 *      updateOwnerChangesForDeactivation(Integer participationId)
	 */
	@Test
	public void participation_disowns_products_with_discounts() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(3030)
				.isActive(false)
				.uniqueIds(uniqueIds[0], uniqueIds[1])
				.calculatedDiscounts(
						percentCalculatedDiscount(1, 25),
						percentCalculatedDiscount(22, 25)
				)
				.build();
		participationTestUtilities.insertParticipationFixture(p1);

		participationCoreDao.setParticipationIsActive(p1.getParticipationId(), true);
		participationCoreDao.updateOwnerChangesForActivation(p1.getParticipationId());
		participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		participationCoreDao.activateAndDeactivateProductSaleIds();
		participationV1Dao.updateLastOnSaleBasePrices(new Date());
		participationV1Dao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);

		participationCoreDao.setParticipationIsActive(p1.getParticipationId(), false);
		participationCoreDao.updateOwnerChangesForDeactivation(p1.getParticipationId());
		participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		participationCoreDao.activateAndDeactivateProductSaleIds();
		participationV1Dao.updateLastOnSaleBasePrices(new Date());
		int rowsAffected = participationV1Dao.takePricesOffSaleAndApplyPendingBasePriceUpdates(1);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);
		participationCoreDao.deleteParticipationProducts(p1.getParticipationId());
		participationV1Dao.deleteParticipationCalculatedDiscounts(p1.getParticipationId());
		participationCoreDao.deleteParticipationItemPartial(p1.getParticipationId());

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(uniqueIds[0]);
		Assertions.assertThat(link.getSaleId()).isNotEqualTo(3030);

		int calcDiscountsCount = participationTestUtilities.getParticipationCalculatedDiscountCount(p1.getParticipationId());
		Assertions.assertThat(calcDiscountsCount).isEqualTo(0);
	}

	@Test
	public void deleteParticipation() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(2020)
				.isActive(false)
				.build();
		participationTestUtilities.insertParticipationFixture(p1);

		int rowsAffected = participationCoreDao.deleteParticipationProducts(p1.getParticipationId())
				+ participationV1Dao.deleteParticipationCalculatedDiscounts(p1.getParticipationId())
				+ participationCoreDao.deleteParticipationItemPartial(p1.getParticipationId());

		Assertions.assertThat(rowsAffected).isEqualTo(1);
	}
}
