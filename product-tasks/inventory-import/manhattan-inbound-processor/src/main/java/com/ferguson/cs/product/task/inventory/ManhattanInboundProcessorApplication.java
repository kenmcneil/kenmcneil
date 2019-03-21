package com.ferguson.cs.product.task.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= "com.ferguson.cs.product.task.inventory")
public class ManhattanInboundProcessorApplication {

	public static void main(String args[]) {
		SpringApplication.run(ManhattanInboundProcessorApplication.class,args).close();
	}
}
