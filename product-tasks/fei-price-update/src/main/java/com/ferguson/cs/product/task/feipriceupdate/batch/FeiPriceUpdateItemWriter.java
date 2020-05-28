package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceUpdateStatus;

public class FeiPriceUpdateItemWriter implements ItemWriter<FeiPriceUpdateItem> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiPriceUpdateItemWriter.class);
	private static final int LIGHTING_BASE_CATEGORY_ID = 4;
	private static final Double MINIMUM_PROFIT_MARGIN = .14;

	private final FeiPriceUpdateService feiPriceUpdateService;

	public FeiPriceUpdateItemWriter(FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends FeiPriceUpdateItem> items) throws Exception {


		for (FeiPriceUpdateItem item : (List<FeiPriceUpdateItem>) items) {

			LOGGER.debug("FeiPriceUpdateItemWriter - Processing Item uniqueId: {}, mpid:{}, consumer price: {}",
					item.getUniqueId(), item.getMpid(), item.getPrice());

			PriceUpdateStatus validationStatus = validPriceUpdateRecord(item);
			Double preferredVendorCost = null;

			if (validationStatus == PriceUpdateStatus.VALID) {
				// Get preferred vendor cost.  This will be used to validate the margin.
				preferredVendorCost = feiPriceUpdateService.getPreferredVendorCost(item.getUniqueId());
				if (preferredVendorCost == null) {
					validationStatus = PriceUpdateStatus.VENDOR_COST_LOOKUP_ERROR;
				} else if (!isValidProfitMargin(preferredVendorCost, item.getPrice())) {
					validationStatus = PriceUpdateStatus.LOW_MARGIN_ERROR;
				}
			}

			// One last validation check to validate the profit margin.
			item.setStatus(validationStatus);

			feiPriceUpdateService.insertTempPriceUpdateRecord(item);

			if (validationStatus == PriceUpdateStatus.VALID) {
				// Create a record for Pro pricing. Input file is customer pricing only
				FeiPriceUpdateItem proItem = buildProPricingRecord(item);

				LOGGER.debug("FeiPriceUpdateItemWriter - Pro price: {}", proItem.getPrice());

				if (!isValidProfitMargin(preferredVendorCost, proItem.getPrice())) {
					validationStatus = PriceUpdateStatus.LOW_MARGIN_ERROR;
				}

				feiPriceUpdateService.insertTempPriceUpdateRecord(proItem);
			}
		}
	}

	/*
	 * Perform record validation.  Error code will be stored in temp table and a CSV file will
	 * be sent out containing any errors that failed validation resulting in no price update.
	 */
	private PriceUpdateStatus validPriceUpdateRecord(FeiPriceUpdateItem item) {

		// Validate the supplied input record
		if (item.getUniqueId() == null) {
			LOGGER.error("FeiPriceUpdateItemWriter - FEI Price update record must contain a uniqueId");
			return PriceUpdateStatus.INPUT_VALIDATION_ERROR;
		}

		// Not an error - This just means it won't be included in the update as it's not a FEI product
		if (item.getFeiOwnedProductId() == null) {
			LOGGER.debug("FeiPriceUpdateItemWriter - Skipping product unique ID : {}. Not FEI owned",
					item.getUniqueId());
			return PriceUpdateStatus.OWNED_LOOKUP_ERROR;
		}

		if (item.getPrice() == null) {
			LOGGER.info("FeiPriceUpdateItemWriter - Skipping product unique ID : {}. price supplied is null",
					item.getUniqueId());
			return PriceUpdateStatus.INPUT_VALIDATION_ERROR;
		} else {
			BigDecimal bdPrice = new BigDecimal(item.getPrice());
			if (bdPrice.compareTo(BigDecimal.ZERO) == 0) {
				LOGGER.error(
						"FeiPriceUpdateItemWriter - Skipping product unique ID : {}. price cannot be a zero amount",
						item.getUniqueId());
				return PriceUpdateStatus.PRICE_VALIDATION_ERROR;
			}
		}

		// Validate mpid and uniqueId
		if (!feiPriceUpdateService.isValidMpidUniqueId(item.getMpid(),  item.getUniqueId())) {
			LOGGER.error("FeiPriceUpdateItemWriter - Invalid MPID: {}, UniqueId: {} combination",
					item.getMpid(),  item.getUniqueId());
			return PriceUpdateStatus.DATA_MATCH_ERROR;
		}

		// Validate FEI owned active status
		if (!item.getFeiOwnedActive()) {
			LOGGER.error("FeiPriceUpdateItemWriter - UniqueId: {} FEI owned inactive",
					item.getUniqueId());
			return PriceUpdateStatus.OWNED_INACTIVE_ERROR;
		}

		return PriceUpdateStatus.VALID;
	}

	/*
	 * Create a 2nd pro pricing record with cost calculations
	 */
	private FeiPriceUpdateItem buildProPricingRecord(FeiPriceUpdateItem item) {
		FeiPriceUpdateItem proPriceUpdateItem = (FeiPriceUpdateItem) SerializationUtils.clone(item);
		proPriceUpdateItem.setPricebookId(22);
		proPriceUpdateItem.setPrice(calculateProPricing(item));
		return proPriceUpdateItem;
	}

	/*
	 * calculate Pro pricing based on the following criteria:
	 * Lighting Product Base Category: PB22 = PC24 x .9
	 * Everything else: PB22 = PC24 x .97
	 * UMRP Skus: PB22 = PC24 (No discount)
	 */
	private Double calculateProPricing(FeiPriceUpdateItem item) {
		// Default to customer price
		BigDecimal proPrice = new BigDecimal(Double.toString(item.getPrice()));

		// if UMRP then pro pricing is same as customer.
		if (item.getUmrpId() == null) {
			if (item.getBaseCategoryId() != null && item.getBaseCategoryId() == LIGHTING_BASE_CATEGORY_ID) {
				LOGGER.debug("calculateProPricing - Applying pro pricing multiplier: [.9] to product unique ID: {}",
						item.getUniqueId());
				proPrice = proPrice.multiply(new BigDecimal(".9"));
			} else {
				LOGGER.debug("calculateProPricing - Applying pro pricing multiplier: [.97] to product unique ID: {}",
						item.getUniqueId());
				proPrice = proPrice.multiply(new BigDecimal(".97"));
			}
		} else {
			LOGGER.debug("calculateProPricing - No pro pricing multiplier for product unique ID: {}",
					item.getUniqueId());
		}

		return proPrice.doubleValue();
	}

	/*
	 * Calculate the profit margin.  If less than 14% we will reject this pricing update.
	 */
	private Boolean isValidProfitMargin(Double vendorCost, Double feiPrice) {

		BigDecimal vendorPrice = new BigDecimal(Double.toString(vendorCost));
		BigDecimal consumerPrice = new BigDecimal(Double.toString(feiPrice));
		BigDecimal margin = (new BigDecimal(1).subtract(vendorPrice.divide(consumerPrice,2, RoundingMode.HALF_DOWN)));

		LOGGER.debug("FeiPriceUpdateItemWriter/isValidProfitMargin - Vendor Cost: {}, Fei Price: {}, profit margin: {}",
				vendorCost, feiPrice, margin);

		return margin.compareTo(new BigDecimal(MINIMUM_PROFIT_MARGIN)) >= 0;
	}
}
