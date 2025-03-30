package ru.denis.NauJava3.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки OpenAPI (Swagger) документации.
 * Определяет основную информацию об API, которая будет отображаться
 * в Swagger UI интерфейсе.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Personal Finance Manager API",
                version = "1.0.0",
                description = "API documentation for Personal Finance Manager application"
        ))
public class OpenApiConfig {

}
