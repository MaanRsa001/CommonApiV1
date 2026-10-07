package com.maan.eway.document.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentListRes {

	@JsonProperty("CommmonDocument")
	private List<ClientDocListRes> commmonDocument;
	
	@JsonProperty("InduvidualDocument")
	private List<ClientDocListRes> induvidualDocument;
	
}
