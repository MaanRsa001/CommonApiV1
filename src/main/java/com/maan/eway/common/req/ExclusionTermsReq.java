package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data

public class ExclusionTermsReq {
	@JsonProperty("Id")
	private String id;

	@JsonProperty("SubId")
	private List<String> subId;

}
