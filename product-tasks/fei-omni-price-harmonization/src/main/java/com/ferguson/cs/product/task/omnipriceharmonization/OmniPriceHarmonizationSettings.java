package com.ferguson.cs.product.task.omnipriceharmonization;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("omni-price-harmonization")
@Component
public class OmniPriceHarmonizationSettings {
	private String incomingFilePath;
	private String archivePath;

	public String getIncomingFilePath() {
		return incomingFilePath;
	}

	public void setIncomingFilePath(String incomingFilePath) {
		this.incomingFilePath = incomingFilePath;
	}

	public String getArchivePath() {
		return archivePath;
	}

	public void setArchivePath(String archivePath) {
		this.archivePath = archivePath;
	}
}
