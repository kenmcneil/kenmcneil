package com.ferguson.cs.product.task.feipricefeed.batch;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPricingType;
import com.ferguson.cs.product.task.feipricefeed.service.FeiPriceService;

public class FeiPriceDataItemProcessor implements ItemProcessor<FeiPriceData,FeiPriceData>, StepExecutionListener {

	private static final Logger LOG = LoggerFactory.getLogger(FeiPriceDataItemProcessor.class);
	private final FeiPriceService feiPriceService;
	private Set<Integer> stalePromoUniqueIds;

	public FeiPriceDataItemProcessor(FeiPriceService feiPriceService) {
		this.feiPriceService = feiPriceService;
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
			Double.parseDouble(item.getPrice());
		} catch (NumberFormatException e) {
			LOG.error("Product with uniqueId {} has invalid price, '{}' is not a valid price.",item.getUniqueId(),item.getPrice());
			return null;
		}
		if(FeiPricingType.PROMO == item.getFeiPricingType() && stalePromoUniqueIds.contains(item.getUniqueId())) {
			//Set price to a negative number to mark promo product as stale
			item.setPrice("-1.00");
		}
		return item;
	}
}
