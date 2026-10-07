package com.maan.eway.master.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserCustomizedEmiReq {

	@JsonProperty("AdvancePercent")
	private String advancePercent;

	@JsonProperty("InterestPercent")
	private String interestPercent;

	@JsonProperty("CustomizedInstallmentYn")
	private String customizedInstallmentYn;

	@JsonProperty("MonthGapYn")
	private String MonthGapYn;

	@JsonProperty("MonthGap")
	private String monthGap;

	@JsonProperty("CustomizedInstallment")
	private List<UserCustomizedInstallmentReq> customizedInstallment;
}
