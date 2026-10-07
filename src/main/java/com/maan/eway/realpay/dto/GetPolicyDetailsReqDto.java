package com.maan.eway.realpay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPolicyDetailsReqDto {
    @JsonProperty("PolicyNo")
    private String policyNo;
    @JsonProperty("QuoteNo")
    private String quoteNo;
    @JsonProperty("InsuranceId")
    private String insuranceId;
    @JsonProperty("ProductId")
    private String productId;

    public GetPolicyDetailsReqDto constructDto(String policyNo, String quoteNo,
                                               String insuranceId, String productId){
        return GetPolicyDetailsReqDto.builder()
                .policyNo(policyNo).quoteNo(quoteNo)
                .insuranceId(insuranceId).productId(productId)
                .build();
    }
}
