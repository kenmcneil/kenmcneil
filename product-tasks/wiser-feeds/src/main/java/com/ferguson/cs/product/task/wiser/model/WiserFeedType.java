package com.ferguson.cs.product.task.wiser.model;

import com.ferguson.cs.utilities.StringMappedEnum;

public enum WiserFeedType implements StringMappedEnum{
	PRODUCT_CATALOG_FEED("wiserProductCatalogFeed"), COMPETITOR_FEED("wiserCompetitorFeed");

	private String stringValue;

	WiserFeedType(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
