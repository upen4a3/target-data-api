package com.example.targetdataapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;


/** Main Class to start the application**/

@EnableAutoConfiguration
@SpringBootApplication
public class TargetDataApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TargetDataApiApplication.class, args);
	}
	
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
	         return new MethodValidationPostProcessor();
	}
}
