package com.maan.eway.integration.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class YiSmiDetailReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("ServiceId")
    private String serviceId;

    @JsonProperty("ServiceAction")
    private String serviceAction;

    @JsonProperty("QuotationPolicyNo")
    private String quotationPolicyNo;

    @JsonProperty("SecCode")
    private String secCode;

    @JsonProperty("L1S1Id")
    private Double l1s1Id;

    @JsonProperty("CvrId")
    private Double cvrId;

    @JsonProperty("SumInsured")
    private Double sumInsured;

    @JsonProperty("IterationNo")
    private String iterationNo;

    @JsonProperty("SiModifiedYn")
    private String siModifiedYn;

    @JsonProperty("Rate")
    private Double rate;

    @JsonProperty("RateModifiedYn")
    private String rateModifiedYn;

    @JsonProperty("Premium")
    private Double premium;

    @JsonProperty("PremiumModifiedYn")
    private String premiumModifiedYn;

    @JsonProperty("RequestTime")
    private Date requestTime;

    @JsonProperty("ResponseTime")
    private Date responseTime;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("PWsResponseType")
    private String pWsResponseType;

    @JsonProperty("PWsError")
    private String pWsError;

    @JsonProperty("RequestReferenceNo")
    private String requestReferenceNo;

    @JsonProperty("ProdCode")
    private String prodCode;

    @JsonProperty("ServiceType")
    private String serviceType;

    @JsonProperty("PrsSmiCode")
    private Integer prsSmiCode;

    @JsonProperty("PrsSmiDesc")
    private String prsSmiDesc;

    @JsonProperty("PrsRate")
    private String prsRate;

    @JsonProperty("PrsRatePer")
    private String prsRatePer;

    @JsonProperty("PrsCvrType")
    private String prsCvrType;

    @JsonProperty("PrsSiFc")
    private String prsSiFc;

    @JsonProperty("PrsSiLc1")
    private String prsSiLc1;

    @JsonProperty("PrsPremFc")
    private String prsPremFc;

    @JsonProperty("PrsPremLc1")
    private String prsPremLc1;

    @JsonProperty("CvrEndNoIdx")
    private String cvrEndNoIdx;
}