package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SequenceGenerateRes {
	
	@JsonProperty("GeneratedValue")
	private String generatedValue;

}
