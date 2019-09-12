package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;

public class ProductConversionBucket implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer productUniqueId;
	private ConversionBucket conversionBucket;

	public Integer getProductUniqueId() {
		return productUniqueId;
	}

	public void setProductUniqueId(Integer productUniqueId) {
		this.productUniqueId = productUniqueId;
	}

	public ConversionBucket getConversionBucket() {
		return conversionBucket;
	}

	public void setConversionBucket(ConversionBucket conversionBucket) {
		this.conversionBucket = conversionBucket;
	}
}
