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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ReInsuranceRiskDetailsId.class)
@Table(name = "reinsurance_risk_details")
public class ReInsuranceRiskDetails {
	
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
	
	@Id
	@Column(name = "risk_id")
	private Integer riskId;
	
	@Id
	@Column(name = "company_id")
	private String companyId;
	
	@Column(name = "company_code")
	private String companyCode;
	
	@Id
	@Column(name = "product_id")
	private String productId;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "product_code")
	private String productCode;
	
	@Column(name = "section_code")
	private String sectionCode;
	
	@Column(name = "risk_ref_no")
	private Integer riskrefno;
	
	@Column(name = "PML_Percentage")
	private String pmlPerc;
	
	@Column(name = "location_name")
	private String locationName;
	
	@Column(name = "risk_address")
	private String riskaddress;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "premia_status")
	private String premiaStatus;
	
	@Column(name = "cover_FAC_PERC")
	private String coverFAC;
	
	@Column(name = "cover_DAF_PERC")
	private String coverDAF;
	
	@Column(name = "risk_category")
	private String riskCategory;
	
	@Column(name = "premia_response")
	private String premiaResponse;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inception_date")
	private Date inceptionDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expiry_date")
	private Date expireDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "entry_date")
	private Date entryDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_date")
	private Date updateDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "endt_start_date")
	private Date endoStartDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "endt_end_date")
	private Date endoEndDate;
	
	@Column(name = "endt_type_name")
	private String endtTypeName;
	
	@Column(name = "endt_type_id")
	private String endtTypeId;
	
	@Column(name = "endt_type_code")
	private String endtTypeCode;

    @Column(name="endt_type_category_id")
    private Integer    endtTypeCategoryId ;
    
    @Column(name="endt_type_category")
    private String    endtTypeCategory ;
    
	@Column(name = "endt_no")
	private String endtNo;
	
	@Column(name = "policy_no")
	private String policyNo;
	
	@Column(name = "uw_divn_id")
	private String uwDivnId;
	
	@Column(name = "uw_type")
	private String uwType;
	
	@Column(name = "uw_dept_id")
	private String uwDeptId;
	
	@Column(name = "uw_bus_type")
	private String uwBusType;
	
	@Column(name = "uw_ri_basis")
	private String uwRiBasis;
	
	@Column(name = "uw_prem_cur")
	private String uwPremCur;
	
	@Column(name = "risk_si_cur")
	private String riskSiCurr;
	
	@Column(name = "risk_rec_type")
	private String riskRecType;
	
	@Column(name = "uw_lob")
	private String uwLob;
	
	@Column(name = "branch_name")
	private String branchName;
	
	@Column(name = "uw_divn_code")
	private String uwDivnCode;
	
	@Column(name = "uw_dept_code")
	private String uwDeptCode;
	
	@Column(name = "approval_status")
	private String approvalStatus;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "approval_date")
	private Date approvalDate;
	
	@Column(name = "approval_user_id")
	private String approvalUserId;

	@Column(name = "insured_code")
	private String insuredCode;

	@Column(name = "insured_name")
	private String insuredName;

	@Column(name = "agbrk_code")
	private String agBrkCode;

	@Column(name = "agbrk_name")
	private String agBrkName;

}
