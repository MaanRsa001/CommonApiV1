package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateSubCoverRes {


	@JsonProperty("FactorTypeId")
    private String factorTypeId ;

    @JsonProperty("InsuranceId")
    private String    companyId ;
  
    @JsonProperty("ProductId")
    private String    productId ;
   
    @JsonProperty("SubCoverId")
    private String    subCoverId ;
    
    @JsonProperty("SubCoverName")
    private String    subCoverName ;
    
    @JsonProperty("SubCoverDesc")
    private String    subCoverDesc ;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date       effectiveDateStart ;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EffectiveDateEnd")
    private Date       effectiveDateEnd ;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EntryDate")
    private Date       entryDate;
    
    @JsonProperty("Status")
    private String     status ;

    @JsonProperty("CreatedBy")
    private String     createdBy ;
}
