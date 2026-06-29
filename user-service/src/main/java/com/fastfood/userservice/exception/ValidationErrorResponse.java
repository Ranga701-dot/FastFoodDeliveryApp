package com.fastfood.userservice.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse {

    private LocalDateTime timestamp;
    private Map<String, String> errors;

    public ValidationErrorResponse(LocalDateTime timestamp,
                                   Map<String, String> errors) {
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}