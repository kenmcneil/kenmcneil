package com.ferguson.cs.product.task.image;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SupplyProductImageImportTask {

	public static void main(String[] args) {
		SpringApplication.run(SupplyProductImageImportTask.class, args).close();
	}

}
