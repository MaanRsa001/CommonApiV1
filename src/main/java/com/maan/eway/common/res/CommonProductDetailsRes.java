package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class CommonProductDetailsRes {

	@JsonProperty("CommonDetails")
	private  CommonDetailsRes commonDetails   ;


	@JsonProperty("Covers")
	private  List<Cover> covers ;
}
