package dev.hieu.springboothelloworld.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple controller to render the feature flag management UI.
 */
@Controller
@RequestMapping("/features")
public class FeatureFlagController {

    @GetMapping
    public String view() {
        return "features/index";
    }
}


