package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.SectionDetails;

import lombok.Data;

@Data
public class PaccGetRes {

	@JsonProperty("Suminsured")
    private String     suminsured ;
	
	@JsonProperty("OccupationType")
    private String    occupationType;

	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("CategoryId")
    private String    categoryId;
	
	@JsonProperty("RiskId")
    private String    riskId;
	@JsonProperty("DocumentsTitle")
    private String    documentsTitle;
	
	@JsonProperty("SectionId")
	private  String sectionId;	

	@JsonProperty("EmpLiabilitySi")
    private String empLiabilitySi    ;
	
	@JsonProperty("LiabilityOccupationId")
    private String liabilityOccupationId    ;
	
	@JsonProperty("FidEmpSi")
    private String fidEmpSi    ;
	
	@JsonProperty("FidEmpCount")
    private String fidEmpCount    ;
	
	 
    @JsonProperty("IndustryName")
	private String       industryName;
	
	@JsonProperty("NatureOfBusinessId")
	private String       natureOfBusinessId ;
	    
	@JsonProperty("NatureOfBusinessDesc")
	private String       natureOfBusinessDesc;
	    
	@JsonProperty("TotalNoOfEmployees")
	private String       totalNoOfEmployees;
	    

	@JsonProperty("IndustryId")
	private  String industryId;	
	

	@JsonProperty("LocationId")
   	private  String locationId;	
    
    @JsonProperty("LocationName")
   	private  String locationName;


	@JsonProperty("SectionDetails")
    private List<SectionDetails>    sectionDetails;
}
