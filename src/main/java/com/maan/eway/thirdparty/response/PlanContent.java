package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class PlanContent{
    @JsonProperty("__cdata") 
    public String get__cdata() { 
		 return this.__cdata; } 
    public void set__cdata(String __cdata) { 
		 this.__cdata = __cdata; } 
    String __cdata;
}
