package com.ferguson.cs.product.task.brand.ge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication(scanBasePackages= {"com.ferguson.cs.product.task.brand"})
public class GeProductsTasksApplication {
	public static void main(String args[]) {
		SpringApplication.run(GeProductsTasksApplication.class, args).close();
	}

}
