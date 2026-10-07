package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyTypeMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("PolicyTypeId")
    private String policyTypeId;
    
    @JsonProperty("InsuranceId")
    private String companyId;

   @JsonProperty("ProductId")
   private String productId;

    

}
