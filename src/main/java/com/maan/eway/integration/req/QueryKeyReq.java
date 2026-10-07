package com.maan.eway.integration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QueryKeyReq {

	@JsonProperty("QueryKey")
	private String queryKey;
}
