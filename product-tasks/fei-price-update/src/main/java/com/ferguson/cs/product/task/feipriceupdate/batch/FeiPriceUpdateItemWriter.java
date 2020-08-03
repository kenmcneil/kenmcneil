package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceUpdateStatus;

public class FeiPriceUpdateItemWriter implements ItemWriter<FeiPriceUpdateItem> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiPriceUpdateItemWriter.class);
	private static final int LIGHTING_BASE_CATEGORY_ID = 4;

	private final FeiPriceUpdateService feiPriceUpdateService;
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final BigDecimal profitMargin;

	public FeiPriceUpdateItemWriter(
			FeiPriceUpdateService feiPriceUpdateService,
			FeiPriceUpdateSettings feiPriceUpdateSettings) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		this.profitMargin = BigDecimal.valueOf(this.feiPriceUpdateSettings.getMargin()).setScale(4,RoundingMode.HALF_EVEN);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends FeiPriceUpdateItem> items) throws Exception {

		for (FeiPriceUpdateItem item : (List<FeiPriceUpdateItem>) items) {

			// Default to success initially
			item.setStatusMsg("FEI Price updated");

			LOGGER.debug("FeiPriceUpdateItemWriter - Processing Item uniqueId: {}, mpid:{}, consumer price: {}",
					item.getUniqueId(), item.getMpid(), item.getPrice());

			PriceUpdateStatus validationStatus = validPriceUpdateRecord(item);
			Double preferredVendorCost = null;

			if (validationStatus == PriceUpdateStatus.VALID) {
				// Get preferred vendor cost.  This will be used to validate the margin.
				preferredVendorCost = feiPriceUpdateService.getPreferredVendorCost(item.getUniqueId());
				if (preferredVendorCost == null) {
					validationStatus = PriceUpdateStatus.VENDOR_COST_LOOKUP_ERROR;
					item.setStatusMsg("VENDOR_COST_LOOKUP_ERROR - No matching vendor preferred cost found fpr product uniqueId");
				} else if (!isValidProfitMargin(preferredVendorCost, item)) {
					validationStatus = PriceUpdateStatus.LOW_MARGIN_ERROR;
					item.setStatusMsg("LOW_MARGIN_ERROR - PB1 Profit margin: " + item.getMargin() + " is below: " + this.profitMargin);
				}

				item.setPreferredVendorCost(preferredVendorCost);
			}

			item.setStatus(validationStatus);

			feiPriceUpdateService.insertTempPriceUpdateRecord(item);

			// If the PB1 record passed validation build the PB22.  There are no additional validation
			// checks for PB22
			if (validationStatus == PriceUpdateStatus.VALID) {
				// Create a record for Pro pricing. Input file is customer pricing only
				FeiPriceUpdateItem proItem = buildProPricingRecord(item);
				LOGGER.debug("FeiPriceUpdateItemWriter - Pro price: {}", proItem.getPrice());
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
			item.setStatusMsg("INPUT_VALIDATION_ERROR - FEI input record missing uniqueId");
			return PriceUpdateStatus.INPUT_VALIDATION_ERROR;
		}

		if (item.getMpid() == null) {
			LOGGER.error("FeiPriceUpdateItemWriter - FEI Price update record must contain a mpid");
			item.setStatusMsg("INPUT_VALIDATION_ERROR - FEI input record missing mpid");
			return PriceUpdateStatus.INPUT_VALIDATION_ERROR;
		}

		// Not an error - This just means it won't be included in the update as it's not a FEI product
		if (item.getFeiOwnedProductId() == null) {
			LOGGER.debug("FeiPriceUpdateItemWriter - Skipping product unique ID : {}. Not FEI owned",
					item.getUniqueId());
			item.setStatusMsg("OWNED_LOOKUP_ERROR - Product not FEI owned");
			return PriceUpdateStatus.OWNED_LOOKUP_ERROR;
		}

		if (item.getPrice() == null) {
			LOGGER.error("FeiPriceUpdateItemWriter - Skipping product unique ID : {}. price supplied is null",
					item.getUniqueId());
			item.setStatusMsg("INPUT_VALIDATION_ERROR - FEI input record price = null");
			return PriceUpdateStatus.INPUT_VALIDATION_ERROR;
		} else {
			BigDecimal bdPrice = BigDecimal.valueOf(item.getPrice());
			if (bdPrice.compareTo(BigDecimal.ZERO) == 0) {
				LOGGER.error(
						"FeiPriceUpdateItemWriter - Skipping product unique ID : {}. price cannot be a zero amount",
						item.getUniqueId());
				item.setStatusMsg("PRICE_VALIDATION_ERROR - FEI input record price = 0");
				return PriceUpdateStatus.PRICE_VALIDATION_ERROR;
			}
		}

		// Validate mpid and uniqueId
		if (!feiPriceUpdateService.isValidMpidUniqueId(item.getMpid(),  item.getUniqueId())) {
			LOGGER.debug("FeiPriceUpdateItemWriter - Invalid MPID: {}, UniqueId: {} combination",
					item.getMpid(),  item.getUniqueId());
			item.setStatusMsg("DATA_MATCH_ERROR - No mpid/uniqueId match in product.feiMPID table");
			return PriceUpdateStatus.DATA_MATCH_ERROR;
		}

		// Validate FEI owned active status
		if (!item.getFeiOwnedActive()) {
			LOGGER.debug("FeiPriceUpdateItemWriter - UniqueId: {} FEI owned inactive",
					item.getUniqueId());
			item.setStatusMsg("OWNED_INACTIVE_ERROR - FEI owned inactive");
			return PriceUpdateStatus.OWNED_INACTIVE_ERROR;
		}

		// Validate IMAP price.  Fei price can not be below the IMAP price
		if (item.getMapPrice() != null) {
			BigDecimal feiPrice = BigDecimal.valueOf(item.getPrice()).setScale(4,RoundingMode.HALF_EVEN);
			BigDecimal floorPrice = BigDecimal.valueOf(item.getMapPrice()).setScale(4,RoundingMode.HALF_EVEN);

			if (feiPrice.compareTo(floorPrice) < 0 ) {
				LOGGER.debug("FeiPriceUpdateItemWriter - UniqueId: {} - Fei Price: {} is less than IMAP price: {}",
						item.getUniqueId(),feiPrice.toString(),floorPrice.toString());
				item.setStatusMsg("IMAP_PRICE_ERROR - FEI price: " + feiPrice.toString() + " below MAP price: " + floorPrice.toString());
				return PriceUpdateStatus.IMAP_PRICE_ERROR;
			}
		}

		return PriceUpdateStatus.VALID;
	}

	/*
	 * Create a 2nd pro pricing record with cost calculations
	 */
	private FeiPriceUpdateItem buildProPricingRecord(FeiPriceUpdateItem item) {
		FeiPriceUpdateItem proPriceUpdateItem = (FeiPriceUpdateItem) SerializationUtils.clone(item);
		proPriceUpdateItem.setPricebookId(22);
		proPriceUpdateItem.setPrice(calculateProPricing(proPriceUpdateItem));
		proPriceUpdateItem.setMargin(null);
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
		BigDecimal proPrice = BigDecimal.valueOf(item.getPrice());

		// if UMRP then pro pricing is same as customer.
		if (item.getUmrpId() == null) {
			if (item.getBaseCategoryId() != null && item.getBaseCategoryId() == LIGHTING_BASE_CATEGORY_ID) {
				LOGGER.debug("calculateProPricing - Applying pro pricing multiplier: [.9] to product unique ID: {}",
						item.getUniqueId());
				proPrice = proPrice.multiply(BigDecimal.valueOf(.9));

				item.setPriceCalculation("Lighting Category: .9");
			} else {
				LOGGER.debug("calculateProPricing - Applying pro pricing multiplier: [.97] to product unique ID: {}",
						item.getUniqueId());
				proPrice = proPrice.multiply(BigDecimal.valueOf(.97));

				item.setPriceCalculation(".97");
			}
		} else {
			LOGGER.debug("calculateProPricing - No pro pricing multiplier for product unique ID: {}",
					item.getUniqueId());
			item.setPriceCalculation("N/A umrpID exists");
		}

		return proPrice.doubleValue();
	}

	/*
	 * Calculate the profit margin.  If less than 14% we will reject this pricing update.
	 */
	private Boolean isValidProfitMargin(Double vendorCost, FeiPriceUpdateItem item) {

		BigDecimal vendorPrice = BigDecimal.valueOf(vendorCost);
		BigDecimal consumerPrice = BigDecimal.valueOf(item.getPrice());
		BigDecimal margin = (BigDecimal.valueOf(1.0).setScale(4).subtract(vendorPrice.divide(consumerPrice,4, RoundingMode.HALF_EVEN)));

		LOGGER.debug("FeiPriceUpdateItemWriter/isValidProfitMargin - Vendor Cost: {}, Fei Price: {}, profit margin: {}",
				vendorCost, item.getPrice(), margin);

		item.setMargin(margin.doubleValue());

		return margin.compareTo(profitMargin) >= 0;
	}
}
