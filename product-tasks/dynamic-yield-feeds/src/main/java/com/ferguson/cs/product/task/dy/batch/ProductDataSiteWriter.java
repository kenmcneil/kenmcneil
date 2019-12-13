package com.ferguson.cs.product.task.dy.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.ferguson.cs.product.task.dy.domain.SiteProductFileResource;
import com.ferguson.cs.product.task.dy.model.DynamicYieldProduct;

public class ProductDataSiteWriter implements ItemStreamWriter<DynamicYieldProduct> {

	private ExecutionContext executionContext;

	@Value("#{stepExecution.jobExecution.id}")
	private Long jobExecutionId;

	@Qualifier("dyProductFileResource")
	private SiteProductFileResource dyProductFileResource;

	private LineAggregator<DynamicYieldProduct> lineAggregator;
	private Map<Integer, SiteResource> siteResources = new HashMap<>();
	private String[] headerNames;
	private String delimeter;

	private String[] getHeaderNames() {
		return headerNames;
	}

	public void setHeaderNames(String[] headerNames) {
		this.headerNames = headerNames;
	}

	private String getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	private LineAggregator<DynamicYieldProduct> getLineAggregator() {
		return lineAggregator;
	}

	public void setLineAggregator(LineAggregator<DynamicYieldProduct> lineAggregator) {
		this.lineAggregator = lineAggregator;
	}

	private SiteProductFileResource getDyProductFileResource() {
		return dyProductFileResource;
	}

	public void setDyProductFileResource(SiteProductFileResource dyProductFileResource) {
		this.dyProductFileResource = dyProductFileResource;
	}

	@PostConstruct
	public void initializeStoreAccounts() {
		for (Map.Entry<Integer, Resource> entry : dyProductFileResource.getSiteFileMap().entrySet()) {
			SiteResource sr = new SiteResource();
			siteResources.put(entry.getKey(), sr);
		}
	}

	@Override
	public void write(List<? extends DynamicYieldProduct> items) throws Exception {
		// collecting the items by site this allows the process to
		// send a list of product objects to the store file writer vs writing them one at a time
		Map<Integer, List<DynamicYieldProduct>> siteProducts = new HashMap<>();

		for (DynamicYieldProduct product : items) {
			for (Integer siteId : product.getSiteIds()) {
				List<DynamicYieldProduct> changeList = siteProducts.computeIfAbsent(siteId, k -> new ArrayList<>());
				changeList.add(product);
			}
		}

		// Write the site products
		for (Map.Entry<Integer, List<DynamicYieldProduct>> entry : siteProducts.entrySet()) {
			Integer siteId = entry.getKey();
			List<DynamicYieldProduct> productsToWrite = entry.getValue();

			// Write the items to file
			if (dyProductFileResource.getSiteFileMap().get(siteId) != null && productsToWrite.size() > 0) {
				siteResources.get(siteId).writer.write(productsToWrite);
			}
		}
	}

	/**
	 * Get a FlatFileItemWriter for the passed resource using the lineAggregator set in this class
	 *
	 * @param resource
	 * @return writer
	 */
	private FlatFileItemWriter<DynamicYieldProduct> createFlatFileItemWriter(Resource resource) {
		//Create writer instance
		FlatFileItemWriter<DynamicYieldProduct> writer = new FlatFileItemWriter<>();
		//Set output file location
		writer.setAppendAllowed(true);
		writer.setShouldDeleteIfExists(true);
		writer.setResource(resource);
		writer.setLineAggregator(getLineAggregator());
		writer.setHeaderCallback(w -> w.write(String.join(getDelimeter(), getHeaderNames())));
		writer.open(executionContext);
		return writer;
	}

	@Override
	public void open(ExecutionContext executionContext) {
		this.executionContext = executionContext;
		for (Map.Entry<Integer, SiteResource> entry : siteResources.entrySet()) {
			Integer siteId = entry.getKey();
			SiteResource sr = entry.getValue();
			Resource resource = dyProductFileResource.getSiteFileMap().get(siteId);
			sr.resource = resource;
			sr.writer = createFlatFileItemWriter(resource);
		}
	}

	@Override
	public void close() {
		for (SiteResource resource : siteResources.values()) {
			resource.writer.close();
		}
	}

	@Override
	public void update(ExecutionContext ec) {
		for (SiteResource resource : siteResources.values()) {
			resource.writer.update(ec);
		}
	}

	private class SiteResource {
		Resource resource;
		FlatFileItemWriter<DynamicYieldProduct> writer;
	}

}