package dev.hieu.springboothelloworld.web.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public String handleError(HttpServletRequest request, Model model) {
        // This controller only handles HTML error pages
        // API endpoints (JSON) will be handled by GlobalExceptionHandler
        // The produces = TEXT_HTML_VALUE ensures this only handles HTML requests
        
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUriAttr = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            model.addAttribute("statusCode", statusCode);
            model.addAttribute("statusText", HttpStatus.valueOf(statusCode).getReasonPhrase());
            model.addAttribute("message", message != null ? message : "An error occurred");
            model.addAttribute("requestUri", requestUriAttr != null ? requestUriAttr : request.getRequestURI());

            if (exception != null) {
                model.addAttribute("exception", exception.getClass().getSimpleName());
            }

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return "error/400";
            }
        }

        return "error/error";
    }
}
