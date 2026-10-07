package com.maan.eway.salesLead.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetUploadDocumentListRes {

	@JsonProperty("FileId")
	private String fileId;
	
	@JsonProperty("FileName")
	private String fileName;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("Status")
	private String status;
	
}
