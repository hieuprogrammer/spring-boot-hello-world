package dev.hieu.springboothelloworld.web;

import dev.hieu.springboothelloworld.configuration.FeatureFlag;
import dev.hieu.springboothelloworld.configuration.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds commonly used attributes to all MVC models, such as feature flag state.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final FeatureFlagService featureFlagService;

    @ModelAttribute("isTodoWriteEnabled")
    public boolean isTodoWriteEnabled() {
        return featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API);
    }

    @ModelAttribute("isTodoSearchEnabled")
    public boolean isTodoSearchEnabled() {
        return featureFlagService.isEnabled(FeatureFlag.TODO_SEARCH_API);
    }
}


