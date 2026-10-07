package com.maan.eway.common.req;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.req.DiscountCoverGetRes;

import lombok.Data;

@Data
public class CoverListGetRes {

	@JsonProperty("CoverId") 
    public String coverId;
    @JsonProperty("CalcType") 
    public String calcType;
    @JsonProperty("CoverName") 
    public String coverName;
    @JsonProperty("CoverDesc") 
    public String coverDesc;
    @JsonProperty("MinimumPremium") 
    public BigDecimal minimumPremium;
    @JsonProperty("IsSubCover") 
    public String isSubCover;
    @JsonProperty("SumInsured") 
    public BigDecimal sumInsured;
    @JsonProperty("Rate") 
    public Double rate;
    @JsonProperty("SubCoverId") 
    public String subCoverId;
    @JsonProperty("SubCoverDesc") 
    public String subCoverDesc;
    @JsonProperty("SubCoverName") 
    public String subCoverName;
    @JsonProperty("PremiumBeforeDiscount") 
    public BigDecimal premiumBeforeDiscount;
    @JsonProperty("PremiumAfterDiscount") 
    public BigDecimal premiumAfterDiscount;
    @JsonProperty("PremiumExcluedTax") 
    public Double premiumExcluedTax;
    @JsonProperty("PremiumIncludedTax") 
    public BigDecimal premiumIncludedTax;
    @JsonProperty("Discounts") 
    public List<DiscountCoverGetRes> discounts;
  
    @JsonProperty("SubCovers") 
    public List<CoverListGetRes> subcovers;
    
    @JsonProperty("FactorTypeId")
    private String factorTypeId;
    
    @JsonProperty("DependentCoverYN")
    private String dependentCoveryn;

    @JsonProperty("DependentCoverId")
    private String dependentCoverId;
    
   	    @JsonProperty("CoverageType") 
    private String coverageType;
    @JsonProperty("isSelected") 
    private String isselected;
}
