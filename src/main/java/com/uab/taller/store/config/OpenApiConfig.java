package com.uab.taller.store.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "Store Bank API";
    private static final String API_DESCRIPTION = "REST API for the Store Bank banking platform.";
    private static final String API_VERSION = "1.0.0";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version(API_VERSION)
                .contact(new Contact().name("Store Bank Team"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"));
    }
}
