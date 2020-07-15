package com.ferguson.cs.product.task.wiser.batch;

import java.io.File;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.ferguson.cs.product.task.wiser.model.CostUploadData;

public class WiserRecommendationFeedReader extends AbstractItemCountingItemStreamItemReader<CostUploadData> implements
		ResourceAwareItemReaderItemStream<CostUploadData>, InitializingBean {

	private final List<Integer> recommendationUniqueIds;
	private FlatFileItemReader<CostUploadData> csvReader;

	public WiserRecommendationFeedReader(String filePath, List<Integer> recommendationUniqueIds) {
		File file = new File(filePath);
		this.recommendationUniqueIds = recommendationUniqueIds;
		csvReader = new FlatFileItemReaderBuilder<CostUploadData>().fieldSetMapper(new BeanWrapperFieldSetMapper<>()).targetType(CostUploadData.class).name("internalWiserRecommendationFeedReader").linesToSkip(1).delimited().delimiter(",").names(new String[] {"uniqueId","pricebookId","cost"}).resource(new FileSystemResource(file)).build();

	}

	@Override
	protected CostUploadData doRead() throws Exception {
		CostUploadData wiserRecommendationData = csvReader.read();
		if(wiserRecommendationData == null) {
			return null;
		}
		recommendationUniqueIds.add(wiserRecommendationData.getUniqueId());

		return wiserRecommendationData;
	}

	@Override
	protected void doOpen() throws Exception {

	}

	@Override
	public void open(ExecutionContext executionContext) {
		csvReader.open(executionContext);
		super.open(executionContext);
	}

	@Override
	protected void doClose() throws Exception {
		csvReader.close();
	}


	@Override
	public void setResource(Resource resource) {
		csvReader.setResource(resource);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		csvReader.afterPropertiesSet();
	}
}
