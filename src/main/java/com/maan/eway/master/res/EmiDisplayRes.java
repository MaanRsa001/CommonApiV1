package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiDisplayRes implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("EmiDetails")
	private EmiInfoListRes emiInfoRes;
	
	@JsonProperty("CompanyEmiInfo")
	private EmiCompanyInfoListRes companyEmiInfo;

	@JsonProperty("EmiPremium")
	private List<EmiDisplayListRes> emiPremium;
	
	@JsonProperty("EmiYn")
	private String emiYn;
	
	@JsonProperty("EmiYnDesc")
	private String emiYnDesc;
	
}
