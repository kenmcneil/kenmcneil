package com.ferguson.cs.product.task.brand.ge.task;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

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

	@Autowired
	private GeProductApiService geProductApiService;
	
	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;
	
	GeProductSearchCriteria criteria = null;
	
	@Override
	protected void doReadPage() {
		if (results == null) {
			results = new CopyOnWriteArrayList<GeProduct>();
		} else {
			results.clear();
		}
		if (criteria == null ) {
			criteria = (GeProductSearchCriteria)((Stack)executionContext.get("dimensionFilter")).pop();

		} 
		results.addAll(fetchGeProducts(criteria));
		
	}
	

	private List<GeProduct> fetchGeProducts(GeProductSearchCriteria criteria) {
		criteria.setNumberOfProducts(getPageSize());
		if (getCurrentItemCount() > 1) {
			criteria.setStartIndex(getCurrentItemCount());
		}
		
		GeProductSearchResult result = geProductApiService.getResults(criteria);
		System.out.println("Search for " + criteria.getNavDescriptors()+ " (start index " + criteria.getStartIndex() + " (page number " + getPage() + " got a page of " + result.getProducts().size() 
	    + " (results"); 
		return result.getProducts();
		
		
	} 
	private List<GeProduct> fetchGeProducts(String dimFilter) {
		String query = dimFilter+"&Nrpp="+getPageSize()+"&No="+(getCurrentItemCount());
		GeProductSearchResult result = geProductApiService.getResults(query );
		System.out.println("Search for " + query + " (page number " + getPage() + " got a page of " + result.getProducts().size() 
	    + " (results"); 
		return result.getProducts();
		
		
	}
	
	
	
	@Override
	protected void doJumpToPage(int itemIndex) {
		//DO NOTHING.  This just reads a page of the products
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	}

	

	
}

