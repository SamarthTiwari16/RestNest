package com.rentnest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI rentNestOpenApi() {
        return new OpenAPI().info(new Info()
                .title("RentNest API")
                .version("v1")
                .description("Privacy-first rental housing platform API."));
    }
}
