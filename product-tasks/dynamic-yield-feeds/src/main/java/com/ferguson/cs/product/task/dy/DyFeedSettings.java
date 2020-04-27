package com.ferguson.cs.product.task.dy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("dy")
@Component
public class DyFeedSettings {
	private String ftpUrl;
	private Integer ftpPort;
	private String ftpRoot;
	private String ftpPrivateKey;
	private String tempFilePrefix = "productfeed";
	private String tempFileSuffix = ".csv";
	private Map<Integer, String> siteUsername = new HashMap<>();
	private List<String> excludedBrands;

	public List<String> getExcludedBrands() {
		return excludedBrands;
	}

	public void setExcludedBrands(List<String> excludedBrands) {
		this.excludedBrands = excludedBrands;
	}

	private List<Integer> restrictionPolicies = new ArrayList<>();

	private List<Integer> stores = new ArrayList<>();

	public String getTempFileSuffix() {
		return tempFileSuffix;
	}

	public void setTempFileSuffix(String tempFileSuffix) {
		this.tempFileSuffix = tempFileSuffix;
	}

	public String getTempFilePrefix() {
		return tempFilePrefix;
	}

	public void setTempFilePrefix(String tempFilePrefix) {
		this.tempFilePrefix = tempFilePrefix;
	}

	public String getFtpPrivateKey() {
		return ftpPrivateKey;
	}

	public void setFtpPrivateKey(String ftpPrivateKey) {
		this.ftpPrivateKey = ftpPrivateKey;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpRoot() {
		return ftpRoot;
	}

	public void setFtpRoot(String ftpRoot) {
		this.ftpRoot = ftpRoot;
	}

	public Map<Integer, String> getSiteUsername() {
		return siteUsername;
	}

	public void setSiteUsername(Map<Integer, String> siteUsername) {
		this.siteUsername = siteUsername;
	}

	public List<Integer> getStores() {
		return stores;
	}

	public void setStores(List<Integer> stores) {
		this.stores = stores;
	}
	public List<Integer> getRestrictionPolicies() {
		return restrictionPolicies;
	}

	public void setRestrictionPolicies(List<Integer> restrictionPolicies) {
		this.restrictionPolicies = restrictionPolicies;
	}
}
