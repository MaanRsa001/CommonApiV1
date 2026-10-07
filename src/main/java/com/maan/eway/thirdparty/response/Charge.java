package com.maan.eway.thirdparty.response; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class Charge{
	 public Charge() {}
	 
    public Charge(String sequenceNo, String rateType, String percentageValue, String amountValue) {
		super();
		this.sequenceNo = sequenceNo;
		this.rateType = rateType;
		this.percentageValue = percentageValue;
		this.amountValue = amountValue;
	}

	@JsonProperty("SequenceNo") 
    public String getSequenceNo() { 
		 return this.sequenceNo; } 
    public void setSequenceNo(String sequenceNo) { 
		 this.sequenceNo = sequenceNo; } 
    String sequenceNo;
    @JsonProperty("RateType") 
    public String getRateType() { 
		 return this.rateType; } 
    public void setRateType(String rateType) { 
		 this.rateType = rateType; } 
    String rateType;
    @JsonProperty("PercentageValue") 
    public String getPercentageValue() { 
		 return this.percentageValue; } 
    public void setPercentageValue(String percentageValue) { 
		 this.percentageValue = percentageValue; } 
    String percentageValue;
    @JsonProperty("AmountValue") 
    public String getAmountValue() { 
		 return this.amountValue; } 
    public void setAmountValue(String amountValue) { 
		 this.amountValue = amountValue; } 
    String amountValue;
}
