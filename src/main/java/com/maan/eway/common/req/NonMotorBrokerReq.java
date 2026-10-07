package com.maan.eway.common.req;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NonMotorBrokerReq {

	@JsonProperty("ApplicationId")
	private String applicationId;
	@JsonProperty("BrokerCode")
	private String brokerCode;
	@JsonProperty("SubUsertype")
	private String subUserType;
	@JsonProperty("LoginId")
	private String loginId;
	@JsonProperty("AgencyCode")
	private String agencyCode;
	@JsonProperty("UserType")
	private String userType;
	@JsonProperty("BankCode")
	private String bankCode;
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	@JsonProperty("SourceTypeId")
	private String sourceTypeId;
	@JsonProperty("SourceType")
	private String sourceType;
	@JsonProperty("CustomerCode")
	private String customerCode;
	@JsonProperty("CustomerName")
	private String customerName;
	@JsonProperty("BdmCode")
	private String bdmCode;
	@JsonProperty("BdmName")
	private String bdmName;
	@JsonProperty("CommissionType")
	private String commissionType;

	@JsonProperty("BusinessSourceId")
	private String businessSourceId;

	@JsonProperty("BusinessSource")
	private String businessSource;
}
