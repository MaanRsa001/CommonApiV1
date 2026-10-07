package com.maan.eway.common.res;

import java.time.LocalDate;
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
public class ClaimHistoryInfoRes {
	
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
	
	@JsonProperty("EffectiveDateStart")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate effectiveDateStart;
	
	@JsonProperty("EffectiveDateEnd")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate effectiveDateEnd;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("AmendId")
	@JsonFormat(shape = Shape.STRING)
	private Integer amendId;
	
	@JsonProperty("ClaimHistoryInfo")
	private List<ClaimHistoryInfoDetailsRes> claimHistoryInfo;
	
}
