package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KeyAndValueDto {
	
	@JsonProperty("Key")
	private String key;
	@JsonProperty("Value")
    private String value;


}
