package com.maan.eway.renewal.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackingDetails {
	@JsonProperty("CustomerName")
	private String customerName;

	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("PolicyEndDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private Date polExpDt;

	@JsonProperty("Premium")
	private Double newPremium;

	@JsonProperty("Status")
	private String status;

//	@JsonProperty("Reason")
//	private String reason;
//
//	@JsonProperty("BranchName")
//	private String branchName;
//
//	@JsonProperty("LoginId")
//	private String loginId;
}
