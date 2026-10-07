package com.maan.eway.thirdparty.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpsellPlan {
	
	@JsonProperty("PlanContent")
	private String planContent;
	@JsonProperty("TotalCoverageAmount")
    private Integer totalCoverageAmount;
	@JsonProperty("PlanCode")
    private String planCode;
	public String getPlanContent() {
		return planContent;
	}

	public void setPlanContent(String planContent) {
		this.planContent = planContent;
	}

	public Integer getTotalCoverageAmount() {
		return totalCoverageAmount;
	}

	public void setTotalCoverageAmount(Integer totalCoverageAmount) {
		this.totalCoverageAmount = totalCoverageAmount;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getPlanAdditionalInfoTitle() {
		return planAdditionalInfoTitle;
	}

	public void setPlanAdditionalInfoTitle(String planAdditionalInfoTitle) {
		this.planAdditionalInfoTitle = planAdditionalInfoTitle;
	}

	public Boolean getIsDefaultPlan() {
		return isDefaultPlan;
	}

	public void setIsDefaultPlan(Boolean isDefaultPlan) {
		this.isDefaultPlan = isDefaultPlan;
	}

	public String getPlanTnC() {
		return planTnC;
	}

	public void setPlanTnC(String planTnC) {
		this.planTnC = planTnC;
	}

	public String getPlanDesc() {
		return planDesc;
	}

	public void setPlanDesc(String planDesc) {
		this.planDesc = planDesc;
	}

	public PlanPricingBreakdown getPlanPricingBreakdown() {
		return planPricingBreakdown;
	}

	public void setPlanPricingBreakdown(PlanPricingBreakdown planPricingBreakdown) {
		this.planPricingBreakdown = planPricingBreakdown;
	}

	public Double getTotalPremiumAmount() {
		return totalPremiumAmount;
	}

	public void setTotalPremiumAmount(Double totalPremiumAmount) {
		this.totalPremiumAmount = totalPremiumAmount;
	}

	public String getPlanTitle() {
		return planTitle;
	}

	public void setPlanTitle(String planTitle) {
		this.planTitle = planTitle;
	}

	public String getPlanNoConsideration() {
		return planNoConsideration;
	}

	public void setPlanNoConsideration(String planNoConsideration) {
		this.planNoConsideration = planNoConsideration;
	}

	public String getPlanAdditionalInfoDesc() {
		return planAdditionalInfoDesc;
	}

	public void setPlanAdditionalInfoDesc(String planAdditionalInfoDesc) {
		this.planAdditionalInfoDesc = planAdditionalInfoDesc;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getPlanNoDesc() {
		return planNoDesc;
	}

	public void setPlanNoDesc(String planNoDesc) {
		this.planNoDesc = planNoDesc;
	}

	public String getPlanYesDesc() {
		return planYesDesc;
	}

	public void setPlanYesDesc(String planYesDesc) {
		this.planYesDesc = planYesDesc;
	}

	public String getPlanQualifiedPassengers() {
		return planQualifiedPassengers;
	}

	public void setPlanQualifiedPassengers(String planQualifiedPassengers) {
		this.planQualifiedPassengers = planQualifiedPassengers;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getsSRFeeCode() {
		return sSRFeeCode;
	}

	public void setsSRFeeCode(String sSRFeeCode) {
		this.sSRFeeCode = sSRFeeCode;
	}

	public String getPlanPremiumChargeType() {
		return planPremiumChargeType;
	}

	public void setPlanPremiumChargeType(String planPremiumChargeType) {
		this.planPremiumChargeType = planPremiumChargeType;
	}

	@JsonProperty("PlanAdditionalInfoTitle")
    private String planAdditionalInfoTitle;
	
	@JsonProperty("IsDefaultPlan")
    private Boolean isDefaultPlan;
	@JsonProperty("PlanTnC")
    private String planTnC;
	
	@JsonProperty("PlanDesc")
    private String planDesc;
	
	@JsonProperty("PlanPricingBreakdown")
    private PlanPricingBreakdown planPricingBreakdown;
	
	@JsonProperty("TotalPremiumAmount")
    private Double totalPremiumAmount;
	
	@JsonProperty("PlanTitle")
    private String planTitle;
	
	@JsonProperty("PlanNoConsideration")
    private String planNoConsideration;
	
	@JsonProperty("PlanAdditionalInfoDesc")
    private String planAdditionalInfoDesc;
	
	@JsonProperty("CurrencyCode")
    private String currencyCode;
	
	@JsonProperty("PlanNoDesc")
    private String planNoDesc;
	
	@JsonProperty("PlanYesDesc")
    private String planYesDesc;
	
	@JsonProperty("PlanQualifiedPassengers")
    private String planQualifiedPassengers;
	
	@JsonProperty("PlanType")
    private String planType;
	
	@JsonProperty("SSRFeeCode")
    private String sSRFeeCode;
	
	@JsonProperty("PlanPremiumChargeType")
    private String planPremiumChargeType;
	
}
