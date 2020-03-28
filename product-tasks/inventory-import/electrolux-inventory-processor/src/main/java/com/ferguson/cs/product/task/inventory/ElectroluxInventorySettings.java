package com.ferguson.cs.product.task.inventory;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("electrolux")
@Component
public class ElectroluxInventorySettings {
	private String apiUrl;
	private String apiKey;
	private Map<Integer,String> vendorUidWarehouseMap;
	private String customerId;
	private String fileNamePrefix;
	private String apiUsername;
	private String apiPassword;

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Map<Integer,String> getVendorUidWarehouseMap() {
		return vendorUidWarehouseMap;
	}

	public void setVendorUidWarehouseMap(Map<Integer,String> vendorUidWarehouseMap) {
		this.vendorUidWarehouseMap = vendorUidWarehouseMap;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public String getApiUsername() {
		return apiUsername;
	}

	public void setApiUsername(String apiUsername) {
		this.apiUsername = apiUsername;
	}

	public String getApiPassword() {
		return apiPassword;
	}

	public void setApiPassword(String apiPassword) {
		this.apiPassword = apiPassword;
	}
}
