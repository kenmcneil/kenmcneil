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
	 * composite test for the following dao methods:
	 *      updateOwnerChangesForActivation(Integer participationId)
	 *      addProductOwnershipForNewOwners(Integer participationId)
	 *      activateProductSaleIds(Integer participationId)
	 *      applyNewCalculatedDiscounts(Date processingDate, Integer userId)
	 *      updateProductModifiedDates(Date processingDate, Integer userId)
	 */
	@Test
	public void participation_owns_products_with_discounts() {
		int[] uniqueIds = getSafeTestUniqueIds();
		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.participationId(53000)
						.saleId(3030)
						.isActive(true)
						.uniqueIds(uniqueIds[0], uniqueIds[1])
						.calculatedDiscounts(
								percentCalculatedDiscount(1, 25),
								amountCalculatedDiscount(22, 25)
						)
						.build());

		int rowsAffected = participationCoreDao.setParticipationIsActive(53000, true);
		Assertions.assertThat(rowsAffected).isEqualTo(1);
		participationCoreDao.updateOwnerChangesForActivation(53000);
		rowsAffected = participationCoreDao.addProductOwnershipForNewOwners(53000);
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationCoreDao.activateProductSaleIds();
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationV1Dao.updateLastOnSaleBasePrices(new Date());
		rowsAffected = participationV1Dao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		rowsAffected = participationCoreDao.updateProductModifiedDates(new Date(), 1);
		Assertions.assertThat(rowsAffected).isEqualTo(2);

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(uniqueIds[0]);
		Assertions.assertThat(link.getSaleId()).isEqualTo(3030);

		int calcDiscountsCount = participationTestUtilities.getParticipationCalculatedDiscountCount(53000);
		Assertions.assertThat(calcDiscountsCount).isEqualTo(2);
	}

	/**
	 * composite test for the following dao methods:
	 *      takePricesOffSaleAndApplyPendingBasePriceUpdates(Integer userId)
	 *      updateOwnerChangesForDeactivation(Integer participationId)
	 */
	@Test
	public void participation_disowns_products_with_discounts() {
		int[] uniqueIds = getSafeTestUniqueIds();
		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.participationId(53000)
						.saleId(3030)
						.isActive(false)
						.uniqueIds(uniqueIds[0], uniqueIds[1])
						.calculatedDiscounts(
								percentCalculatedDiscount(1, 25),
								percentCalculatedDiscount(22, 25)
						)
						.build());

		participationCoreDao.setParticipationIsActive(53000, true);
		participationCoreDao.updateOwnerChangesForActivation(53000);
		participationCoreDao.addProductOwnershipForNewOwners(53000);
		participationCoreDao.activateProductSaleIds();
		participationV1Dao.updateLastOnSaleBasePrices(new Date());
		participationV1Dao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);

		participationCoreDao.setParticipationIsActive(53000, false);
		participationCoreDao.updateOwnerChangesForDeactivation(53000);
		participationCoreDao.addProductOwnershipForNewOwners(53000);
		participationCoreDao.deactivateProductSaleIds();
		participationV1Dao.updateLastOnSaleBasePrices(new Date());
		int rowsAffected = participationV1Dao.takePricesOffSaleAndApplyPendingBasePriceUpdates(1);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);
		participationCoreDao.deleteParticipationProducts(53000);
		participationV1Dao.deleteParticipationCalculatedDiscounts(53000);
		participationCoreDao.deleteParticipationItemPartial(53000);

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(uniqueIds[0]);
		Assertions.assertThat(link.getSaleId()).isNotEqualTo(3030);

		int calcDiscountsCount = participationTestUtilities.getParticipationCalculatedDiscountCount(53000);
		Assertions.assertThat(calcDiscountsCount).isEqualTo(0);
	}

	@Test
	public void deleteParticipation() {

		ParticipationItemFixture values = new ParticipationItemFixture();
		values.setParticipationId(50000);
		values.setSaleId(2020);
		values.setIsActive(false);
		participationTestUtilities.insertParticipationFixture(values);

		int rowsAffected = participationCoreDao.deleteParticipationProducts(50000)
				+ participationV1Dao.deleteParticipationCalculatedDiscounts(50000)
				+ participationCoreDao.deleteParticipationItemPartial(50000);

		Assertions.assertThat(rowsAffected).isEqualTo(1);
	}
}
