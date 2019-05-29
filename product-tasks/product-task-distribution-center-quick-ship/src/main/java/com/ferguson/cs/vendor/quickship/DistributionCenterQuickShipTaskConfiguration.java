package com.ferguson.cs.vendor.quickship;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ferguson.cs.vendor.quickship.model.DistributionCenterQuickShipSettings;

@Configuration
public class DistributionCenterQuickShipTaskConfiguration {
	@Bean
	@ConfigurationProperties(prefix = "distribution-center-quick-ship")
	public DistributionCenterQuickShipSettings vendorQuickShipSettings() {
		return new DistributionCenterQuickShipSettings();
	}
}
