package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumReportRes {
	
	
	@JsonProperty("FileName")
	private String fileName;
	
	@JsonProperty("Base64")
	private String base64;
	
	@JsonProperty("FilePath")
	private String filePath;

}
