package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "eway_user_mapping")
public class EwayUserMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MAPPING_USER_ID")
	private Long mappingUserId;

	@Column(name = "COMPANY_ID", nullable = false, length = 100)
	private String companyId;

	@Column(name = "APPROVER_ID", length = 100)
	private String approverId;

	@Column(name = "LOGIN_ID", nullable = false, length = 100)
	private String loginId;
	@Column(name = "Role", nullable = false, length = 400)
	private String role;

	@Column(name = "ACTIVE", nullable = false, length = 10)
	private String active;

	@Column(name = "EFFECTIVE_DATE_START")
	private Date effectiveDateStart;

	@Column(name = "EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;

	@Column(name = "Branch_code", length = 500)
	private String branchCode;

	// Getters and Setters

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public Long getMappingUserId() {
		return mappingUserId;
	}

	public void setMappingUserId(Long mappingUserId) {
		this.mappingUserId = mappingUserId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getApproverId() {
		return approverId;
	}

	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Date getEffectiveDateStart() {
		return effectiveDateStart;
	}

	public void setEffectiveDateStart(Date effectiveDateStart) {
		this.effectiveDateStart = effectiveDateStart;
	}

	public Date getEffectiveDateEnd() {
		return effectiveDateEnd;
	}

	public void setEffectiveDateEnd(Date effectiveDateEnd) {
		this.effectiveDateEnd = effectiveDateEnd;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
