package com.maan.eway.res.calc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class CalcEngineResponse {
	 @JsonProperty("Covers") 
	  public List<Cover> covers;
}
