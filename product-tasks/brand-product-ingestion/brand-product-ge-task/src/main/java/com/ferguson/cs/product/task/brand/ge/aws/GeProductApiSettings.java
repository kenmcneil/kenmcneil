package com.ferguson.cs.product.task.brand.ge.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ge-products")
public class GeProductApiSettings {

	private String results;
	private String dimensions;
	private String host;
	private String apiKey;
	private String apiSecretKey;
	private String region;
	private String service;
	private String documentBaseUrl;
	private String videoBaseUrl;
	

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public String getDimensions() {
		return dimensions;
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiSecretKey() {
		return apiSecretKey;
	}

	public void setApiSecretKey(String apiSecretKey) {
		this.apiSecretKey = apiSecretKey;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getDocumentBaseUrl() {
		return documentBaseUrl;
	}

	public void setDocumentBaseUrl(String documentBaseUrl) {
		this.documentBaseUrl = documentBaseUrl;
	}
	
	public String getVideoBaseUrl() {
		return videoBaseUrl;
	}

	public void setVideoBaseUrl(String videoBaseUrl) {
		this.videoBaseUrl = videoBaseUrl;
	}
}
