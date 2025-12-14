package dev.hieu.springboothelloworld.web.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class PingApi {
    @GetMapping
    public String ping() { return "pong"; }
}
