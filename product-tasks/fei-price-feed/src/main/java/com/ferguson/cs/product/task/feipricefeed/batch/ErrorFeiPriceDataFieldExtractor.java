package com.ferguson.cs.product.task.feipricefeed.batch;

import org.springframework.batch.item.file.transform.FieldExtractor;

import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;

public class ErrorFeiPriceDataFieldExtractor implements FieldExtractor<FeiPriceData> {
	@Override
	public Object[] extract(FeiPriceData item) {
		Object[] fields = new Object[6];

		fields[0] = item.getUniqueId();
		fields[1] = item.getMpid();
		fields[2] = item.getPrice();
		fields[3] = item.getBrand();
		fields[4] = item.getStatus();
		fields[5] = item.getFeiPriceDataStatus().getStringValue();

		return fields;
	}
}
