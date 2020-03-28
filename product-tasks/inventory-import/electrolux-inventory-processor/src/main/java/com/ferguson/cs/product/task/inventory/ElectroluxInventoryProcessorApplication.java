package com.ferguson.cs.product.task.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.inventory")
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product.task.inventory")
@EnableFeignClients
public class ElectroluxInventoryProcessorApplication {

	public static void main(String args[]) {
		SpringApplication.run(ElectroluxInventoryProcessorApplication.class, args).close();
	}
}
