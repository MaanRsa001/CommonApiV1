package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BuildingLocationDetails {

	@JsonProperty("LocationId")
   	private  String locationId;	
    
    @JsonProperty("DocumentsTitle")
    private String    documentsTitle;
    
    @JsonProperty("LocationName")
   	private  String locationName;
    
    @JsonProperty("RiskId")
   	private  String riskId;	
    
    @JsonProperty("Suminsured")
    private String     suminsured ;
    
	@JsonProperty("SectionId")
	private  String sectionId;	
	
	@JsonProperty("EndorsementYn")
    private String     endorsementYn;
}
