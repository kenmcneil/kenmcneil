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
import com.ferguson.cs.product.stream.participation.engine.ParticipationServiceImpl;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;
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
public class ParticipationV1Lifecycle extends ParticipationLifecycleBase {

	private static final Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);
	public static final String NAME_WITH_MAJOR_VERSION = "participation@1";

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationDao participationDao;

	// Define Keys used in content for this participation type.
	private static final String[] PRODUCT_UNIQUE_IDS_PATH = {"calculatedDiscounts", "uniqueIds", "list"};
	private static final String[] PRODUCT_SALE_ID_PATH = {"productSale" , "saleId"};
	private static final String[] PRICE_DISCOUNTS_TYPE_PATH = {
			"priceDiscounts", "calculatedDiscount", "discountType", "value"};
	private static final String[] PRICE_DISCOUNTS_CALCULATED_DISCOUNT_PATH = {"priceDiscounts", "calculatedDiscount"};
	private static final String[] CALCULATED_DISCOUNT_TEMPLATE_PATH = {"template", "selected"};
	private static final String[] PB22_DISCOUNT_VALUE_PATH = {"pricebookId22", "text"};
	private static final String[] PB1_DISCOUNT_VALUE_PATH = {"pricebookId1", "text"};
	private static final String PERCENT_DISCOUNT_KEY = "percentDiscount";
	private static final String PRICE_DISCOUNTS_KEY = "priceDiscounts";

	@Override
	public int publish(ParticipationItem item, Date processingDate) {
		ParticipationItemPartial itemPartial = ParticipationItemPartial.builder()
				.participationId(item.getId())
				.saleId(getSaleId(item))
				.startDate(item.getSchedule() == null ? null : item.getSchedule().getFrom())
				.endDate(item.getSchedule() == null ? null : item.getSchedule().getTo())
				.lastModifiedUserId(item.getLastModifiedUserId())
				.isActive(false)
				.build();

		int rowsAffected = participationDao.upsertParticipationItemPartial(itemPartial);
		rowsAffected += participationDao.upsertParticipationProducts(item.getId(), getUniqueIds(item));
		rowsAffected += participationDao.upsertParticipationCalculatedDiscounts(
				item.getId(), getParticipationCalculatedDiscounts(item));

		LOG.debug("{}: {} total rows updated to publish", item.getId(), rowsAffected);

		return rowsAffected;
	}

	/**
	 * Apply queries specific to sale id and calculated discount activation. Expects that the
	 * product ownership change table is initialized. All queries used here are filtered to the uniqueIds
	 * in change table rows where newParticipationId is not null.
	 *
	 * TODO: add the filtering as part of the work for itemized discounts: "and the uniqueId is in
	 *      a Participation of this type (participation@1)."
	 */
	@Override
	public int activate(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId() == null
				? participationEngineSettings.getTaskUserId()
				: itemPartial.getLastModifiedUserId();
		long coolOffPeriodMinutes = participationEngineSettings.getCoolOffPeriod().toMinutes();
		int totalRows = 0;

		int rowsAffected = participationDao.updateProductSaleIds(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product sale ids updated", participationId, rowsAffected);

		rowsAffected = participationDao.updateLastOnSaleBasePrices(processingDate);
		totalRows += rowsAffected;
		LOG.debug("{}: {} lastOnSale basePrice values saved", participationId, rowsAffected);

		rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices taken off sale from calculated discounts", participationId, rowsAffected);

		// activate new discounts (if any)
		rowsAffected = participationDao.applyNewCalculatedDiscounts(processingDate, userId, coolOffPeriodMinutes);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices put on sale from calculated discounts", participationId, rowsAffected);

		return totalRows;
	}

	/**
	 * Apply queries specific to sale id and calculated discount deactivation. Expects that the
	 * product ownership change table is initialized. All queries used here are filtered to the uniqueIds
	 * in change table rows where newParticipationId is not null.
	 *
	 * TODO: add the filtering as part of the work for itemized discounts: "and the uniqueId is in
	 *      a Participation of this type (participation@1)."
	 */
	@Override
	public int deactivate(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId() == null
				? participationEngineSettings.getTaskUserId()
				: itemPartial.getLastModifiedUserId();
		long coolOffPeriodMinutes = participationEngineSettings.getCoolOffPeriod().toMinutes();
		int totalRows = 0;

		// update sale ids to fallback participations or to zeros
		int rowsAffected = participationDao.updateProductSaleIds(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product sale ids updated", participationId, rowsAffected);

		rowsAffected = participationDao.updateLastOnSaleBasePrices(processingDate);
		totalRows += rowsAffected;
		LOG.debug("{}: {} lastOnSale basePrice values saved", participationId, rowsAffected);

		rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices taken off sale from calculated discounts", participationId, rowsAffected);

		// activate fallback discounts (if any)
		rowsAffected = participationDao.applyNewCalculatedDiscounts(processingDate, userId, coolOffPeriodMinutes);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices put on sale from calculated discounts", participationId, rowsAffected);

		return totalRows;
	}

	@Override
	public int unpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		return participationDao.deleteParticipationV1Data(itemPartial.getParticipationId());
	}

	/**
	 * Extract the value of saleId in the content map using the predefined path for this version.
	 * @return saleId
	 */
	private int getSaleId(ParticipationItem item) {
		Integer saleId = getAtPath(item, PRODUCT_SALE_ID_PATH);
		return saleId != null ? saleId : 0;
	}

	/**
	 * Extract the value of saleId in the content map using the predefined path for this version.
	 * @return saleId
	 */
	public List<Integer> getUniqueIds(ParticipationItem item) {
		List<Integer> ids = getAtPath(item, PRODUCT_UNIQUE_IDS_PATH);
		return ids == null ? new ArrayList<>() : ids;
	}

	/**
	 * Return list of participation calculated discounts for pb1 and pb22 if they are present in the content map.
	 */
	public List<ParticipationCalculatedDiscount> getParticipationCalculatedDiscounts(ParticipationItem item) {
		List<ParticipationCalculatedDiscount> discounts = new ArrayList<>();

		// Calculated discounts are optional.
		if (!item.getContent().containsKey(PRICE_DISCOUNTS_KEY)
				|| getAtPath(item, PRICE_DISCOUNTS_CALCULATED_DISCOUNT_PATH) == null) {
			return discounts;
		}

		String discountType = getAtPath(item, PRICE_DISCOUNTS_TYPE_PATH);
 		boolean isPercentDisc = discountType.equals(PERCENT_DISCOUNT_KEY);

		// Get discount map using the discountType key.
		List<String> pathToDiscountMap = Arrays.asList(PRICE_DISCOUNTS_CALCULATED_DISCOUNT_PATH);
		pathToDiscountMap.add(discountType);
		Map<String, Object> discountMap = getAtPath(item, StringUtils.toStringArray(pathToDiscountMap));

		// Get the template Id from the discountMap.
		Integer discountTemplateId = getAtPath(discountMap, CALCULATED_DISCOUNT_TEMPLATE_PATH);

		// Return ready-to-insert pricebook 1 and 22 discount objects.
		discounts.add(makeCalculatedDiscount(item.getId(), 1, getAtPath(discountMap, PB1_DISCOUNT_VALUE_PATH),
				isPercentDisc, discountTemplateId));
		discounts.add(makeCalculatedDiscount(item.getId(), 22, getAtPath(discountMap, PB22_DISCOUNT_VALUE_PATH),
				isPercentDisc, discountTemplateId));

		return discounts;
	}

	public ParticipationCalculatedDiscount makeCalculatedDiscount(
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
