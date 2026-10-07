package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class PremiumBreakdown{
    @JsonProperty("PremiumCharges") 
    public PremiumCharges getPremiumCharges() { 
		 return this.premiumCharges; } 
    public void setPremiumCharges(PremiumCharges premiumCharges) { 
		 this.premiumCharges = premiumCharges; } 
    PremiumCharges premiumCharges;
}
