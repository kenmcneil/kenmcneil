package com.ferguson.cs.product.task.inventory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("inventory-import.manhattan")
public class ManhattanInboundSettings {
	private String manhattanOutputFilePath;
	private Long jobCompletionTimeOutInMilliseconds;

	public String getManhattanOutputFilePath() {
		return manhattanOutputFilePath;
	}

	public void setManhattanOutputFilePath(String manhattanOutputFilePath) {
		this.manhattanOutputFilePath = manhattanOutputFilePath;
	}

	public Long getJobCompletionTimeOutInMilliseconds() {
		return jobCompletionTimeOutInMilliseconds;
	}

	public void setJobCompletionTimeOutInMilliseconds(Long jobCompletionTimeOutInMilliseconds) {
		this.jobCompletionTimeOutInMilliseconds = jobCompletionTimeOutInMilliseconds;
	}
}
