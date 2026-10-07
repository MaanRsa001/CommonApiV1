package com.maan.eway.bean;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ReInsuranceCoverDetailsId.class)
@Table(name = "reinsurance_cover_details")
public class ReinsuranceCoverDetails {
	
	@Id
	@Column(name = "quote_no")
	private String quoteno;
	
	@Column(name = "uw_sys_id")
	private Integer uwSysId;
	
	@Column(name = "pol_idx")
	private Integer polIdx;
	
	@Id
	@Column(name = "section_id")
	private String sectionId;
	
	@Column(name = "section_name")
	private String sectionName;
	
	@Column(name = "section_code")
	private String sectionCode;
	
	@Id
	@Column(name = "risk_id")
	private Integer riskId;
	
	@Id
	@Column(name = "product_id")
	private String productId;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "product_code")
	private String productCode;
	
	@Id
	@Column(name = "cover_id")
	private String coverId;
	
	@Column(name = "cover_code")
	private String coverCode;
	
	@Id
	@Column(name = "company_id")
	private String companyId;
	
	@Column(name = "cover_name")
	private String coverName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inception_date")
	private Date inceptionDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expiry_date")
	private Date expiryDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "entry_date")
	private Date entryDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "endt_start_date")
	private Date endoStartDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "endt_end_date")
	private Date endoEndDate;
	
	@Column(name = "war_yn")
	private String waryn;
	
//	@Column(name = "currency")
//	private String currency;
	
	@Column(name = "sum_insured_fc")
	private BigDecimal sumInsuredfc;
	
	@Column(name = "sum_insured_lc")
	private BigDecimal suminsuredlc;
	
	@Column(name = "premium_lc")
	private BigDecimal premiumlc;
	
	@Column(name = "premium_fc")
	private BigDecimal premiumfc;
	
	@Column(name = "FAC_Percentage")
	private String facPercantage;
	
	@Column(name = "DAF_Percentage")
	private String dafPerc;
	
	@Column(name = "PML_suminsured_lc")
	private BigDecimal pmlSuminsuredlc;
	
	@Column(name = "PML_suminsured_fc")
	private BigDecimal pmlSuminsuredfc;
	
	@Column(name = "FAC_suminsured_lc")
	private BigDecimal facSuminsuredlc;
	
	@Column(name = "FAC_suminsured_fc")
	private BigDecimal facSuminsuredfc;
	
	@Column(name = "FAC_PML_suminsured_lc")
	private BigDecimal facpmlSuminsuredlc;
	
	@Column(name = "FAC_PML_suminsured_fc")
	private BigDecimal facpmlSuminsuredfc;
	
	@Column(name = "FAC_premium_lc")
	private BigDecimal facPremiumlc;
	
	@Column(name = "FAC_premium_fc")
	private BigDecimal facPremiumfc;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "premia_status")
	private String premiaStatus;
	
	@Column(name = "premia_response")
	private String premiaResponse;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_Date")
	private Date updatedDate;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "cover_rec_type")
	private String coverRecType;
	

}
