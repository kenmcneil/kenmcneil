package com.ferguson.cs.product.task.stylyze;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.task.stylyze.model.StylyzeInputProduct;

public class StylyzeSettings {

	private List<StylyzeInputProduct> inputData;
	private String baseUrl;
	private String baseImageUrl;

	@PostConstruct
	public void init() {
		Assert.state(!CollectionUtils.isEmpty(inputData), "inputData must not be null or empty.");
		Assert.state(!StringUtils.isEmpty(baseUrl), "baseUrl must not be null or empty.");
		Assert.state(!StringUtils.isEmpty(baseImageUrl), "baseImageUrl must not be null or empty.");
	}

	public List<StylyzeInputProduct> getInputData() {
		return inputData;
	}

	public void setInputData(List<StylyzeInputProduct> inputData) {
		this.inputData = inputData;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseImageUrl() {
		return baseImageUrl;
	}

	public void setBaseImageUrl(String baseImageUrl) {
		this.baseImageUrl = baseImageUrl;
	}

}