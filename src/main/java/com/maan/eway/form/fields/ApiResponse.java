package com.maan.eway.form.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ApiResponse
 *
 * Standard wrapper for all REST API responses.
 *
 * Success example:
 * {
 *   "status":    "SUCCESS",
 *   "message":   "Field saved successfully",
 *   "data":      { ... },
 *   "timestamp": "2025-02-23T10:15:30"
 * }
 *
 * Error example:
 * {
 *   "status":    "ERROR",
 *   "message":   "apiUrl is required for dropdown field types",
 *   "data":      null,
 *   "timestamp": "2025-02-23T10:15:30"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String        status;
    private String        message;
    private T             data;
    private LocalDateTime timestamp;

    // ── Factory helpers ───────────────────────────────────────────────────────

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Operation completed successfully", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
