package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TaxDetailsRes {

    @JsonProperty("TaxId")
    private Integer taxId;

    @JsonProperty("TaxDesc")
    private String taxDesc;

    @JsonProperty("TaxDescLocal")
    private String taxDescLocal;

    @JsonProperty("TaxRate")
    private BigDecimal taxRate;

    @JsonProperty("TaxAmountFc")
    private BigDecimal taxAmountFc;

    @JsonProperty("TaxAmountLc")
    private BigDecimal taxAmountLc;
}
