package com.ferguson.cs.product;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("inventory-import")
@Configuration
public class InventoryImportSettings {
	private String inventoryDirectory;

	public String getInventoryDirectory() {
		return inventoryDirectory;
	}

	public void setInventoryDirectory(String inventoryDirectory) {
		this.inventoryDirectory = inventoryDirectory;
	}
}
