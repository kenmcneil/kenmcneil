package com.ferguson.cs.product.task.feipricefeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.feipricefeed")
@EnableFeignClients
public class FeiPriceFeedApplication {

	public static void main(String args[]) {
		SpringApplication.run(FeiPriceFeedApplication.class, args).close();
	}
}
