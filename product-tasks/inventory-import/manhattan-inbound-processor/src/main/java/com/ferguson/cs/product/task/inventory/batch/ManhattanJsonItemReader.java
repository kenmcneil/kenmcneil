package com.ferguson.cs.product.task.inventory.batch;

import javax.xml.bind.ValidationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

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


	@Override
	@Autowired
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	protected LocationAvailabilityResponse doRead() throws Exception {

		return JsonToken.START_OBJECT.equals(jsonParser.nextToken()) ? this.objectMapper
				.readValue(jsonParser, LocationAvailabilityResponse.class) : null;
	}

	@Override
	protected void doOpen() throws Exception {
		JsonFactory jsonFactory = new JsonFactory();
		jsonParser = jsonFactory.createParser(resource.getFile());
		//Consume start object token
		JsonToken jsonToken = jsonParser.nextToken();
		if(!JsonToken.START_OBJECT.equals(jsonToken)) {
			throw new ValidationException("JSON file does not start with START_OBJECT token");
		}
		//Read file until end object token or LocationAvailabilityResponse array is encountered
		while (!JsonToken.END_OBJECT.equals(jsonParser.nextToken())) {
			String fieldName = jsonParser.getCurrentName();
			if ("LocationAvailabilityResponse".equals(fieldName)) {
				//Consume array start token and leave loop
				jsonToken = jsonParser.nextToken();
				break;
			}
			//If we didn't encounter LocationAvailabilityResponse, consume irrelevant field and make sure it isn't the end
			if(JsonToken.END_OBJECT.equals(jsonParser.nextToken())) {
				throw new ValidationException("JSON terminated before LocationAvailabilityResponse found");
			}
		}
		if (!JsonToken.START_ARRAY.equals(jsonToken)) {
			throw new ValidationException("LocationAvailabiltyResponse is not an array");
		}

	}

	@Override
	protected void doClose() throws Exception {
		jsonParser.close();
	}
}
