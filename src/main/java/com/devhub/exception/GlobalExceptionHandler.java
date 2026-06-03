package com.devhub.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        log.error("Resource not found: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.error("Illegal argument: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/generic";
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobalError(Exception ex, Model model) {
        log.error("Unexpected error occurred", ex);
        model.addAttribute("message", "An unexpected error occurred: " + ex.getMessage());
        return "error/generic";
    }
}
