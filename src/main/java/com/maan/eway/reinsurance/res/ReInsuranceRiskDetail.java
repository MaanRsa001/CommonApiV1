package com.maan.eway.reinsurance.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReInsuranceRiskDetail {

	@JsonProperty("RiskId")
	private String riskId;

	@JsonProperty("LocationName")
	private String locationName;

	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("SectionName")
	private String sectionName;

	@JsonProperty("RiskRefNo")
	private String riskRefNo;

	@JsonProperty("RiskCategory")
	private String riskCategory;
	
	@JsonProperty("PMLPerc")
	private String pmlPerc;

	@JsonProperty("CoverFACPerc")
	private String coverFACPerc;

	@JsonProperty("CoverDAFPerc")
	private String coverDAFPerc;
	
	@JsonProperty("CoverDetails")
	private List<ReInsuranceCoverDetail> ReInsuranceCoverDetails;

}
