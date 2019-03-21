package com.ferguson.cs.product.task.inventory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class ManhattanInboundSettings {
	private String manhattanInputFile;
	private String manhattanOutputFile;

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
}
