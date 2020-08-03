package com.ferguson.cs.product.task.brand.ge.task;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ferguson.cs.product.task.brand.ge.services.GeProductApiService;
import com.ge_products.api.GeProduct;
import com.ge_products.api.GeProductSearchCriteria;
import com.ge_products.api.GeProductSearchResult;

public class GeProductReader extends AbstractPagingItemReader<GeProduct> implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeProductReader.class);

	@Autowired
	private GeProductApiService geProductApiService;

	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;

	GeProductSearchCriteria criteria = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void doReadPage() {
		if (results == null) {
			results = new CopyOnWriteArrayList<GeProduct>();
		} else {
			results.clear();
		}
		if (criteria == null) {
			criteria = (GeProductSearchCriteria)((Stack<Object>)executionContext.get("dimensionFilter")).pop();
		}
		results.addAll(fetchGeProducts(criteria));
	}

	private List<GeProduct> fetchGeProducts(GeProductSearchCriteria criteria) {
		criteria.setNumberOfProducts(getPageSize());
		criteria.setStartIndex(getCurrentItemCount() - 1);

		GeProductSearchResult result = geProductApiService.getResults(criteria);
		LOGGER.info("Search for {} (start index {} got a page of {} results)",
				criteria.getNavDescriptors(), criteria.getStartIndex(), getPage(), result.getProducts().size());
		return result.getProducts();
	}

	@Override
	protected void doJumpToPage(int itemIndex) {
		// DO NOTHING. This just reads a page of the products
	}

}
