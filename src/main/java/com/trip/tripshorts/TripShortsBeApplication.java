package com.trip.tripshorts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TripShortsBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripShortsBeApplication.class, args);
	}

}
