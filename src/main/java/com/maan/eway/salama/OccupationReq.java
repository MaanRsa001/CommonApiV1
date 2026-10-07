package com.maan.eway.salama;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OccupationReq {

	@JsonProperty("CompanyId")
    private String companyId;
	@JsonProperty("IndustryId")
    private String typeId;
    
}