package dev.hieu.springboothelloworld.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CorsConfigTest {

    @Autowired
    private CorsFilter corsFilter;

    @Test
    void corsFilter_ShouldBeConfigured() {
        assertNotNull(corsFilter);
    }
}

