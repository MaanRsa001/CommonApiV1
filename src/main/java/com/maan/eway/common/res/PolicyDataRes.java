package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyDataRes {
	
	
	    @JsonProperty("PolicyNo")
	    private String policyNo;

	    @JsonProperty("TypeOfTransaction")
	    private String typeOfTransaction;

	    @JsonProperty("TransactionDate")
	    private String transactionDate;

	    @JsonProperty("InceptionDate")
	    private String inceptionDate;

	    @JsonProperty("ExpiryDate")
	    private String expiryDate;

	    @JsonProperty("UWYear")
	    private Long uwYear;

//	    @JsonProperty("SumInsured")
//	    private Double sumInsured;

	    @JsonProperty("GrossPremium")
	    private BigDecimal grossPremium;

	    @JsonProperty("ProductId")
	    private Integer  productId;

//	    @JsonProperty("SectionId")
//	    private Integer sectionId;
	    
	    @JsonProperty("CoversList")
	    private List<Cover> coversList; 
	    
	    
	    
//	    @JsonProperty("SumInsuredWithSectionId")
//	    private Map<Integer , String> SumInsuredWithSectionId;

//	    @JsonProperty("CoverId")
//	    private String coverId;
	    
	
}

