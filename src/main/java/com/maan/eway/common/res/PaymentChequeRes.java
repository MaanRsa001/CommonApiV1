package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class PaymentChequeRes {
	
	  @JsonProperty("BankName")
	    private String     bankName ;
	    
	    @JsonProperty("ChequeNo")
	    private String     chequeNo;

	    @JsonFormat(pattern="dd/MM/yyyy")
	    @JsonProperty("ChequeDate")
	    private Date       chequeDate ;
	    
	    @JsonProperty("MicrNo")
	    private String micrNo;
   

}
