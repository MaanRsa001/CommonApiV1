package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class PricingBreakdown{
    @JsonProperty("MinAge") 
    public String getMinAge() { 
		 return this.minAge; } 
    public void setMinAge(String minAge) { 
		 this.minAge = minAge; } 
    String minAge;
    @JsonProperty("MaxAge") 
    public String getMaxAge() { 
		 return this.maxAge; } 
    public void setMaxAge(String maxAge) { 
		 this.maxAge = maxAge; } 
    String maxAge;
    @JsonProperty("Gender") 
    public String getGender() { 
		 return this.gender; } 
    public void setGender(String gender) { 
		 this.gender = gender; } 
    String gender;
    @JsonProperty("CurrencyCode") 
    public String getCurrencyCode() { 
		 return this.currencyCode; } 
    public void setCurrencyCode(String currencyCode) { 
		 this.currencyCode = currencyCode; } 
    String currencyCode;
    @JsonProperty("PremiumAmount") 
    public String getPremiumAmount() { 
		 return this.premiumAmount; } 
    public void setPremiumAmount(String premiumAmount) { 
		 this.premiumAmount = premiumAmount; } 
    String premiumAmount;
    @JsonProperty("PremiumBreakdown") 
    public PremiumBreakdown getPremiumBreakdown() { 
		 return this.premiumBreakdown; } 
    public void setPremiumBreakdown(PremiumBreakdown premiumBreakdown) { 
		 this.premiumBreakdown = premiumBreakdown; } 
    PremiumBreakdown premiumBreakdown;
}
