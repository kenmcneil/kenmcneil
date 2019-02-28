package com.ferguson.cs.product.task.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication(scanBasePackages= {"com.ferguson.cs.product.task.inventory"})
@EnableIntegration
public class EmailInventoryImportTaskApplication {
	public static void main(String args[]) {
		SpringApplication.run(EmailInventoryImportTaskApplication.class, args).close();
	}
}
