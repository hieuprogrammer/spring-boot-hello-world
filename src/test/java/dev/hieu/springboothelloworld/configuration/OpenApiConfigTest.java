package dev.hieu.springboothelloworld.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    @InjectMocks
    private OpenApiConfig openApiConfig;

    @Test
    void customOpenAPI_ShouldReturnConfiguredOpenAPI() {
        // When
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        // Then
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        Info info = openAPI.getInfo();
        assertEquals("Todo Management API", info.getTitle());
        assertEquals("1.0.0", info.getVersion());
        assertNotNull(info.getDescription());
        assertNotNull(info.getContact());
        assertNotNull(info.getLicense());
        assertEquals("Hieu", info.getContact().getName());
        assertEquals("Apache 2.0", info.getLicense().getName());
    }
}

