package com.ferguson.cs.product.task.brand.ge.task;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.ferguson.cs.product.task.brand.ge.services.GeProductApiHelper;
import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ge_products.api.GeProduct;


public class GeProductProcessor  implements ItemProcessor<GeProduct, BrandProduct> {
	
	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;
	
	@Override
	public BrandProduct process(GeProduct geProduct) throws Exception {
		return GeProductApiHelper.convertToProductDistributionProduct(geProduct, executionContext.getInt("systemSourceId"));
		
	}

}
 