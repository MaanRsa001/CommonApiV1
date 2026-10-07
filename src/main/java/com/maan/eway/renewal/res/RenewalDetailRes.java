package com.maan.eway.renewal.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalDetailRes {
	
	@JsonProperty("OldPolicyNo")
	private String oldpolicyNo;

    @JsonProperty("OldQuoteNo")
    private String   oldquoteNo ;
    
    @JsonProperty("CustomerName")
    private String   customerName ;
	
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("InceptionDate")
	private Date inceptionDate;
	
    @JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ExpiryDate")
	private Date expiryDate;
	
	@JsonProperty("NewPolicyNumber")
	private String newpolicyNumber;
	
	@JsonProperty("CurrentStatus")
	private String currentStatus;
	
	@JsonProperty("NewRequestRefNo")
	private String newRequestRefNo;
	
	@JsonProperty("RegistrationNo")
	private String registrationNo;


}
