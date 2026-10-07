package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.EserviceBuildingsDetailsRes;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class BuildingProductDetailsRes {
	
	@JsonProperty("BuildingDetails")
	private  EserviceBuildingsDetailsRes buildingDetails   ;


	@JsonProperty("Covers")
	private  List<Cover> covers ;

}
