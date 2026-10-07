package com.maan.eway.mtpintegration.dto;

import lombok.Data;

@Data
public class VehicleValidationResponse {

    private Integer returnCode;
    private String amount;
    private String vehicleNo;
    private String name;
    private String engineSize;
    private String make;
    private String model;
    private String service;
    private String policyStartDate;
    private String policyEndDate;
    private String rawReturnMessage;
    private String customerReferenceNo;
}