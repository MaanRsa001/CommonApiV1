package com.maan.eway.excelupload.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({
    "coverId",
    "coverName",
    "sumInsured",
    "columns",
    "templateBase64",
    "additionaInfo"
})
public class CoverResponse {

	@JsonProperty("CoverId")
    private String coverId;
	@JsonProperty("CoverName")
    private String coverName;
	@JsonProperty("Suminsured")
    private BigDecimal sumInsured;   
	@JsonProperty("ColumnDetails")
    private List<ColumnMeta> columns;
	@JsonProperty("TemplateDownload")
    private String templateBase64;
	@JsonProperty("AdditionaInfo")
	private List<Map<String, Object>> additionaInfo;
	@JsonProperty("RiskList")
	private List<RiskResponse> riskList;
}

