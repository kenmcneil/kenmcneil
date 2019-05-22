package com.ferguson.cs.vendor.quickship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {"com.ferguson.cs.vendor.quickship"})
public class VendorQuickShipApplication {
	public static void main(String args[]) {
		SpringApplication.run(VendorQuickShipApplication.class, args).close();
	}
}
