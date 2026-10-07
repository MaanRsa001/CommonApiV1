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
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ExcessMasterId.class)
@Table(name = "excess_master")
@Getter
@Setter
public class ExcessMaster {

	@Id
	@Column(name = "EXCESS_ID")
	private Integer excessId;

	@Id
	@Column(name = "COMPANY_ID")
	private String companyId;

	@Id
	@Column(name = "PRODUCT_ID")
	private String productId;

	@Id
	@Column(name = "SECTION_ID")
	private String sectionId;

	
	@Id
	@Column(name = "AMEND_ID")
	private Integer amendId;

	@Column(name = "EXCESS_PERCENTAGE")
	private Integer excessPercentage;

	@Column(name = "EXCESS_AMOUNT")
	private Double excessAmount;

	@Column(name = "EXCESS_DESCRIPTION")
	private String excessDescription;

	@Column(name = "CURRENCY")
	private String currency;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EFFECTIVE_DATE_START")
	private Date effectiveDateStart;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;

	@Temporal(TemporalType.DATE)
     @Column(name = "ENTRY_DATE")
	private Date entryDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "REGULATORY_CODE")
	private String regulatoryCode;

	@Column(name = "CORE_APP_CODE")
	private String coreAppCode;
	
	@Column(name = "BRANCH_CODE")
	private String branchCode;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "COVER_NAME")
	private String coverName;
	
	@Column(name="TYPE_ID",length=20)
	private String typeId;

	@Column(name="TYPE_DESC",length=20)
	private String typeDesc;
	

	@Column(name="COVER_ID")
	private String coverId;

}
