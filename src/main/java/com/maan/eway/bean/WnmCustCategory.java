package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "wnm_cust_category")
public class WnmCustCategory {

	@Column(name = "cc_sys_id")
	private Long ccSysId;

	@Id
	@Column(name = "cc_cust_catg_code", nullable = false, length = 100)
	private String ccCustCatgCode;

	@Column(name = "cc_cust_catg_code_desc", length = 240)
	private String ccCustCatgCodeDesc;

	@Column(name = "cc_cust_type", length = 100)
	private String ccCustType;

	@Column(name = "cc_cust_type_desc", length = 240)
	private String ccCustTypeDesc;

	@Column(name = "cc_ctrl_acnt_code", length = 100)
	private String ccCtrlAcntCode;

	@Column(name = "cc_ctrl_acnt_code_desc", length = 240)
	private String ccCtrlAcntCodeDesc;

	@Column(name = "cc_prefix_yn", length = 5)
	private String ccPrefixYn;

	@Column(name = "cc_prefix_code", length = 50)
	private String ccPrefixCode;

	@Column(name = "cc_cust_code_autogen_yn", length = 5)
	private String ccCustCodeAutogenYn;

	@Column(name = "cc_default_tax_yn", length = 5)
	private String ccDefaultTaxYn;

	@Column(name = "cc_tax_code_1", length = 50)
	private String ccTaxCode1;

	@Column(name = "cc_tax_code_2", length = 50)
	private String ccTaxCode2;

	@Column(name = "cc_tax_code_3", length = 50)
	private String ccTaxCode3;

	@Column(name = "cc_effective_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date ccEffectiveStartDate;

	@Column(name = "cc_effective_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date ccEffectiveEndDate;

	@Column(name = "cc_createdby", length = 240)
	private String ccCreatedBy;

	@Column(name = "cc_createddate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date ccCreatedDate;

	@Column(name = "cc_updatedby", length = 240)
	private String ccUpdatedBy;

	@Column(name = "cc_updateddate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date ccUpdatedDate;

	@Column(name = "company_id", length = 100)
	private String companyId;
}
