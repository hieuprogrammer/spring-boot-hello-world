package dev.hieu.springboothelloworld.web.api;

import dev.hieu.springboothelloworld.configuration.FeatureFlag;
import dev.hieu.springboothelloworld.configuration.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST API for viewing and updating feature flags at runtime.
 */
@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
public class FeatureFlagApi {

    private final FeatureFlagService featureFlagService;

    @GetMapping
    public Map<String, Boolean> getAll() {
        return featureFlagService.getAll()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
    }

    @PutMapping("/{name}")
    public ResponseEntity<Void> update(
            @PathVariable String name,
            @RequestParam boolean enabled) {

        FeatureFlag flag = FeatureFlag.valueOf(name);
        featureFlagService.setEnabled(flag, enabled);
        return ResponseEntity.noContent().build();
    }
}


