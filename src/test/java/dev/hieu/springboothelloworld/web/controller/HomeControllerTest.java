package dev.hieu.springboothelloworld.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Test
    void home_ShouldRedirectToTodos() {
        // When
        String redirectUrl = homeController.home();

        // Then
        assertEquals("redirect:/todos", redirectUrl);
    }
}

