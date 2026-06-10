package com.example.phase2;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Phase2Application {

	@Bean
	public Clock applicationClock() {
		return Clock.systemUTC();
	}

	public static void main(String[] args) {
		SpringApplication.run(Phase2Application.class, args);
	}

}