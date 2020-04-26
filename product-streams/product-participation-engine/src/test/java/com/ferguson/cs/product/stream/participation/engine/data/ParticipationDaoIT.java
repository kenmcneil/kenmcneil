package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationEngineITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

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
	 *      updateProductSaleIds(Integer participationId)
	 *      applyNewCalculatedDiscounts(Date processingDate, Integer userId)
	 *      updateProductModifiedDates(Date processingDate, Integer userId)
	 */
	@Test
	public void participation_owns_products_with_discounts() {

		int calculatedDiscountTemplateId = participationTestUtilities.insertCalculatedDiscountTemplateAndType();

		List<ParticipationCalculatedDiscount> discounts = new ArrayList<>();
		ParticipationCalculatedDiscount discount1 = new ParticipationCalculatedDiscount();
		discount1.setPricebookId(1);
		discount1.setChangeValue(.25);
		discount1.setIsPercent(true);
		discount1.setTemplateId(calculatedDiscountTemplateId);
		discounts.add(discount1);
		ParticipationCalculatedDiscount discount22 = new ParticipationCalculatedDiscount();
		discount22.setPricebookId(22);
		discount22.setChangeValue(.25);
		discount22.setIsPercent(false);
		discount22.setTemplateId(calculatedDiscountTemplateId);
		discounts.add(discount22);
		ParticipationItemFixture values = new ParticipationItemFixture();
		values.setParticipationId(53000);
		values.setSaleId(3030);
		values.setIsActive(true);
		values.setUniqueIds(Arrays.asList(123456, 234567));
		values.setCalculatedDiscounts(discounts);
		participationTestUtilities.insertParticipationFixture(values);

		int rowsAffected = participationDao.setParticipationIsActive(53000, true);
		Assertions.assertThat(rowsAffected).isEqualTo(1);
		participationDao.updateOwnerChangesForActivation(53000);
		rowsAffected = participationDao.addProductOwnershipForNewOwners(53000);
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationDao.updateProductSaleIds(53000);
		Assertions.assertThat(rowsAffected).isEqualTo(2);
		rowsAffected = participationDao.updateLastOnSaleBasePrices(new Date());
		rowsAffected = participationDao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		rowsAffected = participationDao.updateProductModifiedDates(new Date(), 1);
		Assertions.assertThat(rowsAffected).isEqualTo(2);

		// Check final state
		int resultSaleId = jdbcTemplate.queryForObject(participationTestUtilities.SELECT_SALE_ID,
				new Object[] { 123456 }, int.class);
		Assertions.assertThat(resultSaleId).isEqualTo(3030);

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

		int calculatedDiscountTemplateId = participationTestUtilities.insertCalculatedDiscountTemplateAndType();

		List<ParticipationCalculatedDiscount> discounts = new ArrayList<>();
		ParticipationCalculatedDiscount discount1 = new ParticipationCalculatedDiscount();
		discount1.setPricebookId(1);
		discount1.setChangeValue(.25);
		discount1.setIsPercent(true);
		discount1.setTemplateId(calculatedDiscountTemplateId);
		discounts.add(discount1);
		ParticipationCalculatedDiscount discount22 = new ParticipationCalculatedDiscount();
		discount22.setPricebookId(22);
		discount22.setChangeValue(.25);
		discount22.setIsPercent(true);
		discount22.setTemplateId(calculatedDiscountTemplateId);
		discounts.add(discount22);
		ParticipationItemFixture values = new ParticipationItemFixture();
		values.setParticipationId(53000);
		values.setSaleId(3030);
		values.setIsActive(false);
		values.setUniqueIds(Arrays.asList(123456, 234567));
		values.setCalculatedDiscounts(discounts);
		participationTestUtilities.insertParticipationFixture(values);
		participationDao.setParticipationIsActive(53000, true);
		participationDao.updateOwnerChangesForActivation(53000);
		participationDao.addProductOwnershipForNewOwners(53000);
		participationDao.updateProductSaleIds(53000);
		participationDao.updateLastOnSaleBasePrices(new Date());
		participationDao.applyNewCalculatedDiscounts(new Date(), 1, 15);
		participationDao.updateProductModifiedDates(new Date(), 1);

		participationDao.setParticipationIsActive(53000, false);
		participationDao.updateOwnerChangesForDeactivation(53000);
		participationDao.addProductOwnershipForNewOwners(53000);
		participationDao.updateProductSaleIds(53000);
		participationDao.updateLastOnSaleBasePrices(new Date());
		int rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(1);
		Assertions.assertThat(rowsAffected).isEqualTo(4);
		participationDao.updateProductModifiedDates(new Date(), 1);
		participationDao.deleteParticipation(53000);

		// Check final state
		int resultSaleId = jdbcTemplate.queryForObject(participationTestUtilities.SELECT_SALE_ID,
				new Object[] { 123456 }, int.class);
		Assertions.assertThat(resultSaleId).isNotEqualTo(3030);

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

		int rowsAffected = participationDao.deleteParticipation(50000);

		Assertions.assertThat(rowsAffected).isEqualTo(1);
	}

}
