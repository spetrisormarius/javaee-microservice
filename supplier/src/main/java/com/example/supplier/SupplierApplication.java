package com.example.supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SupplierApplication {

	public static void main(String[] args) {

		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			System.out.println("mama");
		});

		try {
			SpringApplication.run(SupplierApplication.class, args);
		} catch (Throwable e) {
			System.out.println("mama");
		}
	}
}
