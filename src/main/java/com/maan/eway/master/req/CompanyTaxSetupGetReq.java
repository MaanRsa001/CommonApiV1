package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyTaxSetupGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("ProductId")
    private String     productId ;
	@JsonProperty("InsuranceId")
    private String     companyId     ;
	@JsonProperty("TaxId")
    private String     taxId ;
	
	  
    @JsonFormat(pattern ="dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date effectiveDateStart;
    
}
