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
public class YiContentUploadReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("ContSrNo")
    private Long contSrNo;

    @JsonProperty("CompanyId")
    private String companyId;

    @JsonProperty("ProductId")
    private String productId;

    @JsonProperty("SectionId")
    private String sectionId;

    @JsonProperty("RiskId")
    private Integer riskId;

    @JsonProperty("EndNoIdx")
    private String endNoIdx;

    @JsonProperty("ItemDescription")
    private String itemDescription;

    @JsonProperty("AmountFc")
    private String amountFc;

    @JsonProperty("AmountLc")
    private String amountLc;

    @JsonProperty("QuotationPolicyNo")
    private String quotationPolicyNo;

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
}

