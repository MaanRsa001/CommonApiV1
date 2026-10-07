package com.maan.eway.common.service.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.res.PortfolioSearchDataRes;

import lombok.Data;

@Data
public class PortFolioSearchGridRes {

	@JsonProperty("TotalCount")
	private Long totalCount ;
	
	@JsonProperty("PortFolioList")
	private List<PortfolioSearchDataRes> portFolioList ;
	
}
