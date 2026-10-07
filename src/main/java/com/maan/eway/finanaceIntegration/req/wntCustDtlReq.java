package com.maan.eway.finanaceIntegration.req;

import java.util.Date;

import lombok.Data;

@Data
public class wntCustDtlReq {
	private Long sysId;
	private String code;
	private Boolean corporate;
	private Boolean credit;
	private Boolean gridGlLedgerStatus;
	private Boolean subGlobalLedgerStatus;
	private Double groupLimit;
	private Long groupId;
	private String nameAr;
	private String nameEn;
	private String partyType;
	private String glGroupLabel;
	private String glGroupValue;
	private String glLabel;
	private String glValue;
	private String parentPartyLabel;
	private String parentPartyValue;
	private String glCompLabel;
	private String glCompValue;
	private String glCurrLabel;
	private String glCurrValue;
	private Long custReqRespStatus;
	private Date custReqSentDt;
	private String custReqMessage;
	private Date custResRecdDt;
	private String custRespMessage;
	private String custFinIntgStatus;
	private String custFinIntgRefNo;
	private String ledgerType;
}
