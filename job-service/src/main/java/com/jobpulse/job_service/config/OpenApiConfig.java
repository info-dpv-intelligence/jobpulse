package com.jobpulse.job_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.ArrayList;

@Configuration
public class OpenApiConfig {

    @Value("${app.openapi.prod-url:http://16.171.9.26:8084}")
    private String prodUrl;

    @Value("${app.openapi.dev-url:http://localhost:8081}")
    private String devUrl;

    @Value("${app.openapi.title:JobPulse Job Service API}")
    private String title;

    @Value("${app.openapi.version:1.0.0}")
    private String version;

    @Value("${app.openapi.description:Job posting and management service for JobPulse platform}")
    private String description;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .contact(new Contact()
                                .name("JobPulse Team")
                                .email("dineshparadesi94@gmail.com")
                                .url("https://github.com/info-dpv-intelligence/jobpulse"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")));
    }
}
