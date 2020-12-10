package com.ferguson.cs.product.task.feitrilogympidsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.feitrilogympidsync")

public class FeiTrilogyMpidSyncApplication {
	public static void main(String args[]) {
		SpringApplication.run(FeiTrilogyMpidSyncApplication.class, args).close();
	}
}
