package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class PlanPricingBreakdown{
    @JsonProperty("PricingBreakdown") 
    public PricingBreakdown getPricingBreakdown() { 
		 return this.pricingBreakdown; } 
    public void setPricingBreakdown(PricingBreakdown pricingBreakdown) { 
		 this.pricingBreakdown = pricingBreakdown; } 
    PricingBreakdown pricingBreakdown;
}
