package com.maan.eway.otp.dto;

import lombok.Data;

@Data
public class MPesaNotifDto {
    private String companyId;
    private String productId;
    private String description;
    private String emailId;
    private String phoneNumber;
    private String phoneCode;
    private String templateName;
}
