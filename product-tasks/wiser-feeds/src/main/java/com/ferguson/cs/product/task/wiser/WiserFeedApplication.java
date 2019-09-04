package com.ferguson.cs.product.task.wiser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.wiser")
@EnableFeignClients
public class WiserFeedApplication {

	public static void main(String args[]) {
		SpringApplication.run(WiserFeedApplication.class, args).close();
	}
}