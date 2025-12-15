package dev.hieu.springboothelloworld.configuration;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Simple in-memory feature flag service.
 * <p>
 * In a real system you might back this with a database, config server, or external
 * feature flag provider. For this demo we keep everything in memory.
 */
@Service
public class FeatureFlagService {

    private final Map<FeatureFlag, Boolean> flags = new EnumMap<>(FeatureFlag.class);

    public FeatureFlagService() {
        // Default state: all features enabled
        for (FeatureFlag flag : FeatureFlag.values()) {
            flags.put(flag, Boolean.TRUE);
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
    }
}


