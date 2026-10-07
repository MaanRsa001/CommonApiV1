package com.maan.eway.jasper.req;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportRes {

	@JsonProperty("TotalCount")
	private String totalCount ;
	@JsonProperty("ReportList")
	private List<Map<String,Object>> reportList ;
	
	
}
