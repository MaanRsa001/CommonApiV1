package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.master.req.TaxMultiInsertReq;

import lombok.Data;

@Data
public class CompanyTaxGetRes implements Serializable {

    private static final long serialVersionUID = 1L;

   
    @JsonProperty("ProductId")
    private String productId ;

    
    @JsonProperty("InsuranceId")
    private String     companyId;
    
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date       effectiveDateStart ;

    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EffectiveDateEnd")
    private Date       effectiveDateEnd ;
    
    //--- ENTITY DATA FIELDS 
    
    @JsonProperty("CompanyTaxDetais")
    private List<TaxMultiInsertReq> companyTaxDetails ; 
    
    @JsonProperty("CreatedBy")
    private String   createdBy ;
    
    @JsonProperty("Status")
    private String     status ; 
    
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;

	@JsonProperty("AmendId")
    private String    amendId;
	
	

}
