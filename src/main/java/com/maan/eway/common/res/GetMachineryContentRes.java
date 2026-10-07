package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.MachineryDropDownRes;

import lombok.Data;

@Data
public class GetMachineryContentRes {
	@JsonProperty("TotalSumInsured")
	private BigDecimal totalSumInsured;
	
	@JsonProperty("ContentTypeRes")
	private List<MachineryDropDownRes> contentTypeRes;
}
