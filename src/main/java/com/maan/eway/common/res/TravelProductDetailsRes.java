package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class TravelProductDetailsRes {

	@JsonProperty("TravelPassengerDetails")
	private  TravelPassDetailsRes travelPassengerDetails;
	
	@JsonProperty("Covers")
	private  List<Cover> covers ;
}
