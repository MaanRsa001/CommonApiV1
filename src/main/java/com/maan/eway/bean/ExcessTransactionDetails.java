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
@IdClass(ExcessTransactionDetailsId.class)
@Table(name = "excess_transaction_details")
@Getter
@Setter
public class ExcessTransactionDetails {

	@Id
	@Column(name = "EXCESS_ID")
	private Integer excessId;

	@Id
	@Column(name = "REQUEST_REFERENCE_NO")
	private String requestReferenceNo;

	@Id
	@Column(name = "PRODUCT_ID")
	private String productId;

	@Id
	@Column(name = "SECTION_ID")
	private String sectionId;

	@Column(name = "COVER_ID")
	private String coverId;

	@Column(name = "EXCESS_PERCENTAGE")
	private Integer excessPercentage;

	@Column(name = "EXCESS_AMOUNT")
	private Double excessAmount;

	@Column(name = "EXCESS_DESCRIPTION")
	private String excessDescription;

	@Column(name = "CURRENCY")
	private String currency;

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

	@Column(name = "LOCATION_ID")
	private String locationId;

	@Column(name = "RISK_ID")
	private Integer riskId;
}
