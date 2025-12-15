package dev.hieu.springboothelloworld.web.api;

import dev.hieu.springboothelloworld.service.feature.FeatureFlag;
import dev.hieu.springboothelloworld.service.feature.FeatureFlagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PingApiTest {

    @Mock
    private FeatureFlagService featureFlagService;

    @InjectMocks
    private PingApi pingApi;

    @Test
    void ping_WhenEnabled_ShouldReturnPong() {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.PING_API)).thenReturn(true);

        // When
        ResponseEntity<String> result = pingApi.ping();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("pong", result.getBody());
    }

    @Test
    void ping_WhenDisabled_ShouldReturnServiceUnavailable() {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.PING_API)).thenReturn(false);

        // When
        ResponseEntity<String> result = pingApi.ping();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
    }
}
