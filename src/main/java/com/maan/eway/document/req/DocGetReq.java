package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocGetReq {

	@JsonProperty("UniqueId")
	private String uniqueId;

}
