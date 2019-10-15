package com.ferguson.cs.product.task.dy.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.dy.model.ProductData;


public class DynamicYieldProductDataProcessor implements ItemProcessor<ProductData, ProductData>, StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}

	@Override
	public ProductData process(ProductData item) throws Exception {

		if (!isValidAndIncluded(item)) {
			return null;
		}

		return item;
	}

	private boolean isValidAndIncluded(ProductData productData) {
		return (productData != null
				&& productData.getSku() != null
				&& productData.getGroupId() != null
				&& productData.getName() != null
				&& productData.getUrl() != null
				&& productData.getPrice() != null
				&& productData.getInStock() != null
				&& productData.getImageUrl() != null
				&& productData.getCategories() != null);
	}
}
