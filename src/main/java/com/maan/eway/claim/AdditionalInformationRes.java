package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class AdditionalInformationRes {

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("CoverId")
	private String coverId;

	@JsonProperty("LocationId")
	private String locationId;

	@JsonProperty("Param1")
	private String param1;

	@Column(name = "VALUE")
	private String value;

	@Column(name = "Param3")
	private String param3;

	@Column(name = "Param4")
	private String param4;

	@Column(name = "Param5")
	private String param5;

	@Column(name = "Param6")
	private String param6;

	@Column(name = "Param7")
	private String param7;

	@Column(name = "Param8")
	private String param8;

	@Column(name = "Param9")
	private String param9;

	@Column(name = "Param10")
	private String param10;

}
