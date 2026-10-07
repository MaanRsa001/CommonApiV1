package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserCustomizedInstallmentReq {

	@JsonProperty("NoOfInstallment")
	private String noOfInstallment;

	@JsonProperty("InstallmentPercent")
	private String installmentPercent;

}
