package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateCoverRes {


	@JsonProperty("FactorTypeId")
    private String factorTypeId ;

    @JsonProperty("InsuranceId")
    private String    companyId ;
  
    @JsonProperty("ProductId")
    private String    productId ;
   
    @JsonProperty("CoverId")
    private String    coverId ;
    
    @JsonProperty("CoverName")
    private String    coverName ;
    
    @JsonProperty("SectionName")
    private String    sectionName ;
    
    @JsonProperty("CoverDesc")
    private String    coverDesc ;
    
    @JsonProperty("SectionId")
    private String    sectionId ;
    
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
