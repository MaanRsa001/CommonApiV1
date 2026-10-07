package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class PlanMarketingPointer{
    @JsonProperty("PointID") 
    public String getPointID() { 
		 return this.pointID; } 
    public void setPointID(String pointID) { 
		 this.pointID = pointID; } 
    String pointID;
}
