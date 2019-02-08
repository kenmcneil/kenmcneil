package com.ferguson.cs.product.task.supply;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {"com.ferguson.cs.product.task.supply"})
public class SupplyProductDataFeedApplication {
	public static void main(String args[]) {
		SpringApplication.run(SupplyProductDataFeedApplication.class, args).close();
	}
}

