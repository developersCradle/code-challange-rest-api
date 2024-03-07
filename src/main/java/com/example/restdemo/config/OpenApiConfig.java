package com.example.restdemo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name = "Rest API Demo"),
				description = "OpenAPI documentation for REST CRUD Demo",
				title = "OpenAPI - REST CRUD Demo",
				version = "1.0")
		)
public class OpenApiConfig {

}
