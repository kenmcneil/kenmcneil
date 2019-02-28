package com.ferguson.cs.product.task.inventory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("inventory-import.email")
@Configuration
public class EmailInventoryImportSettings {
	private Integer emailPort;
	private String emailHostName;
	private String emailUsername;
	private String emailPassword;
	private Boolean safeMode;

	public Integer getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(Integer emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailHostName() {
		return emailHostName;
	}

	public void setEmailHostName(String emailHostName) {
		this.emailHostName = emailHostName;
	}

	public String getEmailUsername() {
		return emailUsername;
	}

	public void setEmailUsername(String emailUsername) {
		this.emailUsername = emailUsername;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public Boolean getSafeMode() {
		return safeMode;
	}

	public void setSafeMode(Boolean safeMode) {
		this.safeMode = safeMode;
	}
}
