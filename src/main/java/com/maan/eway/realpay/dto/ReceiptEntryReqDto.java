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
public class ReceiptEntryReqDto {
    @JsonProperty("AccountType")
    private String AccountType;
    @JsonProperty("LoginUserId")
    private String LoginUserId;
    @JsonProperty("AcntType")
    private String AcntType;
    @JsonProperty("PolicyNo")
    private String PolicyNo;
    @JsonProperty("QuoteNo")
    private String QuoteNo;
    private String instalment;
    private String instAmount;
    private String paymentId;
    @JsonProperty("InsuranceId")
    private String InsuranceId;
    @JsonProperty("ProductId")
    private String ProductId;

    public ReceiptEntryReqDto constructRecepitEntry(String loginId,String policyNo, String quoteNo,
                                                    String instalment, String instAmount,
                                                    String paymentId,String insuranceId, String productId){
        return ReceiptEntryReqDto.builder()
                .AccountType("UW-001").LoginUserId(loginId).AcntType("UW-001").PolicyNo(policyNo)
                .QuoteNo(quoteNo).instalment(instalment).instAmount(instAmount).paymentId(paymentId)
                .InsuranceId(insuranceId).ProductId(productId)
                .build();
    }
}

