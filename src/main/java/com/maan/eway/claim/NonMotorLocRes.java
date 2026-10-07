package com.maan.eway.claim;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class NonMotorLocRes {

	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("LocationName")
	private String locationName;
	
	@JsonProperty("CoversRequiredYn")
	private String coversRequiredYn;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("BuildingOwnerYn")
	private String buildingOwnerYn;
	
	@JsonProperty("SectionList")
	private List<NonMotorSectionRes> sectionList;

}
