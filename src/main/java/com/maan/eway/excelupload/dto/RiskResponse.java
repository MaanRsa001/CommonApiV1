package com.maan.eway.excelupload.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskResponse {
	@JsonProperty("RiskId")
    private String riskId;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("SumInsured")
    private BigDecimal sumInsured;
    @JsonProperty("AdditionalInfo")
    private List<Map<String, Object>> additionalInfo;
}
