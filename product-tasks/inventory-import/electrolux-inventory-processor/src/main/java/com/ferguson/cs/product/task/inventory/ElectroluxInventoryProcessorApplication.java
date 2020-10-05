package com.ferguson.cs.product.task.inventory;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.inventory")
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product.task.inventory")
public class ElectroluxInventoryProcessorApplication {

	public static void main(String args[]) {
		new SpringApplicationBuilder(ElectroluxInventoryProcessorApplication.class)
				.web(WebApplicationType.NONE)
				.run(args)
				.close();
	}
}
