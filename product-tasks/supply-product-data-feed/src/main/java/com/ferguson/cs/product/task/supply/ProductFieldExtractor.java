package com.ferguson.cs.product.task.supply;

import org.springframework.batch.item.file.transform.FieldExtractor;

import com.ferguson.cs.product.task.supply.model.Product;

public class ProductFieldExtractor implements FieldExtractor<Product> {
	@Override
	public Object[] extract(Product product) {
		Object[] fields = new Object[14];
		fields[0] = product.getUniqueId();
		fields[1] = product.getProductId();
		fields[2] = product.getManufacturer();
		fields[3] = product.getFinish();
		fields[4] = product.getSku();
		fields[5] = product.getUpc();
		fields[6] = product.getMpn();
		fields[7] = product.getMsrp();
		fields[8] = product.getIsLtl();
		fields[9] = product.getFreightCost();
		fields[10] = product.getType();
		fields[11] = product.getApplication();
		fields[12] = product.getHandleType();
		fields[13] = product.getStatus();

		return fields;
	}
}
