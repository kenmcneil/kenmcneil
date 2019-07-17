package com.ferguson.cs.product.task.brand.ge.task;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.service.ProductDistributionService;

;

public class GeProductWriter implements ItemWriter<BrandProduct> {

	@Autowired
	protected ProductDistributionService productDistributionService;

	public void write(List<? extends BrandProduct> products) throws Exception {
		productDistributionService.saveProducts((List<BrandProduct>) products);
	}

}