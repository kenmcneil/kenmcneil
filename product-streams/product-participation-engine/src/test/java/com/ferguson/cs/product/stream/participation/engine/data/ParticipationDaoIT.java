package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationEngineITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductSaleParticipation;

public class ParticipationDaoIT extends ParticipationEngineITBase {
	@Autowired
	public ParticipationDao participationDao;

	@Test
	public void setParticipationIsActive_getParticipationIsActive() {

		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.participationId(52000)
						.saleId(2020)
						.build());

		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.participationId(53000)
						.saleId(3030)
						.build());

		participationDao.setParticipationIsActive(52000, false);
		participationDao.setParticipationIsActive(53000, true);

		Boolean isInactiveActivated = participationDao.getParticipationIsActive(52000);
		Boolean isActiveActivated = participationDao.getParticipationIsActive(53000);
		Assertions.assertThat(isInactiveActivated).isFalse();
		Assertions.assertThat(isActiveActivated).isTrue();
	}

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
		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.participationId(53000)
						.saleId(3030)
						.isActive(true)
						.uniqueIds(123456, 234567)
						.calculatedDiscounts(
								percentCalculatedDiscount(1, 25),
								amountCalculatedDiscount(22, 25)
						)
						.build());

		int rowsAffected = participationDao.setParticipationIsActive(53000, true);
		Assertions.assertThat(rowsAffected).isEqualTo(1);
		participationDao.updateOwnerChangesForActivation(53000);
		rowsAffected = participationDao.addProductOwnershipForNewOwners(53000);
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationDao.activateProductSaleIds();
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationDao.updateLastOnSaleBasePrices(new Date());
		rowsAffected = participationDao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		rowsAffected = participationDao.updateProductModifiedDates(new Date(), 1);
		Assertions.assertThat(rowsAffected).isEqualTo(2);

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(123456);
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
		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.participationId(53000)
						.saleId(3030)
						.isActive(false)
						.uniqueIds(123456, 234567)
						.calculatedDiscounts(
								percentCalculatedDiscount(1, 25),
								percentCalculatedDiscount(22, 25)
						)
						.build());

		participationDao.setParticipationIsActive(53000, true);
		participationDao.updateOwnerChangesForActivation(53000);
		participationDao.addProductOwnershipForNewOwners(53000);
		participationDao.activateProductSaleIds();
		participationDao.updateLastOnSaleBasePrices(new Date());
		participationDao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		participationDao.updateProductModifiedDates(new Date(), 1);

		participationDao.setParticipationIsActive(53000, false);
		participationDao.updateOwnerChangesForDeactivation(53000);
		participationDao.addProductOwnershipForNewOwners(53000);
		participationDao.deactivateProductSaleIds();
		participationDao.updateLastOnSaleBasePrices(new Date());
		int rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(1);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		participationDao.updateProductModifiedDates(new Date(), 1);
		participationDao.deleteParticipationProducts(53000);
		participationDao.deleteParticipationCalculatedDiscounts(53000);
		participationDao.deleteParticipationItemPartial(53000);

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(123456);
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

		int rowsAffected = participationDao.deleteParticipationProducts(50000)
				+ participationDao.deleteParticipationCalculatedDiscounts(50000)
				+ participationDao.deleteParticipationItemPartial(50000);

		Assertions.assertThat(rowsAffected).isEqualTo(1);
	}
}
