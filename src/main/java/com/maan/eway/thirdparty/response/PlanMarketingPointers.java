package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class PlanMarketingPointers{
    @JsonProperty("PlanMarketingPointer") 
    public PlanMarketingPointer getPlanMarketingPointer() { 
		 return this.planMarketingPointer; } 
    public void setPlanMarketingPointer(PlanMarketingPointer planMarketingPointer) { 
		 this.planMarketingPointer = planMarketingPointer; } 
    PlanMarketingPointer planMarketingPointer;
}
