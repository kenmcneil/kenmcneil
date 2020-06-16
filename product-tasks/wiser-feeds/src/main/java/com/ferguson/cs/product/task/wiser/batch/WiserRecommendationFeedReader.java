package com.ferguson.cs.product.task.wiser.batch;

import java.io.File;

import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.ferguson.cs.product.task.wiser.model.WiserRecommendationData;
import com.ferguson.cs.product.task.wiser.service.WiserService;

public class WiserRecommendationFeedReader extends AbstractItemCountingItemStreamItemReader<WiserRecommendationData> implements
		ResourceAwareItemReaderItemStream<WiserRecommendationData>, InitializingBean {

	private final WiserService wiserService;
	private final File file;
	private FlatFileItemReader<WiserRecommendationData> csvReader;
	private MyBatisCursorItemReader<Double> currentPriceReader;

	public WiserRecommendationFeedReader(WiserService wiserService, String filePath) {
		this.wiserService = wiserService;
		this.file = new File(filePath);
		csvReader = new FlatFileItemReaderBuilder<WiserRecommendationData>().fieldSetMapper(new BeanWrapperFieldSetMapper<>()).targetType(WiserRecommendationData.class).name("internalWiserRecommendationFeedReader").linesToSkip(1).delimited().delimiter(",").names(new String[] {"uniqueId","pricebookId","cost"}).resource(new FileSystemResource(file)).build();
		currentPriceReader = new MyBatisCursorItemReader<>();
	}

	@Override
	protected WiserRecommendationData doRead() throws Exception {
		WiserRecommendationData wiserRecommendationData = csvReader.read();
		if(wiserRecommendationData == null) {
			return null;
		}
		wiserRecommendationData.setOldCost(wiserService.getCurrentPrice(wiserRecommendationData.getUniqueId(),wiserRecommendationData.getPricebookId()));
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
