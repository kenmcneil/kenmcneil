package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationEngineITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductSaleParticipation;

public class ParticipationItemizedV1DaoIT extends ParticipationEngineITBase {
	@Autowired
	public ParticipationCoreDao participationCoreDao;
	@Autowired
	public ParticipationItemizedV1Dao participationItemizedV1Dao;

	/**
	 * composite test for the following dao methods:
	 *      updateOwnerChangesForActivation(Integer participationId)
	 *      addProductOwnershipForNewOwners(Integer participationId)
	 *      activateProductSaleIds(Integer participationId)
	 *      applyNewItemizedDiscounts(Date processingDate, Integer userId)
	 *      updateProductModifiedDates(Date processingDate, Integer userId)
	 */

	@Test
	public void participation_owns_products_with_discounts() {
		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V1)
						.participationId(53000)
						.saleId(3030)
						.isActive(true)
						.uniqueIds(123456, 234567)
						.itemizedDiscounts(
								itemizedDiscount(123456, 200.00, 100.00),
								itemizedDiscount(234567, 250.00, 150.00)
						)
						.build());

		int rowsAffected = participationCoreDao.setParticipationIsActive(53000, true);
		Assertions.assertThat(rowsAffected).isEqualTo(1);
		participationCoreDao.updateOwnerChangesForActivation(53000);
		rowsAffected = participationCoreDao.addProductOwnershipForNewOwners(53000);
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationCoreDao.activateProductSaleIds();
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		participationItemizedV1Dao.updateLastOnSaleBasePrices(new Date());
		rowsAffected = participationItemizedV1Dao.applyNewItemizedDiscounts(new Date(), 1, 15);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		rowsAffected = participationCoreDao.updateProductModifiedDates(new Date(), 1);
		Assertions.assertThat(rowsAffected).isEqualTo(2);

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(123456);
		Assertions.assertThat(link.getSaleId()).isEqualTo(3030);

		int calcDiscountsCount = participationTestUtilities.getParticipationItemizedDiscountCount(53000);
		Assertions.assertThat(calcDiscountsCount).isEqualTo(4);
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
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V1)
						.participationId(53000)
						.saleId(3030)
						.isActive(false)
						.uniqueIds(123456, 234567)
						.itemizedDiscounts(
								itemizedDiscount(123456, 200.00, 100.00),
								itemizedDiscount(234567, 250.00, 150.00)
						)
						.build());

		participationCoreDao.setParticipationIsActive(53000, true);
		participationCoreDao.updateOwnerChangesForActivation(53000);
		participationCoreDao.addProductOwnershipForNewOwners(53000);
		participationCoreDao.activateProductSaleIds();
		participationItemizedV1Dao.updateLastOnSaleBasePrices(new Date());
		participationItemizedV1Dao.applyNewItemizedDiscounts(new Date(), 1, 15);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);

		participationCoreDao.setParticipationIsActive(53000, false);
		participationCoreDao.updateOwnerChangesForDeactivation(53000);
		participationCoreDao.addProductOwnershipForNewOwners(53000);
		participationCoreDao.deactivateProductSaleIds();
		participationItemizedV1Dao.updateLastOnSaleBasePrices(new Date());
		int rowsAffected = participationItemizedV1Dao.takePricesOffSaleAndApplyPendingBasePriceUpdates(1);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);
		participationCoreDao.deleteParticipationProducts(53000);
		participationCoreDao.deleteAllTypesOfDiscounts(53000);
		participationCoreDao.deleteParticipationItemPartial(53000);

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

		int rowsAffected = participationCoreDao.deleteParticipationProducts(50000)
				+ participationCoreDao.deleteAllTypesOfDiscounts(50000)
				+ participationCoreDao.deleteParticipationItemPartial(50000);

		Assertions.assertThat(rowsAffected).isEqualTo(1);
	}
}
