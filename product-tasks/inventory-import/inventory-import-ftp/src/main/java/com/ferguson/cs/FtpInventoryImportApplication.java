package com.ferguson.cs;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {"com.ferguson.cs"})
public class FtpInventoryImportApplication {
	public static void main(String args[]) {
		SpringApplication.run(FtpInventoryImportApplication.class, args).close();
	}
}

