package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CertificateDetailsReq {


	
	@JsonProperty("CertificateNo")
	private String certificateNo;
	
    @JsonProperty("UsageId")
     private Integer usageId;
	
    @JsonProperty("CompanyId")
    private Integer companyid;
}
