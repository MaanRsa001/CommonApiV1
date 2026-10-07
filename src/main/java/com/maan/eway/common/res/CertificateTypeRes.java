package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class CertificateTypeRes {
    
	@JsonProperty("status")
	private Integer status;
	
	@JsonProperty("data")
	private List<Datas> data;

	@JsonProperty("message")
	private String message;
	
	@JsonProperty("hasError")
	private boolean hasError;

}
