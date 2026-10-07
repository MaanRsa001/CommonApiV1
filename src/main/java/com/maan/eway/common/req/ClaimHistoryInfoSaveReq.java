package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ClaimHistoryInfoSaveReq {
	@JsonProperty("CompanyId")
	@JsonFormat(shape = Shape.STRING)
	private Integer companyId;
	
	@JsonProperty("ProductId")
	@JsonFormat(shape = Shape.STRING)
	private Integer productId;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
		
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ClaimHistoryInfo")
	private List<ClaimHistoryInfoDetailsReq> claimHistoryInfo;

}
