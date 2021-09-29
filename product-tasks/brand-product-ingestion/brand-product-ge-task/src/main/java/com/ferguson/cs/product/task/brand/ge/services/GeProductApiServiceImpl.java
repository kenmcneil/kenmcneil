package com.ferguson.cs.product.task.brand.ge.services;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferguson.cs.product.task.brand.ge.aws.AwsClientHttpRequestFactory;
import com.ferguson.cs.product.task.brand.ge.aws.AwsRequestSigner;
import com.ferguson.cs.product.task.brand.ge.aws.AwsVersion4RequestSigner;
import com.ferguson.cs.product.task.brand.ge.aws.GeProductApiSettings;
import com.ge_products.api.GeProductSearchCriteria;
import com.ge_products.api.GeProductSearchResult;

@Service
public class GeProductApiServiceImpl implements GeProductApiService {

	private static final Log LOGGER = LogFactory.getLog(GeProductApiServiceImpl.class);

	private RestTemplateBuilder restTemplateBuilder;
	private GeProductApiSettings settings;
	private MetricsService metricsService;

	public GeProductApiServiceImpl(RestTemplateBuilder restTemplateBuilder,
	                               GeProductApiSettings settings,
	                               MetricsService metricsService) {
		this.restTemplateBuilder = restTemplateBuilder;
		this.settings = settings;
		this.metricsService = metricsService;
	}

	private RestTemplate restTemplate;

	private ObjectMapper objectMapper;

	@PostConstruct
	private void postConstruct() {
		restTemplate = restTemplateBuilder.requestFactory(this::getClientHttpRequestFactory).build();
		objectMapper = new ObjectMapper();
	}

	private AwsClientHttpRequestFactory getClientHttpRequestFactory() {
		AwsRequestSigner signer = new AwsVersion4RequestSigner(
				settings.getRegion(),
				settings.getApiKey(),
				settings.getApiSecretKey(),
				settings.getService()
		);
		signer.setEnforceHost(true);
		return new AwsClientHttpRequestFactory(signer);
	}

	private String executeQuery(String url, String query) throws RuntimeException {

		String response = null;
		try {
			response = restTemplate.getForObject(url + query, String.class);
		} catch (RestClientException rce) {
			String errorMessage = "Rest exception '" + rce.getMessage() + "' occurred for GE Product data query: " + query;
			LOGGER.error(errorMessage);
			metricsService.noticeError(errorMessage);
			metricsService.noticeError(rce);
			throw rce;
		}
		if (response == null || response.equals("Error occurred while fetching information... Please try again...")) {
			String errorMessage = "Error occurred while fetching GE Product data for query: " + query;
			LOGGER.error(errorMessage);
			metricsService.noticeError(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		return response;
	}

	/**
	 * Convert a GE Product API response string into a JSON
	 * node structure using Jackson objectMapper. 
	 * 
	 * @param response - GE Product API response string to convert
	 * @return - JsonNode object
	 */
	private JsonNode convertResultsToJson(String response) {

		JsonNode node = null;
		try {
			node = objectMapper.readTree(response);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			metricsService.noticeError(e);
		}
		return node;
	}

	@Override
	public GeProductSearchResult getResults(GeProductSearchCriteria criteria) {
		// Get the search query string from the search criteria
		String query = GeProductApiHelper.getQueryStringFromSearchCriteria(criteria);
		String response = executeQuery(settings.getResults(), query);
		return GeProductApiHelper.getResultFromJsonNode(convertResultsToJson(response), metricsService);

	}

	@Override
	public GeProductSearchResult getDimensions(GeProductSearchCriteria criteria) {
		// Get the search query string from the search criteria
		String query = GeProductApiHelper.getQueryStringFromSearchCriteria(criteria);
		String response = executeQuery(settings.getDimensions(), query);
		return GeProductApiHelper.getResultFromJsonNode(convertResultsToJson(response), metricsService);
	}

}
