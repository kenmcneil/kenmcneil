package com.ferguson.cs.product.task.brand.ge.services;

import com.ge_products.api.GeProductSearchCriteria;
import com.ge_products.api.GeProductSearchResult;

public interface GeProductApiService {

	GeProductSearchResult getResults(GeProductSearchCriteria criteria);

	GeProductSearchResult getDimensions(GeProductSearchCriteria criteria);
}
