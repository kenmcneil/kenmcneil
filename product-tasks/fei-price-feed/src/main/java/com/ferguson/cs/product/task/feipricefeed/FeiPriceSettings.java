package com.ferguson.cs.product.task.feipricefeed;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("fei-price")
@Component
public class FeiPriceSettings {
	private Map<String,String> locations;
	private String temporaryFilePath;
	private String storageFilePath;
	private List<String> whiteLabelBrands;
	private String imapFilePath;

	public Map<String, String> getLocations() {
		return locations;
	}

	public void setLocations(Map<String, String> locations) {
		this.locations = locations;
	}

	public String getTemporaryFilePath() {
		return temporaryFilePath;
	}

	public void setTemporaryFilePath(String temporaryFilePath) {
		this.temporaryFilePath = temporaryFilePath;
	}

	public List<String> getWhiteLabelBrands() {
		return whiteLabelBrands;
	}

	public void setWhiteLabelBrands(List<String> whiteLabelBrands) {
		this.whiteLabelBrands = whiteLabelBrands;
	}

	public String getStorageFilePath() {
		return storageFilePath;
	}

	public void setStorageFilePath(String storageFilePath) {
		this.storageFilePath = storageFilePath;
	}

	public String getImapFilePath() {
		return imapFilePath;
	}

	public void setImapFilePath(String imapFilePath) {
		this.imapFilePath = imapFilePath;
	}
}
