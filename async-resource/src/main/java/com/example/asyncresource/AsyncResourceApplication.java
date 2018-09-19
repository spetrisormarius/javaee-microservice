package com.example.asyncresource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AsyncResourceApplication {

	public static void main(String[] args) {

		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			System.out.println("SYSTEM EXCEPTION Logging exception: " + e.getMessage());
		});

		SpringApplication.run(AsyncResourceApplication.class, args);
	}
}
