package com.maan.eway.master.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.master.req.RatingFieldDetailsRes;

import lombok.Data;

@Data
public class FactorTypeDetailsGetRes {

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
	 
    @JsonProperty("FactorTypeName")
    private String     factorTypeName ;
    
    
    @JsonProperty("Remarks")
    private String     remarks ;
    @JsonProperty("FactorTypeDesc")
    private String     factorTypeDesc ;
	@JsonProperty("AmendId")
    private String    amendId      ; 
	@JsonProperty("CreatedBy")
    private String     createdBy    ;
	@JsonProperty("Status")
    private String     status ;
	@JsonProperty("RatingFieldDetails")
    private List<RatingFieldDetailsRes>     ratingFieldDetails  ;  
}
