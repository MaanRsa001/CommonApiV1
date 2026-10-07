package com.maan.eway.excelupload.dto;

import java.util.List;

import lombok.Data;

@Data
public class SavePassengersResponse {
    private boolean IsError;
    private String Message;
    private Object Result;
    private List<String> ErrorMessage;
}
