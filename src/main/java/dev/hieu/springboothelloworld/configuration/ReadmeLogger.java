package dev.hieu.springboothelloworld.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
@Order(1) // Run before DataInitializer
public class ReadmeLogger implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
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
