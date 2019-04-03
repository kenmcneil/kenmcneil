package com.ferguson.cs.product.task.inventory.batch;

import java.io.IOException;

import javax.xml.bind.ValidationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferguson.cs.product.task.inventory.model.manhattan.LocationAvailabilityResponse;

public class ManhattanJsonItemReader extends AbstractItemCountingItemStreamItemReader<LocationAvailabilityResponse> implements ResourceAwareItemReaderItemStream<LocationAvailabilityResponse> {
	private static final Log LOGGER = LogFactory.getLog(JsonItemReader.class);
	private Resource resource;
	private JsonParser jsonParser;
	private ObjectMapper objectMapper = new ObjectMapper();
	private int totalJsonFileCount;
	boolean hasError = false;


	@Override
	@Autowired
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	protected LocationAvailabilityResponse doRead() throws Exception {

		try {
			return JsonToken.START_OBJECT.equals(jsonParser.nextToken()) ? this.objectMapper
					.readValue(jsonParser, LocationAvailabilityResponse.class) : null;
		} catch (IOException e) {
			hasError = true;
			throw new ValidationException("Error reading JSON record");
		}
	}

	@Override
	protected void doOpen() throws Exception {
		JsonFactory jsonFactory = new JsonFactory();
		jsonParser = jsonFactory.createParser(resource.getFile());
		//Consume start object token
		JsonToken jsonToken = jsonParser.nextToken();
		if(!JsonToken.START_OBJECT.equals(jsonToken)) {
			hasError = true;
			throw new ValidationException("JSON file does not start with START_OBJECT token");
		}
		//Read file until end object token or LocationAvailabilityResponse array is encountered
		while (!JsonToken.END_OBJECT.equals(jsonParser.nextToken())) {
			String fieldName = jsonParser.getCurrentName();
			if("TotalCount".equals(fieldName)){
				jsonToken = jsonParser.nextToken();
				totalJsonFileCount = jsonParser.getIntValue();
			} else if ("LocationAvailabilityResponse".equals(fieldName)) {
				//Consume array start token and leave loop
				jsonToken = jsonParser.nextToken();
				break;
			} else if(JsonToken.END_OBJECT.equals(jsonParser.nextToken())) {
				hasError = true;
				throw new ValidationException("JSON terminated before LocationAvailabilityResponse found");
			}
		}
		if (!JsonToken.START_ARRAY.equals(jsonToken)) {
			hasError = true;
			throw new ValidationException("LocationAvailabiltyResponse is not an array");
		}

	}

	@Override
	protected void doClose() throws Exception {
		jsonParser.close();
	}

	@AfterStep
	public void afterStep(StepExecution stepExecution) {
		int currentJsonFileCount = stepExecution.getJobExecution().getExecutionContext().getInt("currentJsonFileCount",0);
		stepExecution.getJobExecution().getExecutionContext().putInt("currentJsonFileCount", currentJsonFileCount+1);
		Integer totalJsonFileCount = (Integer)stepExecution.getJobExecution().getExecutionContext().get("totalJsonFileCount");
		if(totalJsonFileCount == null) {
			stepExecution.getJobExecution().getExecutionContext().putInt("totalJsonFileCount",this.totalJsonFileCount);
		}
	}
}
