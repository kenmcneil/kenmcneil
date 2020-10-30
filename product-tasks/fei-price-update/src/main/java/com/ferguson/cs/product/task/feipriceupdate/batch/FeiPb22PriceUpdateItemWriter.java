package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.ContainerType;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPricingType;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceUpdateStatus;
import com.ferguson.cs.product.task.feipriceupdate.model.ProductStatus;

public class FeiPb22PriceUpdateItemWriter implements ItemWriter<FeiPriceUpdateItem> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiPb22PriceUpdateItemWriter.class);
	private static final int LIGHTING_BASE_CATEGORY_ID = 4;

	private final FeiPriceUpdateService feiPriceUpdateService;
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final BigDecimal profitMargin;

	public FeiPb22PriceUpdateItemWriter(
			FeiPriceUpdateService feiPriceUpdateService,
			FeiPriceUpdateSettings feiPriceUpdateSettings) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		this.profitMargin = BigDecimal.valueOf(this.feiPriceUpdateSettings.getPb22margin()).setScale(4,RoundingMode.HALF_EVEN);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends FeiPriceUpdateItem> items) throws Exception {

		for (FeiPriceUpdateItem item : (List<FeiPriceUpdateItem>) items) {

			// Default to success initially
			item.setStatusMsg("FEI PB22 Price updated");

			LOGGER.debug("FeiPb22PriceUpdateItemWriter - Processing Item uniqueId: {}, mpid:{}, consumer price: {}",
					item.getUniqueId(), item.getMpid(), item.getPrice());

			// Validate the record
			item.setPriceUpdateStatus(validPriceUpdateRecord(item));

			// Insert into temp table regardless of validation status.  Valid records will be passed on
			// to the costUploader.  Invalid records will go on the error report that is sent out.
			feiPriceUpdateService.insertTempPriceUpdateRecord(item);
		}
	}

	/*
	 * Perform record validation.  Error code will be stored in temp table and a CSV file will
	 * be sent out containing any errors that failed validation resulting in no price update.
	 */
	private PriceUpdateStatus validPriceUpdateRecord(FeiPriceUpdateItem item) {

		// Validate the supplied input record - must have an mmc.product.product.uniqueId match
		if (item.getUniqueId() == null) {
			LOGGER.error("FeiPb22PriceUpdateItemWriter - FEI Price update record must contain a uniqueId");
			item.setStatusMsg("INPUT_VALIDATION_ERROR - FEI input record missing uniqueId");
			return PriceUpdateStatus.INPUT_VALIDATION_ERROR;
		}

		// mpid is required
		if (item.getMpid() == null) {
			LOGGER.error("FeiPb22PriceUpdateItemWriter - FEI Price update record must contain a mpid");
			item.setStatusMsg("INPUT_VALIDATION_ERROR - FEI input record missing mpid");
			return PriceUpdateStatus.INPUT_VALIDATION_ERROR;
		}

		// Product must be FEI or Build owned
		if (item.getFeiOwnedProductId() == null &&
				(item.getFeiPricingType() == null || item.getFeiPricingType() != FeiPricingType.PERMANENT)) {
			LOGGER.debug("FeiPb22PriceUpdateItemWriter - Skipping product unique ID : {}. Not FEI or Build owned",
					item.getUniqueId());
			item.setStatusMsg("OWNED_LOOKUP_ERROR - Product not FEI or Build owned");
			return PriceUpdateStatus.OWNED_LOOKUP_ERROR;
		}

		// Supplied price is required and must be > 0
		if (item.getPrice() == null) {
			LOGGER.error("FeiPb22PriceUpdateItemWriter - Skipping product unique ID : {}. price supplied is null",
					item.getUniqueId());
			item.setStatusMsg("INPUT_VALIDATION_ERROR - FEI input record price = null");
			return PriceUpdateStatus.INPUT_VALIDATION_ERROR;
		} else {
			BigDecimal bdPrice = BigDecimal.valueOf(item.getPrice());
			if (bdPrice.compareTo(BigDecimal.ZERO) == 0) {
				LOGGER.error(
						"FeiPb22PriceUpdateItemWriter - Skipping product unique ID : {}. price cannot be a zero amount",
						item.getUniqueId());
				item.setStatusMsg("PRICE_VALIDATION_ERROR - FEI input record price = 0");
				return PriceUpdateStatus.PRICE_VALIDATION_ERROR;
			}
		}

		// Validate mpid and uniqueId
		if (!feiPriceUpdateService.isValidMpidUniqueId(item.getMpid(),  item.getUniqueId())) {
			LOGGER.debug("FeiPb22PriceUpdateItemWriter - Invalid MPID: {}, UniqueId: {} combination",
					item.getMpid(),  item.getUniqueId());
			item.setStatusMsg("DATA_MATCH_ERROR - No mpid/uniqueId match in product.feiMPID table");
			return PriceUpdateStatus.DATA_MATCH_ERROR;
		}

		// Validate FEI/Build owned active status
		if (!isProductActive(item)) {
			LOGGER.debug("FeiPb22PriceUpdateItemWriter - UniqueId: {} FEI/Build owned inactive",
					item.getUniqueId());
			item.setStatusMsg("OWNED_INACTIVE_ERROR - FEI/Build owned inactive");
			return PriceUpdateStatus.OWNED_INACTIVE_ERROR;
		}

		// Validate IMAP price.  If UMRP then price can not be below the IMAP price
		if (item.getUmrpId() != null && item.getMapPrice() != null) {
			BigDecimal feiPrice = BigDecimal.valueOf(item.getPrice()).setScale(4,RoundingMode.HALF_EVEN);
			BigDecimal floorPrice = BigDecimal.valueOf(item.getMapPrice()).setScale(4,RoundingMode.HALF_EVEN);

			if (feiPrice.compareTo(floorPrice) < 0 ) {
				LOGGER.debug("FeiPb22PriceUpdateItemWriter - UniqueId: {} - Fei Price: {} is less than IMAP price: {}",
						item.getUniqueId(),feiPrice.toString(),floorPrice.toString());
				item.setStatusMsg("IMAP_PRICE_ERROR - FEI price: " + feiPrice.toString() + " below MAP price: " + floorPrice.toString());
				return PriceUpdateStatus.IMAP_PRICE_ERROR;
			}
		}

		// Get preferred vendor cost.  This will be used to validate the margin.
		Double preferredVendorCost = feiPriceUpdateService.getPreferredVendorCost(item.getUniqueId());
		item.setPreferredVendorCost(preferredVendorCost);

		if (preferredVendorCost == null) {
			item.setStatusMsg("VENDOR_COST_LOOKUP_ERROR - No matching vendor preferred cost found fpr product uniqueId");
			return PriceUpdateStatus.VENDOR_COST_LOOKUP_ERROR;
		} else if (!isValidProfitMargin(preferredVendorCost, item)) {
			item.setStatusMsg("LOW_MARGIN_ERROR - PB22 Profit margin: " + item.getMargin() + " is below: " + this.profitMargin);
			return PriceUpdateStatus.LOW_MARGIN_ERROR;
		}

		// Validate that the PB22 price is not greater than the PB1 price. If there was a new PB1 price
		// in the current PB1 input file use that. Otherwise we will reference the PB1 price
		// from the pricebook_cost table.
		Double pb1Price = item.getNewPb1Price() == null ? item.getExistingPb1Price() : item.getNewPb1Price();

		if (pb1Price != null && item.getPrice() > pb1Price) {
			item.setStatusMsg("PRICE_VALIDATION_ERROR - PB22 price: " + item.getPrice() + " is greater than PB1 price: " + pb1Price);
			return PriceUpdateStatus.PRICE_VALIDATION_ERROR;
		}

		return PriceUpdateStatus.VALID;
	}

	private boolean isProductActive(FeiPriceUpdateItem item) {

		// pricingFeiOwned trumps if the product also exists in pricingFeiWhitelist so I'll
		// check it first
		if ( item.getFeiOwnedProductId() != null ) {
			return item.getFeiOwnedActive().booleanValue();
		}

		if (item.getContainerType() != null && item.getContainerType() != ContainerType.PRODUCT) {
			return false;
		}

		if (item.getProductStatus() != null &&
				(item.getProductStatus() == ProductStatus.STOCK ||
				item.getProductStatus() == ProductStatus.NONSTOCK)) {
			return true;
		}

		return false;
	}

	/*
	 * Calculate the profit margin.  If less than 10% (config item) we will reject this pricing update.
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
