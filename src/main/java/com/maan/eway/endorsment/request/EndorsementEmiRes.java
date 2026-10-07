package com.maan.eway.endorsment.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementEmiRes {

	@JsonProperty("IsFinanceEndt") // IsFinanceEndt
	private String isFinaceYn;

	@JsonProperty("IsChargRefund")
	private String isChargRefund;

	@JsonProperty("ChargeOrRefundPremium")
	private String pendingPremiumWithTax;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("NextDueDate")
	private Date nextDueDate;

	@JsonProperty("NextPremium")
	private String nextPremium;

}
