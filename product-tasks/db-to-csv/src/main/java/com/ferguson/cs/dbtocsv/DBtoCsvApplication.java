package com.ferguson.cs.dbtocsv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {"com.ferguson.cs.dbtocsv"})
public class DBtoCsvApplication  {
	public static void main(String args[]) {
		SpringApplication.run(DBtoCsvApplication.class, args).close();
	}
}