package com.maan.eway.res.calc;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Tax implements Serializable  {
	@JsonProperty("TaxId") 
    public String taxId;
    @JsonProperty("TaxRate") 
    public Double taxRate;
    @JsonProperty("TaxAmount") 
    public BigDecimal taxAmount;
    @JsonProperty("TaxDesc") 
    public String taxDesc;
    public String isTaxExempted;
    @JsonProperty("TaxExemptType") 
    public String taxExemptType;
    @JsonProperty("TaxExemptCode") 
    public String taxExemptCode;
    @JsonProperty("TaxCalcType") 
    public String calcType;

    @JsonProperty("RegulatoryCode")
    private String  regulatoryCode ;
    
    @JsonProperty("EndtTypeId")
    private String endtTypeId;
    @JsonProperty("EndtTypeCount")
    private BigDecimal endtTypeCount;
    
    
    @JsonProperty("DependentYN")
    private String dependentYn;
    @JsonProperty("TaxExemptedAllowed")
    private String taxExemptedAllowed;
    
    @JsonProperty("MinimumTaxAmount")
    private BigDecimal minimumTaxAmount;
    
    @JsonProperty("MinimumTaxAmountLC")
    private BigDecimal minimumTaxAmountLc;
    
    @JsonProperty("MaxTaxAmountYN")
    private String maxTaxAmountYn;
    
    @JsonProperty("MaxTaxAmountLC")
    private BigDecimal maxTaxAmount;
    
    @JsonProperty("TaxAmountLc") 
    public BigDecimal taxAmountLc;
    
    @JsonProperty("TaxFor")
    private String taxFor;
    @JsonProperty("extend_Cust_tax")
    private String extend_Cust_tax;
    
    
}
