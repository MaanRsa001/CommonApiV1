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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="war_rate_master")
@IdClass(WarRateMasterId.class)
public class WarRateMaster {

	@Id
	@Column(name="WAR_RATE_ID",nullable=false)
	private Integer warRateId;
	
	@Id
	@Column(name="BRANCH_CODE",length=20, nullable=false)
	private String branchCode;
	
	@Id
	@Column(name="PRODUCT_ID",length=20, nullable=false)
	private String productId;
	
	@Id
	@Column(name="COMPANY_ID",length=20, nullable=false)
	private String companyId;
	
	@Id
	@Column(name="AMEND_ID",nullable=false)
	private Integer amendId;
	
	@Id
	@Column(name="SECTION_ID",length=20)
	private String sectionId;
	
	
	@Column(name="WAR_RATE_DESC",length=100)
	private String warRateDesc;
	
	@Column(name="WAR_RATE")
	private Double warRate;
	
	@Column(name="MODE_TRANSPORT_ID")
	private Integer modeTransportId;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_START")
	private Date effectiveDateStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name="REMARKS",length=100)
	private String remarks;
	
	@Column(name="STATUS",length=1)
	private String status;
	
	@Column(name="CREATED_BY",length=100)
	private String createdBy;
	
	@Column(name="UPDATED_BY",length=100)
	private String updatedBy;
	
	@Column(name="REGULATORY_CODE",length=20)
	private String regulatoryCode;
	
	@Column(name="CORE_APP_CODE",length=20)
	private String coreAppCode;
	
	
	@Column(name="DOC_REF_NO",length=50)
	private String docRefNo;

	@Column(name="TYPE_ID",length=20)
	private String typeId;

	@Column(name="TYPE_DESC",length=20)
	private String typeDesc;
	
	@Column(name="WAR_RATE_DESC_LOCAL",length=100)
	private String warRateDescLocal;

}
