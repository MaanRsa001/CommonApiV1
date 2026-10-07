package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.referal.MasterReferal;

import lombok.Data;

@Data
public class VehicleIdsReq {

	@JsonProperty("Id")
	private Integer vehicleId;

	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("LocationId")
	private Integer locationId;

	@JsonProperty("Covers")
	private List<CoverIdsReq> coverIdList;

	@JsonProperty("MasterReferral")
	private List<MasterReferal> referals;
}
