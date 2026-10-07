package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoInsertTermsReq {
 
    @JsonProperty("RequestReferenceNo")
    private String requestReferenceNo;
 
    @JsonProperty("QuoteNo")
    private String quoteNo;
 
    @JsonProperty("InsuranceId")
    private String companyId;
 
    @JsonProperty("BranchCode")
    private String branchCode;
 
    @JsonProperty("ProductId")
    private String productId;
 
    @JsonProperty("CreatedBy")
    private String createdBy;
}
