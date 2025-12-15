package dev.hieu.springboothelloworld.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test demonstrating WireMock usage for mocking external HTTP services.
 * This test shows how to use WireMock to mock external API calls that your application might make.
 */
class ExternalServiceIntegrationTest {

    private WireMockServer wireMockServer;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Start WireMock server on a random port
        wireMockServer = new WireMockServer(
                WireMockConfiguration.options()
                        .dynamicPort()
        );
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void mockGetRequest_ShouldReturnMockedResponse() throws IOException, InterruptedException {
        // Given - Configure WireMock to stub a GET request
        stubFor(get(urlEqualTo("/api/external/todos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "data": [
                                        {"id": "1", "title": "Mocked Todo 1"},
                                        {"id": "2", "title": "Mocked Todo 2"}
                                    ],
                                    "total": 2
                                }
                                """)));

        // When - Make actual HTTP request to WireMock server
        String baseUrl = "http://localhost:" + wireMockServer.port();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/external/todos"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Verify the response
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertTrue(response.body().contains("Mocked Todo 1"));
        assertTrue(response.body().contains("Mocked Todo 2"));

        // Verify that the stub was called
        verify(getRequestedFor(urlEqualTo("/api/external/todos")));
    }

    @Test
    void mockPostRequest_ShouldReturnMockedResponse() throws IOException, InterruptedException {
        // Given - Configure WireMock to stub a POST request
        stubFor(post(urlEqualTo("/api/external/todos"))
                .withRequestBody(containing("Test Todo"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id": "123",
                                    "title": "Test Todo",
                                    "status": "created"
                                }
                                """)));

        // When - Make actual HTTP POST request to WireMock server
        String baseUrl = "http://localhost:" + wireMockServer.port();
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "title", "Test Todo",
                "description", "Test Description"
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/external/todos"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Verify the response
        assertEquals(HttpStatus.CREATED.value(), response.statusCode());
        assertTrue(response.body().contains("Test Todo"));
        assertTrue(response.body().contains("123"));

        // Verify that the stub was called with the correct body
        verify(postRequestedFor(urlEqualTo("/api/external/todos"))
                .withRequestBody(containing("Test Todo")));
    }

    @Test
    void mockRequest_WithQueryParameters_ShouldMatchCorrectly() throws IOException, InterruptedException {
        // Given - Configure WireMock to stub a request with query parameters
        stubFor(get(urlPathEqualTo("/api/external/todos"))
                .withQueryParam("status", equalTo("PENDING"))
                .withQueryParam("page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "data": [{"id": "1", "status": "PENDING"}],
                                    "total": 1
                                }
                                """)));

        // When - Make request with query parameters
        String baseUrl = "http://localhost:" + wireMockServer.port();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/external/todos?status=PENDING&page=0"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Verify the response
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertTrue(response.body().contains("PENDING"));

        // Verify that the stub was called with correct query parameters
        verify(getRequestedFor(urlPathEqualTo("/api/external/todos"))
                .withQueryParam("status", equalTo("PENDING"))
                .withQueryParam("page", equalTo("0")));
    }

    @Test
    void mockRequest_WithHeaders_ShouldMatchCorrectly() throws IOException, InterruptedException {
        // Given - Configure WireMock to stub a request requiring specific headers
        stubFor(get(urlEqualTo("/api/external/todos"))
                .withHeader("Authorization", equalTo("Bearer token123"))
                .withHeader("X-API-Key", equalTo("api-key-123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "data": [],
                                    "total": 0
                                }
                                """)));

        // When - Make request with headers
        String baseUrl = "http://localhost:" + wireMockServer.port();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/external/todos"))
                .header("Authorization", "Bearer token123")
                .header("X-API-Key", "api-key-123")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Verify the response
        assertEquals(HttpStatus.OK.value(), response.statusCode());

        // Verify that the stub was called with correct headers
        verify(getRequestedFor(urlEqualTo("/api/external/todos"))
                .withHeader("Authorization", equalTo("Bearer token123"))
                .withHeader("X-API-Key", equalTo("api-key-123")));
    }

    @Test
    void mockRequest_WithErrorResponse_ShouldReturnError() throws IOException, InterruptedException {
        // Given - Configure WireMock to return an error response
        stubFor(get(urlEqualTo("/api/external/todos/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "error": "Not Found",
                                    "message": "Todo with id 999 not found"
                                }
                                """)));

        // When - Make request that will return error
        String baseUrl = "http://localhost:" + wireMockServer.port();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/external/todos/999"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Verify the error response
        assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
        assertTrue(response.body().contains("Not Found"));
        assertTrue(response.body().contains("999"));

        verify(getRequestedFor(urlEqualTo("/api/external/todos/999")));
    }

    @Test
    void mockRequest_WithDelay_ShouldSimulateSlowResponse() throws IOException, InterruptedException {
        // Given - Configure WireMock to add a delay
        stubFor(get(urlEqualTo("/api/external/todos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(1000) // 1 second delay
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "data": [],
                                    "total": 0
                                }
                                """)));

        // When - Make request and measure time
        String baseUrl = "http://localhost:" + wireMockServer.port();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/external/todos"))
                .GET()
                .build();

        long startTime = System.currentTimeMillis();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        long endTime = System.currentTimeMillis();

        // Then - Verify response and that delay was applied
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertTrue((endTime - startTime) >= 1000, "Response should take at least 1 second");

        verify(getRequestedFor(urlEqualTo("/api/external/todos")));
    }

    @Test
    void mockRequest_WithResponseTemplate_ShouldUseTemplate() throws IOException, InterruptedException {
        // Given - Configure WireMock with a dynamic path stub (static response for simplicity)
        stubFor(get(urlEqualTo("/api/external/todos/456"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id": "456",
                                    "title": "Todo 456",
                                    "status": "ACTIVE"
                                }
                                """)));

        // When - Make request with dynamic path
        String baseUrl = "http://localhost:" + wireMockServer.port();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/external/todos/456"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Verify the templated response
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertTrue(response.body().contains("456"));
        assertTrue(response.body().contains("Todo 456"));

        verify(getRequestedFor(urlEqualTo("/api/external/todos/456")));
    }
}

