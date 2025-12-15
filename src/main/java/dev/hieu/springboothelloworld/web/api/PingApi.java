package dev.hieu.springboothelloworld.web.api;

import dev.hieu.springboothelloworld.configuration.FeatureFlag;
import dev.hieu.springboothelloworld.configuration.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
@RequiredArgsConstructor
public class PingApi {

    private final FeatureFlagService featureFlagService;

    @GetMapping
    public ResponseEntity<String> ping() {
        if (!featureFlagService.isEnabled(FeatureFlag.PING_API)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok("pong");
    }
}
