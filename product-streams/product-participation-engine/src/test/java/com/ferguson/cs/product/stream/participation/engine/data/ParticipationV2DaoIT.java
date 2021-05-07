package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationEngineITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductSaleParticipation;

public class ParticipationV2DaoIT extends ParticipationEngineITBase {
	@Autowired
	public ParticipationCoreDao participationCoreDao;
	@Autowired
	public ParticipationV2Dao participationV2Dao;

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
						percentCalculatedDiscount(22, 25)
				)
				.contentType(ParticipationContentType.PARTICIPATION_V2)
				.build();
		participationTestUtilities.insertParticipationFixture(p1);

		int rowsAffected = participationCoreDao.setParticipationIsActive(p1.getParticipationId(), true);
		Assertions.assertThat(rowsAffected).isEqualTo(1);
		participationCoreDao.updateOwnerChangesForActivation(p1.getParticipationId());
		rowsAffected = participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationCoreDao.activateAndDeactivateProductSaleIds();
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		participationCoreDao.updateLastOnSaleForDeactivatingProducts(new Date());
		rowsAffected = participationV2Dao.applyNewCalculatedDiscounts(new Date(), 1, 15);
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
				.contentType(ParticipationContentType.PARTICIPATION_V2)
				.build();
		participationTestUtilities.insertParticipationFixture(p1);

		participationCoreDao.setParticipationIsActive(p1.getParticipationId(), true);
		participationCoreDao.updateOwnerChangesForActivation(p1.getParticipationId());
		participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		participationCoreDao.activateAndDeactivateProductSaleIds();
		participationCoreDao.updateLastOnSaleForDeactivatingProducts(new Date());
		participationV2Dao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);

		participationCoreDao.setParticipationIsActive(p1.getParticipationId(), false);
		participationCoreDao.updateOwnerChangesForDeactivation(p1.getParticipationId());
		participationCoreDao.addProductOwnershipForNewOwners(p1.getParticipationId());
		participationCoreDao.activateAndDeactivateProductSaleIds();
		participationCoreDao.updateLastOnSaleForDeactivatingProducts(new Date());
		int rowsAffected = participationV2Dao.takePricesOffSaleAndApplyPendingBasePriceUpdates(1);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		participationCoreDao.updateProductModifiedDates(new Date(), 1);
		participationCoreDao.deleteParticipationProducts(p1.getParticipationId());
		participationV2Dao.deleteParticipationCalculatedDiscounts(p1.getParticipationId());
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
				+ participationV2Dao.deleteParticipationCalculatedDiscounts(p1.getParticipationId())
				+ participationCoreDao.deleteParticipationItemPartial(p1.getParticipationId());

		Assertions.assertThat(rowsAffected).isEqualTo(1);
	}

	@Test
	public void insertParticipationItemPartialHistory_insertParticipationCalculatedDiscountsHistory() {
		int tUniqueId = 123456;
		int tParticipationId = 10000;
		double tDiscount = 27;
		ParticipationItemPartial itemPartial = ParticipationItemPartial.builder()
				.participationId(tParticipationId)
				.saleId(1010)
				.startDate(new Date())
				.endDate(new Date())
				.lastModifiedUserId(1)
				.isActive(false)
				.isCoupon(true)
				.shouldBlockDynamicPricing(false)
				.contentTypeId(ParticipationContentType.PARTICIPATION_V2.contentTypeId())
				.build();
		List<Integer> uniqueIds = new ArrayList<>();
		uniqueIds.add(tUniqueId);
		List<ParticipationCalculatedDiscount> calculatedDiscounts = new ArrayList<>();
		ParticipationCalculatedDiscount discount = new ParticipationCalculatedDiscount();
		discount.setChangeValue(tDiscount);
		discount.setIsPercent(true);
		discount.setParticipationId(tParticipationId);
		discount.setPricebookId(1);
		discount.setTemplateId(1);
		calculatedDiscounts.add(discount);

		int identity = participationCoreDao.insertParticipationItemPartialHistory(itemPartial);
		participationCoreDao.insertParticipationProductsHistory(identity, uniqueIds);
		participationV2Dao.insertParticipationCalculatedDiscountsHistory(identity, calculatedDiscounts);

		double returnedPercentage = participationTestUtilities.getCalculatedHistoryChangeValue(
				tParticipationId);
		Assertions.assertThat(returnedPercentage).isEqualTo(tDiscount);
	}
}
