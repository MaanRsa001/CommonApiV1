package com.maan.eway.excess.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExcessInsertDetailsReq {

	 @JsonProperty("ExcessList")
	  private List<ExcessInsertReq> excessList;

}
