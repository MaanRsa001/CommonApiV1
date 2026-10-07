package com.maan.eway.upr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UprApiResponse {
    private boolean success;
    private String message;
    private UprPolicyResponse data;  // single policy object
    private String timestamp;
}
