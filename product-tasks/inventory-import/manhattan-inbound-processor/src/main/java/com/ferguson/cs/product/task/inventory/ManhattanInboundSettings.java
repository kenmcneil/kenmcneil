package com.ferguson.cs.product.task.inventory;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("inventory-import.manhattan")
public class ManhattanInboundSettings {
	private String manhattanInputFile;
	private String manhattanOutputFile;
	private Map<String, String> locationIdDcMap;

	public String getManhattanInputFile() {
		return manhattanInputFile;
	}

	public void setManhattanInputFile(String manhattanInputFile) {
		this.manhattanInputFile = manhattanInputFile;
	}

	public String getManhattanOutputFile() {
		return manhattanOutputFile;
	}

	public void setManhattanOutputFile(String manhattanOutputFile) {
		this.manhattanOutputFile = manhattanOutputFile;
	}

	public Map<String, String> getLocationIdDcMap() {
		return locationIdDcMap;
	}

	public void setLocationIdDcMap(Map<String, String> locationIdDcMap) {
		this.locationIdDcMap = locationIdDcMap;
	}
}
