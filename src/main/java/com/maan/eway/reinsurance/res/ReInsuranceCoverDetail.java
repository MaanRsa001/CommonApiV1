package com.maan.eway.reinsurance.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReInsuranceCoverDetail {

	@JsonProperty("CoverId")
	private String coverId;

	@JsonProperty("CoverName")
	private String coverName;

	@JsonProperty("SumInsured")
	private String sumInsured;

	@JsonProperty("PMLSumInsured")
	private String pmlSumInsured;

	@JsonProperty("Premium")
	private String premium;

	@JsonProperty("FACPerc")
	private String facPerc;

	@JsonProperty("FACSumInsured")
	private String facSumInsured;

	@JsonProperty("FACPML")
	private String facpml;

	@JsonProperty("FACPermium")
	private String facPermium;

	@JsonProperty("DAFTreartyPerc")
	private String dafTreartyPerc;

}
