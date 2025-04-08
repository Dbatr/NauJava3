package ru.denis.NauJava3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NauJava3Application {

	public static void main(String[] args) {
		System.out.println("Swagger: http://localhost:8080/swagger-ui/index.html");
		System.out.println("OpenAPI: http://localhost:8080/api-docs");
		SpringApplication.run(NauJava3Application.class, args);
	}

}
