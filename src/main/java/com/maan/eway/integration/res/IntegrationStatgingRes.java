package com.maan.eway.integration.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IntegrationStatgingRes {
	
	@JsonProperty("QuotationPolicyNo")
    private String     quotationPolicyNo ;

	@JsonProperty("PolicyStatus")
    private String     policyStatus ;
    
	@JsonProperty("SectionStatus")
    private String     sectionStatus;

	@JsonProperty("PolicyRiskStatus")
    private String     policyRiskStatus;
    
	@JsonProperty("DriverStatus")
    private String     driverStatus ;

	@JsonProperty("CoverStatus")
    private String     coverStatus ;
    
	@JsonProperty("DiscountStatus")
    private String discountStatus ;
    
	@JsonProperty("ChargeStatus")
    private String chargeStatus ;
    
	@JsonProperty("VatStatus")
    private String vatStatus ;

	@JsonProperty("PremiumStatus")
    private String premiumStatus ;
    
	@JsonProperty("PolicyApproveStatus")
    private String policyApproveStatus ;

}
