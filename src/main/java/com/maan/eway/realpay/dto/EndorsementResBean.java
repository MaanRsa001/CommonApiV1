package com.maan.eway.realpay.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndorsementResBean {

    private String quoteNo;
    private BigDecimal overallPremiumLc;
    private String accountType;
    private String accountingType;
    private Boolean isEndosPolicy;
    private String receiptAccountType;
    private String commisionAccountType;

    private String orginalPolicNo;
    private String financeType;

    private String financeTypeCode;
    private String accountCvrEntryType;
    private String financeTypeCatg;

    private String paymentType;
    private String endTypeId;
    private String prevQuoteNo;
    private List<String> addsectionIds;
    private List<String> addTaxIds;
    private String prevEndQuoteNo;
}

