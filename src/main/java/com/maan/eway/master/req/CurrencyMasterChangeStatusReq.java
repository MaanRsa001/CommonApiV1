package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CurrencyMasterChangeStatusReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("CurrencyId")
    private String     currencyId     ;

	@JsonProperty("InsuranceId")
    private String     companyId     ;
    
	@JsonProperty("Status")
    private String    status    ;
    
}
