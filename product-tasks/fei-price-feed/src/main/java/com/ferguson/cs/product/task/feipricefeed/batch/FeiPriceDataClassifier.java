package com.ferguson.cs.product.task.feipricefeed.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceDataStatus;

public class FeiPriceDataClassifier implements Classifier <FeiPriceData, ItemWriter<? super FeiPriceData>> {

	private static final long serialVersionUID = 1L;
	private final ItemWriter<FeiPriceData> validFeiPriceDataItemWriter;
	private final ItemWriter<FeiPriceData> errorFeiPriceDataItemWriter;

	public FeiPriceDataClassifier(ItemWriter<FeiPriceData> validFeiPriceDataItemWriter, ItemWriter<FeiPriceData> errorFeiPriceDataItemWriter) {
		this.validFeiPriceDataItemWriter = validFeiPriceDataItemWriter;
		this.errorFeiPriceDataItemWriter = errorFeiPriceDataItemWriter;
	}

	@Override
	public ItemWriter<? super FeiPriceData> classify(FeiPriceData classifiable) {
		if(classifiable.getFeiPriceDataStatus() == FeiPriceDataStatus.VALID || classifiable.getFeiPriceDataStatus() == FeiPriceDataStatus.OVERRIDE) {
			return validFeiPriceDataItemWriter;
		} else {
			return errorFeiPriceDataItemWriter;
		}
	}
}
