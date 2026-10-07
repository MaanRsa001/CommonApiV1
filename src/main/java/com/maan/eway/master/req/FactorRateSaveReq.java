package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateSaveReq {

	@JsonProperty("FactorTypeId")
    private String factorTypeId ;

    @JsonProperty("InsuranceId")
    private String    companyId ;
  
    @JsonProperty("ProductId")
    private String    productId ;
  
    @JsonProperty("BranchCode")
    private String    branchCode ;
    
    @JsonProperty("AgencyCode")
    private String    agencyCode ;
    
    @JsonProperty("SectionId")
    private String    sectionId ;
    
    @JsonProperty("CoverId")
    private String    coverId ;
    
    @JsonProperty("SubCoverId")
    private String    subCoverId ;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date       effectiveDateStart ;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EffectiveDateEnd")
    private Date       effectiveDateEnd ;
    
    @JsonProperty("Status")
    private String     status ;
    
    @JsonProperty("SubCoverYn")
    private String     subCoverYn;

    @JsonProperty("CreatedBy")
    private String     createdBy ;
    
    @JsonProperty("Remakrs")
    private String     remarks;
    
    @JsonProperty("FactorParams")
    private List<FactorParamsInsert>     factorParams;
    
}
