package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationCoreDao;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationItemizedV1Dao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;

import lombok.RequiredArgsConstructor;

/**
 * This class is responsible for all database changes related to itemized discounts.
 *
 * Example content record:
 *
 *	 "content": {
 *		 "_type": "participation-itemized@1.0.0",
 *		 "productSale": {
 *			 "saleId": 6713,
 *			 "_type": "atom-product-sale@1.0.0"
 *		 },
 *		 "itemizedDiscounts": {
 *			 "list": [
 *				 [
 *					 298721,
 *					"Kohler",
 *					 99.86,
 *					 86.9
 *				 ],
 *				 [
 *					 93506,
 *					"Moen",
 *					 20,
 *					 17
 *				 ]
 *			 ],
 *			 "_type": "atom-tuple-list@1.0.0"
 *		 }
 *	 }
 *
 * Null checking is mostly omitted since a content object is validated before publishing.
 */
@RequiredArgsConstructor
public class ParticipationItemizedV1Lifecycle implements ParticipationLifecycle {

	private static final Logger LOG = LoggerFactory.getLogger(ParticipationItemizedV1Lifecycle.class);

	private static final String[] PRODUCT_SALE_ID_PATH = {"productSale", "saleId"};
	private static final String[] ITEMIZED_DISCOUNTS_TYPE_PATH = {"itemizedDiscounts", "list"};

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationCoreDao participationCoreDao;
	private final ParticipationItemizedV1Dao participationItemizedV1Dao;

	public ParticipationContentType getContentType() {
		return ParticipationContentType.PARTICIPATION_ITEMIZED_V1;
	}

	private ParticipationItemPartial buildItemPartial(ParticipationItem item) {
		return ParticipationItemPartial.builder()
				.participationId(item.getId())
				.saleId(getSaleId(item))
				.startDate(item.getSchedule() == null ? null : item.getSchedule().getFrom())
				.endDate(item.getSchedule() == null ? null : item.getSchedule().getTo())
				.lastModifiedUserId(item.getLastModifiedUserId())
				.isActive(false)
				.contentTypeId(ParticipationContentType.PARTICIPATION_ITEMIZED_V1.contentTypeId())
				.isCoupon(false)
				.shouldBlockDynamicPricing(true)
				.build();
	}

	/**
	 * "Publish" method upserts all necessary participation data to SQL for future or immediate activation,
	 * where participation is of Itemized Discount type
	 * @return total rows affected in the db
	 */
	@Override
	public int publish(ParticipationItem item, Date processingDate) {
		ParticipationItemPartial itemPartial = buildItemPartial(item);

		int rowsAffected = participationCoreDao.upsertParticipationItemPartial(itemPartial);
		rowsAffected += participationCoreDao.upsertParticipationProducts(item.getId(),getUniqueIds(item));
		rowsAffected += participationItemizedV1Dao.upsertParticipationItemizedDiscounts(getParticipationItemizedDiscounts(item));

		return rowsAffected;
	}

	/**
	 * Calculate the owner changes table, update product ownership, and trigger product storage cache
	 * update by updating each product's modified date.
	 */
	@Override
	public int activate(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId();
		int totalRows = 0;

		// Determine what products are changing ownership and store into temp table,
		// and update ownership data.
		// -- not logging returned row-modified count since it's not always accurate
		participationCoreDao.updateOwnerChangesForActivation(participationId);

		int rowsAffected = participationCoreDao.addProductOwnershipForNewOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} products with new participation ownership", participationId, rowsAffected);

		rowsAffected = participationCoreDao.removeProductOwnershipForOldOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} products disowned from other participations", participationId, rowsAffected);

		rowsAffected = participationCoreDao.activateAndDeactivateProductSaleIds();
		totalRows += rowsAffected;
		LOG.debug("{}: {} product sale ids updated", participationId, rowsAffected);

		// Update modified dates.
		rowsAffected = participationCoreDao.updateProductModifiedDates(processingDate, userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);

		return totalRows;
	}

	@Override
	public int activateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId();
		int totalRows = 0;

		// Activate any new itemized discounts.
		int rowsAffected = participationItemizedV1Dao.applyNewItemizedDiscounts(processingDate, userId,
				participationEngineSettings.getCoolOffPeriod().toMinutes());
		totalRows += rowsAffected;
		LOG.debug("{}: {} pricebook prices (on {} products) discounted", participationId, rowsAffected, rowsAffected/2);

		return totalRows;
	}

	/**
	 * Calculate the owner changes table, update product ownership, and trigger product storage cache
	 * update by updating each product's modified date.
	 *
	 * Apply queries specific to sale id and itemized discount deactivation. All queries used here are filtered to the
	 * uniqueIds in change table rows where newParticipationId is not null.
	 */
	@Override
	public int deactivate(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId();
		int totalRows = 0;

		// Determine what products are changing ownership and store into temp table.
		participationCoreDao.updateOwnerChangesForDeactivation(participationId);

		// Assign ownership of each unique id to any active fallback participations, but
		// don't bother to update ownership on participationProduct rows of the deactivating
		// participation since they will be deleted when unpublished.
		int rowsAffected = participationCoreDao.addProductOwnershipForNewOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} formerly itemized products under new management", participationId, rowsAffected);

		rowsAffected = participationCoreDao.activateAndDeactivateProductSaleIds();
		totalRows += rowsAffected;
		LOG.debug("{}: {} product sale ids updated", participationId, rowsAffected);

		// Update modified date on each product modified.
		rowsAffected = participationCoreDao.updateProductModifiedDates(processingDate, userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);

		return totalRows;
	}

	/**
	 * Remove sale id and itemized discount effects from products in participationOwnerChange
	 * where oldParticipationId is not null.
	 */
	@Override
	public int deactivateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId();
		int totalRows = 0;

		int rowsAffected = participationItemizedV1Dao.updateLastOnSaleBasePrices(processingDate);
		totalRows += rowsAffected;
		LOG.debug("{}: {} lastOnSale base prices saved", participationId, rowsAffected);

		rowsAffected = participationItemizedV1Dao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices taken off sale from itemized discounts", participationId, rowsAffected);

		return totalRows;
	}

	@Override
	public int unpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		return participationCoreDao.deleteParticipationProducts(participationId)
				+ participationItemizedV1Dao.deleteParticipationItemizedDiscounts(participationId)
				+ participationCoreDao.deleteParticipationItemPartial(participationId);
	}

	/**
	 * Extract the saleId value from the content map.
	 */
	private int getSaleId(ParticipationItem item) {
		Integer saleId = ParticipationLifecycle.getAtPath(item, PRODUCT_SALE_ID_PATH);
		return saleId != null ? saleId : 0;
	}

	/**
	 * Extract the unique ids list from the content.
	 */
	private List<Integer> getUniqueIds(ParticipationItem item) {
		List<Integer> ids = new ArrayList<>();
		List<List<Object>> discountedPricesRows = ParticipationLifecycle.getAtPath(item, ITEMIZED_DISCOUNTS_TYPE_PATH);
		discountedPricesRows.forEach(row -> ids.add((Integer) (row.get(0))));
		return ids;
	}

	/**
	 * Consumes ParticipationItem and extracts the relevant parts of each itemized discount for pricing.
	 * Returns a multi-line string where each line is is a comma-separated list of uniqueId/pb1price/pb22price.
	 * @return csDiscountedPrices
	 */
	private List<ParticipationItemizedDiscount> getParticipationItemizedDiscounts(ParticipationItem item) {
		int participationId = item.getId();
		List<ParticipationItemizedDiscount> itemizedDiscounts = new ArrayList<>();
		List<List<Object>> discountedPricesRows = ParticipationLifecycle.getAtPath(item, ITEMIZED_DISCOUNTS_TYPE_PATH);
		if (!CollectionUtils.isEmpty(discountedPricesRows)) {
			discountedPricesRows.forEach(row -> {
				Integer uniqueId = (Integer) (row.get(0));
				Double pb1Price = doublify(row.get(2));
				Double pb22Price =  doublify(row.get(3));
				itemizedDiscounts.add(new ParticipationItemizedDiscount(participationId, uniqueId, 1, pb1Price));
				itemizedDiscounts.add(new ParticipationItemizedDiscount(participationId, uniqueId, 22, pb22Price));
			});
		}
		return itemizedDiscounts;
	}

	private Double doublify(Object o) {
		if (o instanceof Integer) {
			return (double) ((Integer) (o)).intValue();
		} else {
			return (double) o;
		}
	}

	// HISTORY

	@Override
	public void publishToHistory(ParticipationItem item, Date processingDate) {
		ParticipationItemPartial itemPartial = buildItemPartial(item);

		int partialHistoryId = participationCoreDao.insertParticipationItemPartialHistory(itemPartial);
		participationCoreDao.insertParticipationProductsHistory(partialHistoryId, getUniqueIds(item));
		participationItemizedV1Dao.insertParticipationItemizedDiscountsHistory(
				partialHistoryId, (getParticipationItemizedDiscounts(item)));
	}

	@Override
	public int updateActivatedHistory(ParticipationItemPartial itemPartial, Date processingDate) {
		return participationCoreDao.updateActivatedHistory(itemPartial.getParticipationId(), processingDate);
	}

	@Override
	public int updateDeactivatedHistory(ParticipationItemPartial itemPartial, Date processingDate) {
		return participationCoreDao.updateDeactivatedHistory(itemPartial.getParticipationId(), processingDate);
	}
}
