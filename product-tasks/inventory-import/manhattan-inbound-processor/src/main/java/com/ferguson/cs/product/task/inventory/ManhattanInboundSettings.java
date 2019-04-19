package com.ferguson.cs.product.task.inventory;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("inventory-import.manhattan")
public class ManhattanInboundSettings {
	private String manhattanInputFilePath;
	private String manhattanOutputFilePath;
	private Map<String, String> locationIdDcMap;
	private Long jobCompletionTimeOutInMilliseconds;

	public String getManhattanInputFilePath() {
		return manhattanInputFilePath;
	}

	public void setManhattanInputFilePath(String manhattanInputFilePath) {
		this.manhattanInputFilePath = manhattanInputFilePath;
	}

	public String getManhattanOutputFilePath() {
		return manhattanOutputFilePath;
	}

	public void setManhattanOutputFilePath(String manhattanOutputFilePath) {
		this.manhattanOutputFilePath = manhattanOutputFilePath;
	}

	public Map<String, String> getLocationIdDcMap() {
		return locationIdDcMap;
	}

	public void setLocationIdDcMap(Map<String, String> locationIdDcMap) {
		this.locationIdDcMap = locationIdDcMap;
	}

	public Long getJobCompletionTimeOutInMilliseconds() {
		return jobCompletionTimeOutInMilliseconds;
	}

	public void setJobCompletionTimeOutInMilliseconds(Long jobCompletionTimeOutInMilliseconds) {
		this.jobCompletionTimeOutInMilliseconds = jobCompletionTimeOutInMilliseconds;
	}
}
