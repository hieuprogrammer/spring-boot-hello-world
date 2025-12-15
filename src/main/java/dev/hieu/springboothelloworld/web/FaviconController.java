package dev.hieu.springboothelloworld.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple controller to handle /favicon.ico requests and redirect them to the SVG favicon.
 * This avoids noisy NoHandlerFoundException warnings for /favicon.ico.
 */
@Controller
public class FaviconController {

    @GetMapping("favicon.ico")
    public String favicon() {
        return "redirect:/favicon.svg";
    }
}


