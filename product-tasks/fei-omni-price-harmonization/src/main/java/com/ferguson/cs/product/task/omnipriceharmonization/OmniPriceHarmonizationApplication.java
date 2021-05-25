package com.ferguson.cs.product.task.omnipriceharmonization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.omnipriceharmonization")
@EnableFeignClients
public class OmniPriceHarmonizationApplication {

	public static void main(String args[]) {
		SpringApplication.run(OmniPriceHarmonizationApplication.class, args).close();
	}
}
