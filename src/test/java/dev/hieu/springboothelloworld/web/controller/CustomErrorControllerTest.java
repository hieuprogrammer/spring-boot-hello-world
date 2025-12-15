package dev.hieu.springboothelloworld.web.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private CustomErrorController customErrorController;

    @Test
    void handleError_WithNotFoundStatus_ShouldReturn404TemplateAndPopulateModel() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.NOT_FOUND.value());
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Page not found");
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/missing-page");

        String viewName = customErrorController.handleError(request, model);

        assertEquals("error/404", viewName);
        verify(model).addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        verify(model).addAttribute("statusText", HttpStatus.NOT_FOUND.getReasonPhrase());
        verify(model).addAttribute("message", "Page not found");
        verify(model).addAttribute("requestUri", "/missing-page");
        verify(model, never()).addAttribute(eq("exception"), any());
    }

    @Test
    void handleError_WithInternalServerErrorAndException_ShouldReturn500TemplateAndIncludeException() {
        RuntimeException ex = new RuntimeException("Boom");
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn(null); // fallback message branch
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION))
                .thenReturn(ex);
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                .thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/fail");

        String viewName = customErrorController.handleError(request, model);

        assertEquals("error/500", viewName);
        verify(model).addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        verify(model).addAttribute("statusText", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        // Captor to assert default message and requestUri fallback
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        verify(model).addAttribute(eq("message"), messageCaptor.capture());
        assertEquals("An error occurred", messageCaptor.getValue());

        verify(model).addAttribute("requestUri", "/api/fail");
        verify(model).addAttribute("exception", ex.getClass().getSimpleName());
    }

    @Test
    void handleError_WithForbiddenStatus_ShouldReturn403Template() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.FORBIDDEN.value());

        String viewName = customErrorController.handleError(request, model);

        assertEquals("error/403", viewName);
    }

    @Test
    void handleError_WithBadRequestStatus_ShouldReturn400Template() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.BAD_REQUEST.value());

        String viewName = customErrorController.handleError(request, model);

        assertEquals("error/400", viewName);
    }

    @Test
    void handleError_WithUnknownStatus_ShouldFallBackToGenericTemplate() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(418); // I'm a teapot :-)

        String viewName = customErrorController.handleError(request, model);

        assertEquals("error/error", viewName);
    }

    @Test
    void handleError_WithNullStatus_ShouldReturnGenericTemplate() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);

        String viewName = customErrorController.handleError(request, model);

        assertEquals("error/error", viewName);
        // No interactions with model required beyond default path
    }
}


