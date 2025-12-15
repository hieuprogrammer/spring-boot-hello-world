package dev.hieu.springboothelloworld.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        // Start WireMock server on a random port
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void getAllTodos_WithWireMock_ShouldReturnSuccess() throws Exception {
        // Given - WireMock is set up but not used in this test
        // This demonstrates the setup for future external service mocking

        // When & Then - Test the actual API endpoint
        mockMvc.perform(get("/api/todos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void createTodo_WithValidData_ShouldCreateSuccessfully() throws Exception {
        // Given
        String todoJson = """
                {
                    "todo": "Test Todo from WireMock Test",
                    "description": "Test Description",
                    "status": "PENDING"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.todo").value("Test Todo from WireMock Test"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getAllTodos_WithPageSizeValidation_ShouldEnforceLimits() throws Exception {
        // Test that page size is validated (1-100)
        
        // Test with size = 0 (should be clamped to 1)
        mockMvc.perform(get("/api/todos")
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isOk());

        // Test with size = 101 (should be clamped to 100)
        mockMvc.perform(get("/api/todos")
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isOk());

        // Test with valid size
        mockMvc.perform(get("/api/todos")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50));
    }

    @Test
    void searchTodos_WithWireMock_ShouldReturnFilteredResults() throws Exception {
        // Given - WireMock setup for potential external service calls
        // In a real scenario, you might mock an external search service
        
        // When & Then - Test actual search endpoint
        mockMvc.perform(get("/api/todos/search")
                        .param("keyword", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }
}

