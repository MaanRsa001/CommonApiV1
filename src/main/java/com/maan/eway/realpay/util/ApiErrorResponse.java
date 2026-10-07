package com.maan.eway.realpay.util;

import lombok.Data;

import java.util.Map;

@Data
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private Map<String, String> errors;

    public ApiErrorResponse(boolean success, String message, Map<String, String> errors) {
        this.success = success;
        this.message = message;
        this.errors = errors;
    }
}

