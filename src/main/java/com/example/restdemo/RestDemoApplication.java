package com.example.restdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;



//OpenApi Config, should be in own class(own config class), for now its here.
@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name = "Rest API Demo"),
				description = "OpenAPI documentation for REST CRUD Demo",
				title = "OpenAPI - REST CRUD Demo",
				version = "1.0")
		)
@SpringBootApplication
public class RestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestDemoApplication.class, args);
	}


}


