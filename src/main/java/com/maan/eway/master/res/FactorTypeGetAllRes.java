package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorTypeGetAllRes {

	@JsonProperty("FactorTypeId")
    private String factorTypeId ;
	
	@JsonProperty("ProductId")
    private String productId    ;
	@JsonProperty("InsuranceId")
    private String     companyId    ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date       entryDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
    private Date       effectiveDateStart ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
    private Date       effectiveDateEnd ;

    @JsonProperty("FactorTypeDesc")
    private String     factorTypeDesc ;
    
    @JsonProperty("FactorTypeName")
    private String     factorTypeName ;
	
    @JsonProperty("AmendId")
    private String    amendId      ; 
	@JsonProperty("CreatedBy")
    private String     createdBy    ;
	
	@JsonProperty("Status")
    private String     status ;
}
