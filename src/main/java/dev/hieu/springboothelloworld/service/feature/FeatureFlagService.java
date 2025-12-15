package dev.hieu.springboothelloworld.service.feature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple in-memory feature flag service.
 * <p>
 * Initial values are read from {@code application.yml} properties, then can be
 * toggled at runtime via the {@link dev.hieu.springboothelloworld.web.api.FeatureFlagApi}.
 * <p>
 * Runtime changes are also persisted to a small JSON file on disk so that
 * feature flag state survives application restarts.
 */
@Service
public class FeatureFlagService {

    // Persisted flag state; relative to project root in development.
    // Note: when running from a packaged JAR this path will be relative to the working directory
    // and the directory may need to exist / be writable in the deployment environment.
    private static final Path STATE_FILE = Path.of("src", "main", "resources", "data", "feature-flags-state.json");

    private final Map<FeatureFlag, Boolean> flags = new EnumMap<>(FeatureFlag.class);
    private final ObjectMapper objectMapper;
    private final boolean persistenceEnabled;

    public FeatureFlagService(
            @Value("${feature-flags.ping-api.enabled:true}") boolean pingApiEnabled,
            @Value("${feature-flags.readme-logger.enabled:false}") boolean readmeLoggerEnabled,
            @Value("${feature-flags.todo-write-api.enabled:true}") boolean todoWriteApiEnabled,
            @Value("${feature-flags.todo-search-api.enabled:true}") boolean todoSearchApiEnabled,
            @Value("${feature-flags.persistence.enabled:true}") boolean persistenceEnabled,
            ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
        this.persistenceEnabled = persistenceEnabled;

        // 1) Seed defaults from application.yml
        for (FeatureFlag flag : FeatureFlag.values()) {
            boolean initialEnabled = switch (flag) {
                case PING_API -> pingApiEnabled;
                case README_LOGGER -> readmeLoggerEnabled;
                case TODO_WRITE_API -> todoWriteApiEnabled;
                case TODO_SEARCH_API -> todoSearchApiEnabled;
            };
            flags.put(flag, initialEnabled);
        }

        // 2) Overlay any previously persisted state from disk (if enabled)
        if (this.persistenceEnabled) {
            loadFromDisk();
        }
    }

    public Map<FeatureFlag, Boolean> getAll() {
        return Collections.unmodifiableMap(flags);
    }

    public boolean isEnabled(FeatureFlag flag) {
        return Boolean.TRUE.equals(flags.get(flag));
    }

    public void setEnabled(FeatureFlag flag, boolean enabled) {
        flags.put(flag, enabled);
        if (persistenceEnabled) {
            persistToDisk();
        }
    }

    /**
     * Load persisted flag state from disk, if present, and overlay onto current values.
     */
    private void loadFromDisk() {
        if (!Files.exists(STATE_FILE)) {
            return;
        }
        try {
            Map<String, Boolean> stored = objectMapper.readValue(
                    Files.readAllBytes(STATE_FILE),
                    new TypeReference<Map<String, Boolean>>() {
                    });

            if (stored != null) {
                for (Map.Entry<String, Boolean> entry : stored.entrySet()) {
                    try {
                        FeatureFlag flag = FeatureFlag.valueOf(entry.getKey());
                        if (entry.getValue() != null) {
                            flags.put(flag, entry.getValue());
                        }
                    } catch (IllegalArgumentException ignored) {
                        // Unknown flag name in file – ignore to be forward-compatible
                    }
                }
            }
        } catch (IOException ignored) {
            // On any problem reading the state file, fall back to in-memory defaults
        }
    }

    /**
     * Persist current flag state to a small JSON file on disk.
     */
    private void persistToDisk() {
        try {
            Map<String, Boolean> toStore = flags.entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));

            byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(toStore);
            // Ensure parent directory exists
            Path parent = STATE_FILE.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.write(STATE_FILE, json);
        } catch (IOException ignored) {
            // Persistence is best-effort; if it fails we still keep in-memory state
        }
    }
}
