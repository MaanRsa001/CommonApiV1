package com.maan.eway.preinspection;



import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRes {
	
	@JsonProperty("Response")
	private String response;
	@JsonProperty("Url")
	private String url;

}
