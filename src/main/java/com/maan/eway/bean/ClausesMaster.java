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
@Table(name="clauses_master")
@IdClass(ClausesMasterId.class)
public class ClausesMaster {

	@Id
	@Column(name="CLAUSES_ID",nullable=false)
	private Integer clausesId;
	
	@Id
	@Column(name="BRANCH_CODE",length=20, nullable=false)
	private String branchCode;
	
	@Id
	@Column(name="COMPANY_ID",length=20, nullable=false)
	private String companyId;

	@Id
	@Column(name="PRODUCT_ID",length=20, nullable=false)
	private String productId;

	@Id
	@Column(name="SECTION_ID",length=20, nullable=false)
	private String sectionId;
//	
//	@Id
//	@Column(name="POLICY_TYPE",length=20, nullable=false)
//	private String policyType;

	
	@Id
	@Column(name="AMEND_ID",nullable=false)
	private Integer amendId;
	//
	@Column(name="CLAUSES_DESCRIPTION",length=200)
	private String clausesDescription;
	
	@Column(name = "CLAUSES_SHORT_DESC")
	private String clausesShortDesc;
	
	
	
	@Column(name="EXTRA_COVER_ID")
	private Integer extraCoverId;
	
	@Column(name="DISPLAY_ORDER")
	private Integer displayOrder;
	
	@Column(name="PDF_LOCATION",length=200)
	private String pdfLocation;
	
	@Column(name="OPTIONAL_TYPE",length=10)
	private String optionalType;
	
	@Column(name="INT_CODE",length=100)
	private String intCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_START",nullable=false)
	private Date effectiveDateStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_END",nullable=false)
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
	
	@Column(name="DOC_REF_NO",length=20)
	private String docRefNo;

	@Column(name="TYPE_ID",length=20)
	private String typeId;

	@Column(name="TYPE_DESC",length=20)
	private String typeDesc;
	
	@Column(name = "BROKER_CODE")
	private String brokerCode;
	
	@Column(name="CLAUSES_DESCRIPTION_LOCAL",length=300)
	private String clausesDescriptionLocal;
	
	@Column(name="CATEGORY_DESCRIPTION",length=500)
	private String categoryDescription;
	
	@Column(name="PDF_NAME")
	private String pdfName;
	
	@Column(name="COVER_DESC")
	private String coverDesc;
	
	@Column(name="COVER_ID")
	private Integer coverId;

}
