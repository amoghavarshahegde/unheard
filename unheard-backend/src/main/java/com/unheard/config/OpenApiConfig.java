package com.unheard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI unheardOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UNHEARD API")
                        .description("Explainable Disaster-Response Decision-Support Platform - MVP")
                        .version("1.0.0")
                        .contact(new Contact().name("UNHEARD Team")));
    }
}
