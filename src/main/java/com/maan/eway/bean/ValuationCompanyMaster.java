package com.maan.eway.bean;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@Table(name="valuation_company_master")
@IdClass(ValuationCompanyMasterId.class)
public class ValuationCompanyMaster {
	
	//---PRIMARY KEYS----
	
	@Id
	@Column(name="SNO",nullable=false)
	private Long sNo;
	
	@Id
	@Column(name="COMPANY_ID",nullable=false,length=10)
	private String companyId;
	
	@Id
	@Column(name="VAL_COMPANY_CODE",nullable=false,length=50)
	private String valCompanyCode;
	
	@Id
	@Column(name="BRANCH_CODE",nullable=false,length=10)
	private String branchCode;
	
	@Id
	@Column(name="AMEND_ID",nullable=false)
	private Integer amendId;
	
	//---OTHER KEYS---
	
	@Column(name="VAL_COMPANY_NAME",length=500)
	private String valCompanyName;
	
	@Column(name="AUTH_API",length=500)
	private String authApi;
	
	@Column(name="AUTH_USER_NAME",length=100)
	private String authUserName;
	
	@Column(name="AUTH_PASSWORD ",length=100)
	private String authPassword;

	@Column(name="CREATE_API ",length=500)
	private String createApi;
	
	@Column(name="STATUS_API",length=500)
	private String statusApi;
	
	@Column(name="GET_DETAIL_API",length=500)
	private String getDetailApi;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_START",nullable=false)
	private Date effectiveDateStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_END",nullable=false)
	private Date effectiveDateEnd;
	
	@Column(name="CREATED_BY",length=100)
	private String createdBy;
	
	@Column(name="UPDATED_BY",length=100)
	private String updatedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE",nullable=false)
	private Date updatedDate;
	
	@Column(name="STATUS",length=10)
	private String status;
	
	@Temporal(TemporalType.DATE)
	@Column(name="ENTRY_DATE",nullable=false)
	private Date entryDate;
	
	@Column(name="REMRAKS",length=100)
	private String remarks;
}
