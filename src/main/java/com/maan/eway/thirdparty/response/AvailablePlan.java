package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
@JacksonXmlRootElement(localName = "AvailablePlan")
public class AvailablePlan{
	@JsonProperty("PlanCode") 
	public String getPlanCode() { 
		return this.planCode; } 
	public void setPlanCode(String planCode) { 
		this.planCode = planCode; } 
	String planCode;
	@JsonProperty("SSRFeeCode") 
	public String getSSRFeeCode() { 
		return this.sSRFeeCode; } 
	public void setSSRFeeCode(String sSRFeeCode) { 
		this.sSRFeeCode = sSRFeeCode; } 
	String sSRFeeCode;
	@JsonProperty("CurrencyCode") 
	public String getCurrencyCode() { 
		return this.currencyCode; } 
	public void setCurrencyCode(String currencyCode) { 
		this.currencyCode = currencyCode; } 
	String currencyCode;
	@JsonProperty("TotalPremiumAmount") 
	public String getTotalPremiumAmount() { 
		return this.totalPremiumAmount; } 
	public void setTotalPremiumAmount(String totalPremiumAmount) { 
		this.totalPremiumAmount = totalPremiumAmount; } 
	String totalPremiumAmount;
	@JsonProperty("TotalCoverageAmount") 
	public String getTotalCoverageAmount() { 
		return this.totalCoverageAmount; } 
	public void setTotalCoverageAmount(String totalCoverageAmount) { 
		this.totalCoverageAmount = totalCoverageAmount; } 
	String totalCoverageAmount;
	@JsonProperty("PlanPremiumChargeType") 
	public String getPlanPremiumChargeType() { 
		return this.planPremiumChargeType; } 
	public void setPlanPremiumChargeType(String planPremiumChargeType) { 
		this.planPremiumChargeType = planPremiumChargeType; } 
	String planPremiumChargeType;
	@JsonProperty("PlanTitle") 
	public String getPlanTitle() { 
		return this.planTitle; } 
	public void setPlanTitle(String planTitle) { 
		this.planTitle = planTitle; } 
	String planTitle;
	@JsonProperty("PlanDesc") 
	public String getPlanDesc() { 
		return this.planDesc; } 
	public void setPlanDesc(String planDesc) { 
		this.planDesc = planDesc; } 
	String planDesc;
	@JsonProperty("PlanMarketingPointers") 
	public PlanMarketingPointers getPlanMarketingPointers() { 
		return this.planMarketingPointers; } 
	public void setPlanMarketingPointers(PlanMarketingPointers planMarketingPointers) { 
		this.planMarketingPointers = planMarketingPointers; } 
	PlanMarketingPointers planMarketingPointers;
	@JsonProperty("PlanAdditionalInfoTitle") 
	public String getPlanAdditionalInfoTitle() { 
		return this.planAdditionalInfoTitle; } 
	public void setPlanAdditionalInfoTitle(String planAdditionalInfoTitle) { 
		this.planAdditionalInfoTitle = planAdditionalInfoTitle; } 
	String planAdditionalInfoTitle;
	@JsonProperty("PlanAdditionalInfoDesc") 
	public String getPlanAdditionalInfoDesc() { 
		return this.planAdditionalInfoDesc; } 
	public void setPlanAdditionalInfoDesc(String planAdditionalInfoDesc) { 
		this.planAdditionalInfoDesc = planAdditionalInfoDesc; } 
	String planAdditionalInfoDesc;
	@JsonProperty("PlanYesDesc") 
	public String getPlanYesDesc() { 
		return this.planYesDesc; } 
	public void setPlanYesDesc(String planYesDesc) { 
		this.planYesDesc = planYesDesc; } 
	String planYesDesc;
	@JsonProperty("PlanNoDesc") 
	public String getPlanNoDesc() { 
		return this.planNoDesc; } 
	public void setPlanNoDesc(String planNoDesc) { 
		this.planNoDesc = planNoDesc; } 
	String planNoDesc;
	@JsonProperty("PlanNoConsideration") 
	public String getPlanNoConsideration() { 
		return this.planNoConsideration; } 
	public void setPlanNoConsideration(String planNoConsideration) { 
		this.planNoConsideration = planNoConsideration; } 
	String planNoConsideration;
	@JsonProperty("PlanTnC") 
	public String getPlanTnC() { 
		return this.planTnC; } 
	public void setPlanTnC(String planTnC) { 
		this.planTnC = planTnC; } 
	String planTnC;
	@JsonProperty("IsDefaultPlan") 
	public String getIsDefaultPlan() { 
		return this.isDefaultPlan; } 
	public void setIsDefaultPlan(String isDefaultPlan) { 
		this.isDefaultPlan = isDefaultPlan; } 
	String isDefaultPlan;
	@JsonProperty("PlanContent") 
	public String getPlanContent() { 
		return this.planContent; } 
	public void setPlanContent(String planContent) { 
		this.planContent = planContent; } 
	String planContent;
	
	@JsonProperty("PlanQualifiedPassengers") 
	public String getPlanQualifiedPassengers() { 
		return this.planQualifiedPassengers; } 
	public void setPlanQualifiedPassengers(String planQualifiedPassengers) { 
		this.planQualifiedPassengers = planQualifiedPassengers; } 
	String planQualifiedPassengers;
	@JsonProperty("PlanPricingBreakdown") 
	public PlanPricingBreakdown getPlanPricingBreakdown() { 
		return this.planPricingBreakdown; } 
	public void setPlanPricingBreakdown(PlanPricingBreakdown planPricingBreakdown) { 
		this.planPricingBreakdown = planPricingBreakdown; } 
	PlanPricingBreakdown planPricingBreakdown;
	@JsonProperty("PlanType") 
	public String getPlanType() { 
		return this.planType; } 
	public void setPlanType(String planType) { 
		this.planType = planType; } 
	String planType;
}
