package com.ferguson.cs.product.task.inventory;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("inventory-import.manhattan")
public class ManhattanInboundSettings {
	private Long jobCompletionTimeOutInMilliseconds;
	private Map<String, FileTransferProperties> fileTransferProperties;


	public Long getJobCompletionTimeOutInMilliseconds() {
		return jobCompletionTimeOutInMilliseconds;
	}

	public void setJobCompletionTimeOutInMilliseconds(Long jobCompletionTimeOutInMilliseconds) {
		this.jobCompletionTimeOutInMilliseconds = jobCompletionTimeOutInMilliseconds;
	}

	public Map<String, FileTransferProperties> getFileTransferProperties() {
		return fileTransferProperties;
	}

	public void setFileTransferProperties(Map<String, FileTransferProperties> fileTransferProperties) {
		this.fileTransferProperties = fileTransferProperties;
	}
}
