package com.example.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    // Ref: https://www.bezkoder.com/spring-boot-swagger-3/

    @Value("${bertramcapital.openapi.dev-url}")
    private String devUrl;


    @Bean
    public OpenAPI myOpenAPI() {

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("a.living.keane@gmail.com");
        contact.setName("Andrea Keane");

        Info info = new Info()
                .title("Bertram Capital Coffee API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage group coffee orders.");

        return new OpenAPI().info(info);
    }
}
