package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wnt_clm_cust_dtl")

public class WntClmCustDtl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sysId")
	private Long sysId;

	@Column(name = "code", length = 100)
	private String code;

	@Column(name = "corporate")
	private Boolean corporate;

	@Column(name = "credit")
	private Boolean credit;

	@Column(name = "gridGlLedgerStatus")
	private Boolean gridGlLedgerStatus;

	@Column(name = "subGlobalLedgerStatus")
	private Boolean subGlobalLedgerStatus;

	@Column(name = "groupLimit")
	private Double groupLimit;

	@Column(name = "groupId")
	private Long groupId;

	@Column(name = "ledgerType", length = 255)
	private String ledgerType;

	@Column(name = "nameAr", length = 255)
	private String nameAr;

	@Column(name = "nameEn", length = 255)
	private String nameEn;

	@Column(name = "partyType", length = 50)
	private String partyType;

	@Column(name = "glGroupLabel", length = 255)
	private String glGroupLabel;

	@Column(name = "glGroupValue", length = 255)
	private String glGroupValue;

	@Column(name = "glLabel", length = 255)
	private String glLabel;

	@Column(name = "glValue", length = 255)
	private String glValue;

	@Column(name = "parentPartyLabel", length = 255)
	private String parentPartyLabel;

	@Column(name = "parentPartyValue", length = 255)
	private String parentPartyValue;

	@Column(name = "glCompLabel", length = 255)
	private String glCompLabel;

	@Column(name = "glCompValue", length = 255)
	private String glCompValue;

	@Column(name = "glCurrLabel", length = 255)
	private String glCurrLabel;

	@Column(name = "glCurrValue", length = 255)
	private String glCurrValue;

	@Column(name = "cust_req_resp_status")
	private Long custReqRespStatus;

	@Column(name = "cust_req_sent_dt")
	@Temporal(TemporalType.TIMESTAMP)
	private Date custReqSentDt;

	@Column(name = "cust_req_message", columnDefinition = "TEXT")
	private String custReqMessage;

	@Column(name = "cust_res_recd_dt")
	@Temporal(TemporalType.TIMESTAMP)
	private Date custResRecdDt;

	@Column(name = "cust_resp_message", columnDefinition = "TEXT")
	private String custRespMessage;

	@Column(name = "cust_fin_intg_status", length = 240)
	private String custFinIntgStatus;

	@Column(name = "cust_fin_intg_ref_no", length = 240)
	private String custFinIntgRefNo;

	@Column(name = "company_id", length = 100)
	private String companyId;

	@Column(name = "customer_reference_no", length = 200)
	private String customerReferenceNo;
	// You can generate them using Lombok or IDE tools
}
