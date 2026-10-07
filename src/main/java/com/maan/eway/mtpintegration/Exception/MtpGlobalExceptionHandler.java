package com.maan.eway.mtpintegration.Exception;

/*
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import com.maan.eway.mtpintegration.dto.ErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Component("mtpGlobalExceptionHandler")

@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@RestControllerAdvice
public class MtpGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse("FAILED", message));
    }

    @ExceptionHandler({IllegalArgumentException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ErrorResponse> badRequest(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("FAILED", ex.getMessage()));
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> partnerApiError(RestClientResponseException ex) {
        String response = ex.getResponseBodyAsString();
        String message = response == null || response.isBlank() ? ex.getMessage() : response;
        return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse("FAILED", message));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> connectionError(ResourceAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("FAILED", "Unable to connect MTP partner API: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> error(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("FAILED", ex.getMessage()));
    }
}
*/




import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import com.maan.eway.mtpintegration.dto.ErrorResponse;

@RestControllerAdvice(basePackages = "com.maan.eway.mtpintegration")
public class MtpGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.ok(new ErrorResponse("FAILED", message));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ErrorResponse> badRequest(Exception ex) {
        return ResponseEntity.ok(new ErrorResponse("FAILED", ex.getMessage()));
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> partnerApiError(RestClientResponseException ex) {
        String response = ex.getResponseBodyAsString();
        String message = response == null || response.isBlank() ? ex.getMessage() : response;
        return ResponseEntity.ok(new ErrorResponse("FAILED", message));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> connectionError(ResourceAccessException ex) {
        return ResponseEntity.ok(new ErrorResponse("FAILED", "Unable to connect MTP partner API: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> error(Exception ex) {
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse("FAILED", ex.getMessage()));
    }
}

