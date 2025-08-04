package com.jobpulse.auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Value("${app.openapi.prod-url:http://16.171.9.26:8089}")
        private String prodUrl;

        @Value("${app.openapi.dev-url:http://localhost:8080}")
        private String devUrl;

        @Value("${app.openapi.title:JobPulse Auth Service API}")
        private String title;

        @Value("${app.openapi.version:1.0.0}")
        private String version;

        @Value("${app.openapi.description:Authentication and authorization service for JobPulse platform}")
        private String description;

        @Bean
        public OpenAPI customOpenAPI() {
                Components components = new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication"));
                
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
                        .servers(createServerList())
                        .components(components);
        }

        private List<Server> createServerList() {
                List<Server> servers = new ArrayList<>();

                if (!devUrl.isEmpty()) {
                        servers.add(new Server()
                                        .url(devUrl)
                                        .description("Development Server"));
                }

                if (!prodUrl.isEmpty()) {
                        servers.add(new Server()
                                        .url(prodUrl)
                                        .description("Production Server"));
                }

                return servers;
        }
}
