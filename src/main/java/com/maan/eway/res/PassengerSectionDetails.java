package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PassengerSectionDetails {

	@JsonProperty("SectionId")
	private  String sectionId;	
	
	@JsonProperty("SectionName")
	private  String sectionName;	
	
	@JsonProperty("PassengerId")
	private  String passengerId;	
	
	@JsonProperty("PassengerName")
	private  String passengerName;	
	
	@JsonProperty("GroupDesc")
	private  String groupDesc;
	
	@JsonProperty("GroupId")
	private  String groupId;
	
	@JsonProperty("PremiumAfterDiscount")
	private  String premiumAfterDiscount;	

	@JsonProperty("PremiumAfterDiscountLc")
	private  String premiumAfterDiscountLc;	

	@JsonProperty("PremiumBeforeDiscount")
	private  String premiumBeforeDiscount;	

	@JsonProperty("PremiumBeforeDiscountLc")
	private  String premiumBeforeDiscountLc;	

	@JsonProperty("PremiumExcluedTax")
	private  String premiumExcluedTax;	

	@JsonProperty("PremiumExcluedTaxLc")
	private  String premiumExcluedTaxLc;	
	
	@JsonProperty("PremiumIncludedTax")
	private  String premiumIncludedTax;	

	@JsonProperty("PremiumIncludedTaxLc")
	private  String premiumIncludedTaxLc;	

	@JsonProperty("Covers")
	private  List<CoverRes> covers ;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
}
