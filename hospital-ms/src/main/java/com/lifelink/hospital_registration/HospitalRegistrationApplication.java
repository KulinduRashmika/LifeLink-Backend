package com.lifelink.hospital_registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HospitalRegistrationApplication {
	public static void main(String[] args) {
		SpringApplication.run(HospitalRegistrationApplication.class, args);
		System.out.println("=========================================");
		System.out.println("Hospital Registration Backend Started!");
		System.out.println("Server running on: http://localhost:8083");
		System.out.println("=========================================");
	}
}
