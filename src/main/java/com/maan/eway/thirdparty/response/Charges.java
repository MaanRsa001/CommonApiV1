package com.maan.eway.thirdparty.response; 
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty; 
public class Charges{
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Charge")
    @JsonProperty("Charge") 
    public ArrayList<Charge> getCharge() { 
		 return this.charge; } 
    public void setCharge(ArrayList<Charge> charge) { 
		 this.charge = charge; } 
    ArrayList<Charge> charge=new ArrayList<Charge>(1);
    
    
}
