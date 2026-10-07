package com.maan.eway.res.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discount implements Serializable {
    @JsonProperty("DiscountId") 
    private String discountId;
    @JsonProperty("DiscountDesc") 
    private String discountDesc;
    @JsonProperty("DiscountRate") 
    private String discountRate;
    @JsonProperty("DiscountAmount") 
    private BigDecimal discountAmount;
    @JsonProperty("DiscountCalcType") 
    private String discountCalcType;
    @JsonProperty("DiscountForId") 
    private String discountforId;
    @JsonProperty("SubCoverId") 
    public String subCoverId;
    @JsonProperty("MaxDiscountAmount") 
    public BigDecimal maxAmount;
    @JsonProperty("FactorTypeId")
    private String factorTypeId;
    @JsonProperty("CoverAgeType")
    private String coverAgeType;

    @JsonProperty("RegulatoryCode")
    private String  regulatoryCode ;
    
    @JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
    private Date   effectiveDate ;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
    private Date   policyEndDate ;
	@JsonProperty("MinRate") 
	  public Double minrate;
	 // only for ui
	  @JsonProperty("ActualRate") 
	  public Double actualrate;
	 

    
}
