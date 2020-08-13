package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationEngineITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductSaleParticipation;

public class ParticipationItemizedV1DaoIT extends ParticipationEngineITBase {
	@Autowired
	public ParticipationCoreDao participationCoreDao;
	@Autowired
	public ParticipationItemizedV1Dao participationItemizedV1Dao;

	/**
	 * Composite test for the following dao methods:
	 *      updateOwnerChangesForActivation(Integer participationId)
	 *      addProductOwnershipForNewOwners(Integer participationId)
	 *      activateProductSaleIds(Integer participationId)
	 *      applyNewItemizedDiscounts(Date processingDate, Integer userId)
	 *      updateProductModifiedDates(Date processingDate, Integer userId)
	 */
	@Test
	public void participation_owns_products_with_discounts() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V1)
				.saleId(3030)
				.isActive(true)
				.itemizedDiscounts(
						itemizedDiscount(uniqueIds[0], 200.00, 100.00),
						itemizedDiscount(uniqueIds[1], 250.00, 150.00)
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
		participationItemizedV1Dao.updateLastOnSaleBasePrices(new Date());
		rowsAffected = participationItemizedV1Dao.applyNewItemizedDiscounts(new Date(), 1, 15);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		rowsAffected = participationCoreDao.updateProductModifiedDates(new Date(), 1);
		Assertions.assertThat(rowsAffected).isEqualTo(2);

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(uniqueIds[0]);
		Assertions.assertThat(link.getSaleId()).isEqualTo(3030);

		int calcDiscountsCount = participationTestUtilities.getParticipationItemizedDiscountCount(p1.getParticipationId());
		Assertions.assertThat(calcDiscountsCount).isEqualTo(4);
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
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V1)
				.saleId(3030)
				.isActive(false)
				.uniqueIds(uniqueIds[0], uniqueIds[1])
				.itemizedDiscounts(
						itemizedDiscount(uniqueIds[0], 200.00, 100.00),
						itemizedDiscount(uniqueIds[1], 250.00, 150.00)
				)
				.build();
		participationTestUtilities.insertParticipationFixture(p1);

		participationCoreDao.setParticipationIsActive(p1.getParticipationId(), true);
		participationCoreDao.updateOwnerChangesForActivation(p1.getParticipationId());
		participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		participationCoreDao.activateAndDeactivateProductSaleIds();
		participationItemizedV1Dao.updateLastOnSaleBasePrices(new Date());
		participationItemizedV1Dao.applyNewItemizedDiscounts(new Date(), 1, 15);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);

		participationCoreDao.setParticipationIsActive(p1.getParticipationId(), false);
		participationCoreDao.updateOwnerChangesForDeactivation(p1.getParticipationId());
		participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		participationCoreDao.activateAndDeactivateProductSaleIds();
		participationItemizedV1Dao.updateLastOnSaleBasePrices(new Date());
		int rowsAffected = participationItemizedV1Dao.takePricesOffSaleAndApplyPendingBasePriceUpdates(1);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);
		participationCoreDao.deleteParticipationProducts(p1.getParticipationId());
		participationItemizedV1Dao.deleteParticipationItemizedDiscounts(p1.getParticipationId());
		participationCoreDao.deleteParticipationItemPartial(p1.getParticipationId());

		// Check final state
		ProductSaleParticipation link = participationTestUtilities.getProductSaleParticipation(uniqueIds[0]);
		Assertions.assertThat(link.getSaleId()).isNotEqualTo(3030);

		int calcDiscountsCount = participationTestUtilities.getParticipationItemizedDiscountCount(p1.getParticipationId());
		Assertions.assertThat(calcDiscountsCount).isEqualTo(0);
	}

	/**
	 * test that the cool-off period is respected.
	 * NOTE: the cool-off is a config value, static here at 15mins.
	 */
	@Test
	public void participation_test_coolOffPeriod_is_honored() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		int coolOffPeriodMinutes = 15;
		//set los.saleDate to now, los.basePrice to some value A...
		int pricebookId = 1;
		int uniqueId = uniqueIds[0];
		Double startingLASBasePrice = 110.00;
		participationTestUtilities.upsertParticipationLastOnSaleBase(pricebookId, uniqueId, new Date(), startingLASBasePrice);

		//publish
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V1)
				.saleId(3030)
				.isActive(false)
				.uniqueIds(uniqueId)
				.itemizedDiscounts(
						itemizedDiscount(uniqueIds[0], 200.00, 100.00)
				)
				.build();
		participationTestUtilities.insertParticipationFixture(p1);
		participationCoreDao.setParticipationIsActive(p1.getParticipationId(), true);
		participationCoreDao.updateOwnerChangesForActivation(p1.getParticipationId());
		participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		participationCoreDao.activateAndDeactivateProductSaleIds();

		//set pbcost.basePrice to some value B...
		Double startingPBCBasePrice = 120.00;
		participationTestUtilities.updatePricebookCostCost(startingPBCBasePrice, uniqueId, pricebookId);

		//run dao.applyNewItemizedDiscounts() with current time
		participationItemizedV1Dao.applyNewItemizedDiscounts(new Date(), 1, coolOffPeriodMinutes);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);
		//assert that pbCost.basePrice == valueA
		Double newPBCBasePrice = participationTestUtilities.getPricebookCostBasePrice(uniqueId, pricebookId);
		Assertions.assertThat(newPBCBasePrice).isEqualTo(startingLASBasePrice);
		//change los.saleDate to T-1hr
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1);
		Date oneHourAgo = cal.getTime();
		participationTestUtilities.upsertParticipationLastOnSaleBase(pricebookId, uniqueId, oneHourAgo,
				startingLASBasePrice);
		//run dao.applyNewItemizedDiscounts() with current time
		participationItemizedV1Dao.applyNewItemizedDiscounts(new Date(), 1, coolOffPeriodMinutes);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);
		//assert that pbCost.basePrice == valueB
		Double finalPBCBasePrice = participationTestUtilities.getPricebookCostBasePrice(uniqueId, pricebookId);
		Assertions.assertThat(finalPBCBasePrice).isEqualTo(startingLASBasePrice);
	}

	@Test
	public void deleteParticipation() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(2020)
				.isActive(false)
				.build();
		participationTestUtilities.insertParticipationFixture(p1);

		int rowsAffected = participationCoreDao.deleteParticipationProducts(p1.getParticipationId())
				+ participationItemizedV1Dao.deleteParticipationItemizedDiscounts(p1.getParticipationId())
				+ participationCoreDao.deleteParticipationItemPartial(p1.getParticipationId());

		Assertions.assertThat(rowsAffected).isEqualTo(1);
	}

	@Test
	public void insertParticipationItemPartialHistory_insertParticipationItemizedDiscountsHistory() {
		int tUniqueId = 123456;
		int tParticipationId = 10000;
		double tPrice = 100.00;
		ParticipationItemPartial itemPartial = ParticipationItemPartial.builder()
				.participationId(tParticipationId)
				.saleId(1010)
				.startDate(new Date())
				.endDate(new Date())
				.lastModifiedUserId(1)
				.isActive(false)
				.isCoupon(true)
				.shouldBlockDynamicPricing(false)
				.contentTypeId(ParticipationContentType.PARTICIPATION_COUPON_V1.contentTypeId())
				.build();
		List<Integer> uniqueIds = new ArrayList<>();
		uniqueIds.add(tUniqueId);
		List<ParticipationItemizedDiscount> itemizedDiscounts = new ArrayList<>();
		ParticipationItemizedDiscount discount = new ParticipationItemizedDiscount();
		discount.setUniqueId(tUniqueId);
		discount.setPricebookId(1);
		discount.setPrice(tPrice);
		itemizedDiscounts.add(discount);

		int identity = participationCoreDao.insertParticipationItemPartialHistory(itemPartial);
		participationCoreDao.insertParticipationProductsHistory(identity, uniqueIds);
		participationItemizedV1Dao.insertParticipationItemizedDiscountsHistory(identity,itemizedDiscounts);

		double returnedPrice = participationTestUtilities.getItemizedHistoryPrice(tParticipationId, tUniqueId);
		Assertions.assertThat(returnedPrice).isEqualTo(tPrice);
	}
}
