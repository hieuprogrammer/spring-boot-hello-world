package dev.hieu.springboothelloworld.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Hieu");
        contact.setEmail("hieu@example.com");
        
        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");
        
        Info info = new Info();
        info.setTitle("Todo Management API");
        info.setVersion("1.0.0");
        info.setDescription("REST API for managing todos with pagination, sorting, and filtering capabilities");
        info.setContact(contact);
        info.setLicense(license);
        
        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(info);
        
        return openAPI;
    }
}

