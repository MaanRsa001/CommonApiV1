package com.maan.eway.mtpintegration.dto;

import lombok.Data;

@Data
public class MtpWrapperReq {

    private String mode; // "SEARCH" or "SAVE" - drives how much of the flow runs

    // Common
    private String registrationNumber;

    // savecustomerdetails specific (only required when mode = SAVE)
    private String idType;
    private String idNumber;

    // savemotordetails specific (only required when mode = SAVE)
//    private String loginId;
//    private String subUserType;
//    private String userType;
//    private String applicationId;
    private String title;
    private String gender;
    private String mobileNumber;

    // getters/setters
}
