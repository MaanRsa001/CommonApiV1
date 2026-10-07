package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wnt_cust_dtl")

@Data
public class WntCustDtl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sysId")
	private Long sysId;

	@Column(name = "code")
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

	@Column(name = "ledgerType")
	private String ledgerType;

	@Column(name = "nameAr")
	private String nameAr;

	@Column(name = "nameEn")
	private String nameEn;

	@Column(name = "partyType")
	private String partyType;

	@Column(name = "glGroupLabel")
	private String glGroupLabel;

	@Column(name = "glGroupValue")
	private String glGroupValue;

	@Column(name = "glLabel")
	private String glLabel;

	@Column(name = "glValue")
	private String glValue;

	@Column(name = "parentPartyLabel")
	private String parentPartyLabel;

	@Column(name = "parentPartyValue")
	private String parentPartyValue;

	@Column(name = "glCompLabel")
	private String glCompLabel;

	@Column(name = "glCompValue")
	private String glCompValue;

	@Column(name = "glCurrLabel")
	private String glCurrLabel;

	@Column(name = "glCurrValue")
	private String glCurrValue;

	@Column(name = "cust_req_resp_status")
	private Long custReqRespStatus;

	@Column(name = "cust_req_sent_dt")
	private Date custReqSentDt;

	@Column(name = "cust_req_message", columnDefinition = "text")
	private String custReqMessage;

	@Column(name = "cust_res_recd_dt")
	private Date custResRecdDt;

	@Column(name = "cust_resp_message", columnDefinition = "text")
	private String custRespMessage;

	@Column(name = "cust_fin_intg_status", length = 240)
	private String custFinIntgStatus;

	@Column(name = "cust_fin_intg_ref_no", length = 240)
	private String custFinIntgRefNo;

	@Column(name = "upd_gl_label")
	private String updGlLabel;

	@Column(name = "upd_gl_value")
	private String updGlValue;

	@Column(name = "upd_req_resp_status")
	private Long custUpdReqRespStatus;

	@Column(name = "upd_req_sent_dt")
	private Date custUpdReqSentDt;

	@Column(name = "upd_req_message", columnDefinition = "text")
	private String custUpdReqMessage;

	@Column(name = "upd_res_recd_dt")
	private Date custUpdResRecdDt;

	@Column(name = "upd_resp_message", columnDefinition = "text")
	private String custUpdRespMessage;

	@Column(name = "upd_fin_intg_status", length = 240)
	private String custUpdFinIntgStatus;

	@Column(name = "Company_id")
	private String companyId;

	public Long getSysId() {
		return sysId;
	}

	public void setSysId(Long sysId) {
		this.sysId = sysId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getCorporate() {
		return corporate;
	}

	public void setCorporate(Boolean corporate) {
		this.corporate = corporate;
	}

	public Boolean getCredit() {
		return credit;
	}

	public void setCredit(Boolean credit) {
		this.credit = credit;
	}

	public Boolean getGridGlLedgerStatus() {
		return gridGlLedgerStatus;
	}

	public void setGridGlLedgerStatus(Boolean gridGlLedgerStatus) {
		this.gridGlLedgerStatus = gridGlLedgerStatus;
	}

	public Boolean getSubGlobalLedgerStatus() {
		return subGlobalLedgerStatus;
	}

	public void setSubGlobalLedgerStatus(Boolean subGlobalLedgerStatus) {
		this.subGlobalLedgerStatus = subGlobalLedgerStatus;
	}

	public Double getGroupLimit() {
		return groupLimit;
	}

	public void setGroupLimit(Double groupLimit) {
		this.groupLimit = groupLimit;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getLedgerType() {
		return ledgerType;
	}

	public void setLedgerType(String ledgerType) {
		this.ledgerType = ledgerType;
	}

	public String getNameAr() {
		return nameAr;
	}

	public void setNameAr(String nameAr) {
		this.nameAr = nameAr;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getPartyType() {
		return partyType;
	}

	public void setPartyType(String partyType) {
		this.partyType = partyType;
	}

	public String getGlGroupLabel() {
		return glGroupLabel;
	}

	public void setGlGroupLabel(String glGroupLabel) {
		this.glGroupLabel = glGroupLabel;
	}

	public String getGlGroupValue() {
		return glGroupValue;
	}

	public void setGlGroupValue(String glGroupValue) {
		this.glGroupValue = glGroupValue;
	}

	public String getGlLabel() {
		return glLabel;
	}

	public void setGlLabel(String glLabel) {
		this.glLabel = glLabel;
	}

	public String getGlValue() {
		return glValue;
	}

	public void setGlValue(String glValue) {
		this.glValue = glValue;
	}

	public String getParentPartyLabel() {
		return parentPartyLabel;
	}

	public void setParentPartyLabel(String parentPartyLabel) {
		this.parentPartyLabel = parentPartyLabel;
	}

	public String getParentPartyValue() {
		return parentPartyValue;
	}

	public void setParentPartyValue(String parentPartyValue) {
		this.parentPartyValue = parentPartyValue;
	}

	public String getGlCompLabel() {
		return glCompLabel;
	}

	public void setGlCompLabel(String glCompLabel) {
		this.glCompLabel = glCompLabel;
	}

	public String getGlCompValue() {
		return glCompValue;
	}

	public void setGlCompValue(String glCompValue) {
		this.glCompValue = glCompValue;
	}

	public String getGlCurrLabel() {
		return glCurrLabel;
	}

	public void setGlCurrLabel(String glCurrLabel) {
		this.glCurrLabel = glCurrLabel;
	}

	public String getGlCurrValue() {
		return glCurrValue;
	}

	public void setGlCurrValue(String glCurrValue) {
		this.glCurrValue = glCurrValue;
	}

	public Long getCustReqRespStatus() {
		return custReqRespStatus;
	}

	public void setCustReqRespStatus(Long custReqRespStatus) {
		this.custReqRespStatus = custReqRespStatus;
	}

	public Date getCustReqSentDt() {
		return custReqSentDt;
	}

	public void setCustReqSentDt(Date custReqSentDt) {
		this.custReqSentDt = custReqSentDt;
	}

	public String getCustReqMessage() {
		return custReqMessage;
	}

	public void setCustReqMessage(String custReqMessage) {
		this.custReqMessage = custReqMessage;
	}

	public Date getCustResRecdDt() {
		return custResRecdDt;
	}

	public void setCustResRecdDt(Date custResRecdDt) {
		this.custResRecdDt = custResRecdDt;
	}

	public String getCustRespMessage() {
		return custRespMessage;
	}

	public void setCustRespMessage(String custRespMessage) {
		this.custRespMessage = custRespMessage;
	}

	public String getCustFinIntgStatus() {
		return custFinIntgStatus;
	}

	public void setCustFinIntgStatus(String custFinIntgStatus) {
		this.custFinIntgStatus = custFinIntgStatus;
	}

	public String getCustFinIntgRefNo() {
		return custFinIntgRefNo;
	}

	public void setCustFinIntgRefNo(String custFinIntgRefNo) {
		this.custFinIntgRefNo = custFinIntgRefNo;
	}

	public String getUpdGlLabel() {
		return updGlLabel;
	}

	public void setUpdGlLabel(String updGlLabel) {
		this.updGlLabel = updGlLabel;
	}

	public String getUpdGlValue() {
		return updGlValue;
	}

	public void setUpdGlValue(String updGlValue) {
		this.updGlValue = updGlValue;
	}

	public Long getCustUpdReqRespStatus() {
		return custUpdReqRespStatus;
	}

	public void setCustUpdReqRespStatus(Long custUpdReqRespStatus) {
		this.custUpdReqRespStatus = custUpdReqRespStatus;
	}

	public Date getCustUpdReqSentDt() {
		return custUpdReqSentDt;
	}

	public void setCustUpdReqSentDt(Date custUpdReqSentDt) {
		this.custUpdReqSentDt = custUpdReqSentDt;
	}

	public String getCustUpdReqMessage() {
		return custUpdReqMessage;
	}

	public void setCustUpdReqMessage(String custUpdReqMessage) {
		this.custUpdReqMessage = custUpdReqMessage;
	}

	public Date getCustUpdResRecdDt() {
		return custUpdResRecdDt;
	}

	public void setCustUpdResRecdDt(Date custUpdResRecdDt) {
		this.custUpdResRecdDt = custUpdResRecdDt;
	}

	public String getCustUpdRespMessage() {
		return custUpdRespMessage;
	}

	public void setCustUpdRespMessage(String custUpdRespMessage) {
		this.custUpdRespMessage = custUpdRespMessage;
	}

	public String getCustUpdFinIntgStatus() {
		return custUpdFinIntgStatus;
	}

	public void setCustUpdFinIntgStatus(String custUpdFinIntgStatus) {
		this.custUpdFinIntgStatus = custUpdFinIntgStatus;
	}

}
