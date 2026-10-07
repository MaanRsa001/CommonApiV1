package com.maan.eway.bond.Dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle validation errors (e.g., @Valid on DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<commonResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<errors> errorList = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> new errors(
                "VALIDATION_ERROR",
                fieldError.getField(),                       
                fieldError.getDefaultMessage()  
            ))
            .collect(Collectors.toList());

        commonResponse res = new commonResponse();
        res.setIsError(true);
        res.setMessage("Validation Failed");
        res.setErroCode(400);
        res.setErrorMessage(errorList);
        res.setCommonResponse(null);

        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<commonResponse> handleGeneralException(Exception ex) {
    	errors error = new errors("INTERNAL_ERROR",null, ex.getMessage());

        commonResponse res = new commonResponse();
        res.setIsError(true);
        res.setMessage("Unexpected Error Occurred");
        res.setErroCode(500);
        res.setErrorMessage(List.of(error));
        res.setCommonResponse(null);

        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}