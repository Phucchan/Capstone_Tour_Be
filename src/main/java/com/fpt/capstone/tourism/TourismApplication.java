package com.fpt.capstone.tourism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TourismApplication {

	public static void main(String[] args) {
		SpringApplication.run(TourismApplication.class, args);
	}

}
