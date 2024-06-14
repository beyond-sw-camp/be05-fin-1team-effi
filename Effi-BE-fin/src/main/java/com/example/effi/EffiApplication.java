package com.example.effi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EffiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EffiApplication.class, args);
	}

}
