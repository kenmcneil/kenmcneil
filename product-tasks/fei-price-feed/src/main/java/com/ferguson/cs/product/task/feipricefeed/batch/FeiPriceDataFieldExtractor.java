package com.ferguson.cs.product.task.feipricefeed.batch;

import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.lang.NonNull;

import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;

public class FeiPriceDataFieldExtractor implements FieldExtractor<FeiPriceData> {

	private final String location;


	public FeiPriceDataFieldExtractor(String location) {
		this.location = location;
	}

	@Override
	@NonNull
	public Object[] extract(FeiPriceData item) {
		Object[] fields = new Object[2];

		fields[0] = String.format("%s*P#%s",location,item.getMpid());
		fields[1] = item.getPrice();
		return fields;
	}
}
