package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationCoreDao;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationV1Dao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

import lombok.RequiredArgsConstructor;

/**
 * This class is responsible for all database changes related to participation@1 records: saleId and calculated discounts.
 * It provides getters to retrieve values based of the path defined in the definition file
 * e.g
 * "content": {
 *     "_type": "participation@1.1.0",
 *     "productSale": {
 *       "saleId": 2222,
 *       "_type": "atom-product-sale@1.0.0"
 *     },
 *     "calculatedDiscounts": {
 *       "_type": "atom-section@1.0.0",
 *       "uniqueIds": {
 *         "list": [
 *           1,
 *           2,
 *           3,
 *           4
 *         ],
 *         "_type": "atom-id-list@1.0.0"
 *       }
 *     },
 *     "priceDiscounts": {
 *       "_type": "atom-section@1.0.0",
 *       "calculatedDiscount": {
 *         "_type": "atom-section@1.0.0",
 *         "discountType": {
 *           "value": "amountDiscount",
 *           "_type": "atom-toggle-radios@1.0.0"
 *         },
 *         "amountDiscount": {
 *           "_type": "atom-section@1.0.0",
 *           "template": {
 *             "selected": 3,
 *             "_type": "atom-select@1.0.0"
 *           },
 *           "pricebookId1": {
 *             "text": "5",
 *             "_type": "atom-text@1.0.0"
 *           },
 *           "pricebookId22": {
 *             "text": "5",
 *             "_type": "atom-text@1.0.0"
 *           }
 *         }
 *       }
 *     }
 *   }
 * }
 *
 * Null checking is mostly omitted since a content object is validated before publishing.
 */
@RequiredArgsConstructor
public class ParticipationV1Lifecycle implements ParticipationLifecycle {

	private static final Logger LOG = LoggerFactory.getLogger(ParticipationV1Lifecycle.class);

	private static final String[] PRODUCT_UNIQUE_IDS_PATH = {"calculatedDiscounts", "uniqueIds", "list"};
	private static final String[] PRODUCT_SALE_ID_PATH = {"productSale", "saleId"};
	private static final String[] PRICE_DISCOUNTS_TYPE_PATH = {
			"priceDiscounts", "calculatedDiscount", "discountType", "value"};
	private static final String[] PRICE_DISCOUNTS_CALCULATED_DISCOUNT_PATH = {"priceDiscounts", "calculatedDiscount"};
	private static final String[] CALCULATED_DISCOUNT_TEMPLATE_PATH = {"template", "selected"};
	private static final String[] PB22_DISCOUNT_VALUE_PATH = {"pricebookId22", "text"};
	private static final String[] PB1_DISCOUNT_VALUE_PATH = {"pricebookId1", "text"};
	private static final String PERCENT_DISCOUNT_KEY = "percentDiscount";
	private static final String PRICE_DISCOUNTS_KEY = "priceDiscounts";

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationCoreDao participationCoreDao;
	private final ParticipationV1Dao participationV1Dao;

	public ParticipationContentType getContentType() {
		return ParticipationContentType.PARTICIPATION_V1;
	}

	/**
	 * The publish handler is fully responsible for inserting or updating all of its data
	 * that needs to be stored in SQL, thus this upserts to the tables that represent this
	 * Participation type: participationProduct, participationCalculatedDiscount,
	 * and participationItemPartial.
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
				.contentTypeId(ParticipationContentType.PARTICIPATION_V1.contentTypeId())
				.build();

		int rowsAffected = participationCoreDao.upsertParticipationItemPartial(itemPartial);
		rowsAffected += participationCoreDao.upsertParticipationProducts(item.getId(), getUniqueIds(item));
		rowsAffected += participationV1Dao.upsertParticipationCalculatedDiscounts(
				item.getId(), getParticipationCalculatedDiscounts(item));

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

		// Update modified date on each product modified.
		rowsAffected = participationCoreDao.updateProductModifiedDates(processingDate, userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);

		return totalRows;
	}

	/**
	 * Run queries to activate the saleId and any calculated discounts effects for new uniqueId owners
	 * in participationOwnerChange where newParticipationId is not null.
	 */
	@Override
	public int activateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId();
		int totalRows = 0;

		// Activate any new calculated discounts.
		int rowsAffected = participationV1Dao.applyNewCalculatedDiscounts(processingDate, userId,
				participationEngineSettings.getCoolOffPeriod().toMinutes());
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices discounted by calculated discounts", participationId, rowsAffected);

		return totalRows;
	}

	/**
	 * Calculate the owner changes table, update product ownership, and trigger product storage cache
	 * update by updating each product's modified date.
	 *
	 * Apply queries specific to sale id and calculated discount deactivation. All queries used here are filtered to the uniqueIds
	 * in change table rows where newParticipationId is not null.
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
		LOG.debug("{}: {} products with new participation ownership", participationId, rowsAffected);

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
	 * Remove sale id and calculated discount effects from products in participationOwnerChange
	 * where oldParticipationId is not null.
	 */
	@Override
	public int deactivateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId();
		int totalRows = 0;

		int rowsAffected = participationV1Dao.updateLastOnSaleBasePrices(processingDate);
		totalRows += rowsAffected;
		LOG.debug("{}: {} lastOnSale basePrice values saved", participationId, rowsAffected);

		rowsAffected = participationV1Dao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices taken off sale from calculated discounts", participationId, rowsAffected);

		return totalRows;
	}

	@Override
	public int unpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		return participationCoreDao.deleteParticipationProducts(participationId)
				+ participationV1Dao.deleteParticipationCalculatedDiscounts(participationId)
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
	 * Return list of participation calculated discounts for pb1 and pb22 if they are present in the content map.
	 */
	private List<ParticipationCalculatedDiscount> getParticipationCalculatedDiscounts(ParticipationItem item) {
		List<ParticipationCalculatedDiscount> discounts = new ArrayList<>();

		// Calculated discounts are optional.
		if (!item.getContent().containsKey(PRICE_DISCOUNTS_KEY)
				|| ParticipationLifecycle.getAtPath(item, PRICE_DISCOUNTS_CALCULATED_DISCOUNT_PATH) == null) {
			return discounts;
		}
		String discountType = ParticipationLifecycle.getAtPath(item, PRICE_DISCOUNTS_TYPE_PATH);
		boolean isPercentDisc = PERCENT_DISCOUNT_KEY.equals(discountType);
		// Get discount map using the discountType key.
		List<String> pathToDiscountMap = new ArrayList<>(Arrays.asList(PRICE_DISCOUNTS_CALCULATED_DISCOUNT_PATH));
		pathToDiscountMap.add(discountType);
		Map<String, Object> discountMap = ParticipationLifecycle.getAtPath(item, StringUtils.toStringArray(pathToDiscountMap));

		// Get the template Id from the discountMap.
		Integer discountTemplateId = ParticipationLifecycle.getAtPath(discountMap, CALCULATED_DISCOUNT_TEMPLATE_PATH);

		// Return ready-to-insert pricebook 1 and 22 discount objects.
		discounts.add(makeCalculatedDiscount(item.getId(), 1, ParticipationLifecycle.getAtPath(discountMap, PB1_DISCOUNT_VALUE_PATH),
				isPercentDisc, discountTemplateId));
		discounts.add(makeCalculatedDiscount(item.getId(), 22, ParticipationLifecycle.getAtPath(discountMap, PB22_DISCOUNT_VALUE_PATH),
				isPercentDisc, discountTemplateId));

		return discounts;
	}

	private ParticipationCalculatedDiscount makeCalculatedDiscount(
			Integer participationId,
			Integer pricebookId,
			String discountAmount,
			Boolean isPercentDisc,
			Integer discountTemplateId
	) {
		Double changeValue = new Double(discountAmount);

		// If the discount is a percent off value then convert to a scaling factor: (100 - percent off) / 100
		// else the discount is a amount off value, convert to a value to add: -amount
		if (isPercentDisc) {
			changeValue = (100 - changeValue) / 100;
		} else {
			changeValue = -changeValue;
		}

		return new ParticipationCalculatedDiscount(participationId, pricebookId, changeValue, isPercentDisc, discountTemplateId);
	}
}
