package com.ferguson.cs.product.task.feipriceupdate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.feipriceupdate")
public class FeiPriceUpdateApplication {
	public static void main(String args[]) {
		SpringApplication.run(FeiPriceUpdateApplication.class, args).close();
	}
}
