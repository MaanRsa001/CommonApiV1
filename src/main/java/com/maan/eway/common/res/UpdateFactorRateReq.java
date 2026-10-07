package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.req.CoverIdsReq;

import lombok.Data;

@Data
public class UpdateFactorRateReq {


	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo ;
	
	@JsonProperty("VehicleId")
	private Integer vehicleId;
	
	@JsonProperty("Covers")
	private List<CoverIdsReq> coverIdList;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ProductId")
	private String productId;
	
}
