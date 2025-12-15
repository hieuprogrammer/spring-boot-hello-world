package dev.hieu.springboothelloworld.configuration;

import dev.hieu.springboothelloworld.service.feature.FeatureFlag;
import dev.hieu.springboothelloworld.service.feature.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Logs README.md content to the console on application startup.
 * <p>
 * This component is only created if {@code feature-flags.readme-logger.enabled=true} in application.yml.
 * When disabled, the bean won't be picked up by ApplicationContext at all.
 */
@Component
@ConditionalOnProperty(name = "feature-flags.readme-logger.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
@Order(1) // Run before DataInitializer
@RequiredArgsConstructor
public class ReadmeLogger implements CommandLineRunner {

    private final FeatureFlagService featureFlagService;

    @Override
    public void run(String... args) {
        // Guarded by feature flag – disabled by default
        if (!featureFlagService.isEnabled(FeatureFlag.README_LOGGER)) {
            log.debug("README logger feature flag is disabled. Skipping README logging.");
            return;
        }

        try {
            // Try to find README.md in the project root
            // First, try relative to current working directory
            Path readmePath = Paths.get("README.md");

            // If not found, try common locations
            if (!Files.exists(readmePath)) {
                // Try parent directory (if running from target/classes)
                readmePath = Paths.get("../README.md");
            }
            if (!Files.exists(readmePath)) {
                // Try project root from user.dir
                String userDir = System.getProperty("user.dir");
                readmePath = Paths.get(userDir, "README.md");
            }

            if (Files.exists(readmePath)) {
                log.info("=".repeat(80));
                log.info("README.md Content:");
                log.info("=".repeat(80));

                try (BufferedReader reader = Files.newBufferedReader(readmePath, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info(line);
                    }
                }

                log.info("=".repeat(80));
            } else {
                log.warn("README.md file not found. Searched in: {}", readmePath.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Error reading README.md: {}", e.getMessage(), e);
        }
    }
}
