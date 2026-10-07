package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Datas {

	
	@JsonProperty("value")
	private String value;
	
	@JsonProperty("key")
	private String key;
}
