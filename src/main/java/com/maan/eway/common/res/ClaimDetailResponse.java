package com.maan.eway.common.res;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimDetailResponse {
    @JsonProperty("ClaimReferenceNo")
    private String claimReferenceNo;

    @JsonProperty("ClaimNo")
    private String claimNo;


    @JsonProperty("PolicyNo")
    private String policyNo;

    @JsonProperty("IntimationDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private Date intimationDate;

    @JsonProperty("LossDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lossDate;

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("ReserveAmount")
    private BigDecimal reserveAmount;

    @JsonProperty("SettlementAmount")
    private BigDecimal settlementAmount;

    @JsonProperty("OutstandingAmount")
    private BigDecimal outstandingAmount;

}
