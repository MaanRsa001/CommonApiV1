package com.maan.eway.bean;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "deductible_master")
public class EwayDeductibleMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DEDUCT_ID")
	private Integer deductId;

	@Column(name = "DEDUCT_START")
	private Integer deductStart;

	@Column(name = "DEDUCT_END")
	private Integer deductEnd;

	@Column(name = "RATE")
	private Double rate;

	@Column(name = "CALC_TYPE", length = 20)
	private String calcType;

	@Column(name = "AMEND_ID")
	private Integer amendId;

	@Column(name = "STATUS", length = 10)
	private String status;

	@Column(name = "ENTRY_DATE")
	private LocalDateTime entryDate;

	@Column(name = "EFFECTIVE_DATE_START")
	private LocalDateTime effectiveDateStart;

	@Column(name = "EFFECTIVE_DATE_END")
	private LocalDateTime effectiveDateEnd;

	@Column(name = "REMARKS", length = 500)
	private String remarks;

	@Column(name = "BRANCH_CODE", length = 10)
	private String branchCode;

	@Column(name = "COMPANY_ID", length = 20)
	private String companyId;

	@Column(name = "PRODUCT_ID")
	private Integer productId;

	@Column(name = "SECTION_ID")
	private Integer sectionId;

}
