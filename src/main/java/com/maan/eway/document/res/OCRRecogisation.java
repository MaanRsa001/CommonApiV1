package com.maan.eway.document.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OCRRecogisation {

	@JsonProperty("Accuracy")
	private Double percentage;
	
	@JsonProperty("Value")
	private String value;
	
	@JsonProperty("Id")
	private String id;

	
}
