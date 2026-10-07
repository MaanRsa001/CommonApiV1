package com.maan.eway.mtpintegration.dto;

import java.util.List;

import lombok.Data;

import com.maan.eway.error.Error;

@Data
public class MtpWrapperRes {
    private boolean isError;
    private String message;
    private Object result;              
    private List<Error> errorMessage;
    private int errorCode;
    // getters/setters
}
