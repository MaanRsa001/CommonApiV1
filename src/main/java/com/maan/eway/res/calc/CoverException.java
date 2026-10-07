package com.maan.eway.res.calc;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class CoverException  extends Exception implements Serializable {
	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")	
	private Boolean isError;
}
