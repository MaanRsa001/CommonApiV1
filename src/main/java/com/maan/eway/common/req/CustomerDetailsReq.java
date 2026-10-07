package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
 
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDetailsReq {
    @JsonProperty("CompanyId")
    private String companyId;
    @JsonProperty("CustomerReferenceNo")
    private String     requestReferenceNo ;
}