package com.ferguson.cs.product.task.feipricefeed.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.feipricefeed.FeiPriceSettings;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceDataStatus;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPricingType;
import com.ferguson.cs.product.task.feipricefeed.service.FeiPriceService;

public class FeiPriceDataItemProcessor implements ItemProcessor<FeiPriceData,FeiPriceData>, StepExecutionListener {

	private static final Logger LOG = LoggerFactory.getLogger(FeiPriceDataItemProcessor.class);
	private final FeiPriceService feiPriceService;
	private final FeiPriceSettings feiPriceSettings;
	private Set<Integer> stalePromoUniqueIds;

	public FeiPriceDataItemProcessor(FeiPriceService feiPriceService, FeiPriceSettings feiPriceSettings) {
		this.feiPriceService = feiPriceService;
		this.feiPriceSettings = feiPriceSettings;
	}


	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		stalePromoUniqueIds = new HashSet<>(feiPriceService.getStalePromoPriceProducts());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		//Intentionally no-op
		return stepExecution.getExitStatus();
	}

	@Override
	public FeiPriceData process(FeiPriceData item) throws Exception {
		if(item.getUniqueId() == null || item.getPrice() == null || item.getMpid() == null) {
			return null;
		}
		try {
			if(FeiPriceDataStatus.OVERRIDE != item.getFeiPriceDataStatus() && !checkMargin(item)) {
				item.setFeiPriceDataStatus(FeiPriceDataStatus.LOW_MARGIN);
			}

		} catch (NumberFormatException e) {
			LOG.error("Product with uniqueId {} has invalid price, '{}' is not a valid price.",item.getUniqueId(),item.getPrice());
			return null;
		}
		if(FeiPricingType.PROMO == item.getFeiPricingType() && stalePromoUniqueIds.contains(item.getUniqueId())) {
			//Set price to a negative number to mark promo product as stale
			item.setPrice("-1.00");
		}
		if(item.getFeiPriceDataStatus() == null) {
			item.setFeiPriceDataStatus(FeiPriceDataStatus.VALID);
		}
		return item;
	}

	private boolean checkMargin(FeiPriceData feiPriceData) throws NumberFormatException{
		BigDecimal price = BigDecimal.valueOf(Double.parseDouble(feiPriceData.getPrice()));
		BigDecimal vendorCost = BigDecimal.valueOf(feiPriceData.getPreferredVendorCost());
		return BigDecimal.valueOf(1.0).subtract(vendorCost.divide(price, RoundingMode.HALF_EVEN)).compareTo(BigDecimal.valueOf(feiPriceSettings.getMinimumMargin())) >= 0;

	}
}
