package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class PremiumCharges{
	
    @JsonProperty("Charges") 
    public Charges getCharges() { 
		 return this.charges; } 
    public void setCharges(Charges charges) { 
		 this.charges = charges; } 
    Charges charges;
}
