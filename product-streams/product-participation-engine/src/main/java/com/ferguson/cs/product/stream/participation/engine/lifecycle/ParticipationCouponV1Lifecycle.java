package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationCoreDao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

import lombok.RequiredArgsConstructor;

/**
 * {
 *   "_type": "participation-coupon@1.1.0",
 *   "productSale": {
 *     "saleId": 0,
 *     "_type": "atom-product-sale@1.0.0"
 *   },
 *   "isCoupon": {
 *     "value": "",
 *     "_type": "atom-toggle-radios@1.0.0"
 *   },
 *   "blockDynamicPricing": {
 *     "value": "",
 *     "_type": "atom-toggle-radios@1.0.0"
 *   },
 *   "uniqueIds": {
 *     "list": [],
 *     "_type": "atom-id-list@1.0.0"
 *   }
 * }
 */

@RequiredArgsConstructor
public class ParticipationCouponV1Lifecycle implements ParticipationLifecycle{

	private static final Logger LOG = LoggerFactory.getLogger(ParticipationCouponV1Lifecycle.class);

	private static final String[] PRODUCT_SALE_ID_PATH = {"productSale", "saleId"};
	private static final String[] PRODUCT_UNIQUE_IDS_PATH = {"uniqueIds", "list"};
	private static final String[] IS_COUPON_PATH = {"isCoupon", "value"};
	private static final String[] SHOULD_BLOCK_DYNAMIC_PRICING_PATH = {"blockDynamicPricing", "value"};

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationCoreDao participationCoreDao;

	public ParticipationContentType getContentType() {
		return ParticipationContentType.PARTICIPATION_COUPON_V1;
	}


	/**
	 * "Publish" method upserts all necessary participation data to SQL for future or immediate activation,
	 * where participation is of Coupon type
	 * @return total rows affected in the db
	 */
	@Override
	public int publish(ParticipationItem item, Date processingDate) {
		ParticipationItemPartial itemPartial = ParticipationItemPartial.builder()
				.participationId(item.getId())
				.saleId(getSaleId(item))
				.startDate(item.getSchedule() == null ? null : item.getSchedule().getFrom())
				.endDate(item.getSchedule() == null ? null : item.getSchedule().getTo())
				.lastModifiedUserId(item.getLastModifiedUserId())
				.isActive(false)
				.isCoupon(getIsCoupon(item))
				.shouldBlockDynamicPricing(getShouldBlockDynamicPricing(item))
				.contentTypeId(ParticipationContentType.PARTICIPATION_COUPON_V1.contentTypeId())
				.build();

		int rowsAffected = participationCoreDao.upsertParticipationItemPartial(itemPartial);
		rowsAffected += participationCoreDao.upsertParticipationProducts(item.getId(),getUniqueIds(item));

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

	/**
	 * Stubbed out. Not needed for this content type at this time.
	 */
	@Override
	public int activateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}

	/**
	 * Stubbed out. Not needed for this content type at this time.
	 */
	@Override
	public int deactivateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}

	/**
	 * Calculate the owner changes table, update product ownership, and trigger product storage cache
	 * update by updating each product's modified date.
	 *
	 * Apply queries specific to sale id deactivation. All queries used here are filtered to the
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

	@Override
	public int unpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		return participationCoreDao.deleteParticipationProducts(participationId)
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
		List<Integer> ids = ParticipationLifecycle.getAtPath(item, PRODUCT_UNIQUE_IDS_PATH);
		return ids == null ? new ArrayList<>() : ids;
	}

	/**
	 * Extract isCoupon from the content map.
	 */
	private Boolean getIsCoupon(ParticipationItem item) {
		return ParticipationLifecycle.getAtPath(item, IS_COUPON_PATH);
	}

	/**
	 * Extract shouldBlockDynamicPricing from the content map.
	 */
	private Boolean getShouldBlockDynamicPricing(ParticipationItem item) {
		return ParticipationLifecycle.getAtPath(item, SHOULD_BLOCK_DYNAMIC_PRICING_PATH);
	}
}
