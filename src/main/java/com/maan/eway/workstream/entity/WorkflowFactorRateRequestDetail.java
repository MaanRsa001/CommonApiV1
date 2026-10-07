/**
 * @author : Ashok Kumar S 
 * @since  : 09-01-2025
 */
package com.maan.eway.workstream.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workflow_factor_rate_request_detail")
@IdClass(WorkflowFactorRateRequestDetailPK.class)
@NoArgsConstructor
@Getter
@Setter
public class WorkflowFactorRateRequestDetail {
	
	  	@Id
	    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=20)
	    private String requestReferenceNo ;

	    @Id
	    @Column(name="VEHICLE_ID", nullable=false)
	    private Integer vehicleId ;
	    
	    @Id
	    @Column(name="LOCATION_ID", nullable=false)
	    private Integer locationId ;	    

	    @Id
	    @Column(name="COMPANY_ID", nullable=false, length=20)
	    private String companyId ;

	    @Id
	    @Column(name="PRODUCT_ID", nullable=false)
	    private Integer productId ;

	    @Id
	    @Column(name="SECTION_ID", nullable=false)
	    private Integer sectionId ;
	    
	    @Id
	    @Column(name="COVER_ID", nullable=false)
	    private Integer coverId ;

	    @Id
	    @Column(name="SUB_COVER_YN", nullable=false, length=20)
	    private String subCoverYn ;

	    @Id
	    @Column(name="SUB_COVER_ID", nullable=false)
	    private Integer subCoverId ;

	    @Id
	    @Column(name="DISC_LOAD_ID", nullable=false)
	    private Integer discLoadId ;

	    @Id
	    @Column(name="TAX_ID", nullable=false)
	    private Integer taxId ;

	    @Id
	    @Column(name="DISCOUNT_COVER_ID")
	    private Integer discountCoverId;
	     
	    @Id
	    @Column(name="ENDT_COUNT")
	    private BigDecimal endtCount ;
	 
	//Proposal Id & Workflow Id are newly added 
	    @Id
	    @Column(name = "PROPOSAL_ID")
	    private Long proposalId;
	    
	    @Id
	    @Column(name = "WORKFLOW_ID")
	    private Long workflowId;
	    
	    //--- ENTITY DATA FIELDS 
	  	    
	    @Column(name="CD_REFNO", nullable=false, length=20)
	    private String cdRefno ;

	    @Column(name="VD_REFNO", nullable=false, length=20)
	    private String vdRefno ;

	    @Column(name="MS_REFNO", nullable=false, length=20)
	    private String msRefno ;

	    @Column(name="COVER_NAME", length=100)
	    private String coverName ;

	    @Column(name="COVER_DESC", length=200)
	    private String coverDesc ;

	    @Column(name="SUB_COVER_NAME", length=100)
	    private String subCoverName ;

	    @Column(name="SUB_COVER_DESC", length=200)
	    private String subCoverDesc ;

	    @Column(name="CALC_TYPE", length=20)
	    private String calcType ;

	    @Column(name="MINIMUM_PREMIUM")
	    private BigDecimal minimumPremium ;

	    @Column(name="SUM_INSURED")
	    private BigDecimal sumInsured ;

	    @Column(name="RATE")
	    private BigDecimal rate ;

	    @Column(name="FACTOR_TYPE_ID")
	    private BigDecimal factorTypeId ;

	    @Column(name="CURRENCY", length=20)
	    private String currency ;

	    @Column(name="EXCHANGE_RATE")
	    private BigDecimal exchangeRate ;

	    @Column(name="PREMIUM_BEFORE_DISCOUNT_LC")
	    private BigDecimal premiumBeforeDiscountLc ;

	    @Column(name="DEPENDENT_COVER_YN", length=20)
	    private String dependentCoverYn ;

	    @Column(name="DEPENDENT_COVER_ID")
	    private String dependentCoverId ;

	    @Column(name="PREMIUM_BEFORE_DISCOUNT_FC")
	    private BigDecimal premiumBeforeDiscountFc ;

	    @Column(name="PREMIUM_AFTER_DISCOUNT_LC")
	    private BigDecimal premiumAfterDiscountLc ;

	    @Column(name="COVERAGE_TYPE", length=20)
	    private String coverageType ;

	    @Column(name="PREMIUM_AFTER_DISCOUNT_FC")
	    private BigDecimal premiumAfterDiscountFc ;

	    @Column(name="IS_SELECTED", length=20)
	    private String isSelected ;

	    @Column(name="PREMIUM_EXCLUDED_TAX_LC")
	    private BigDecimal premiumExcludedTaxLc ;

	    @Column(name="PREMIUM_EXCLUDED_TAX_FC")
	    private BigDecimal premiumExcludedTaxFc ;

	    @Column(name="PREMIUM_INCLUDED_TAX_LC")
	    private BigDecimal premiumIncludedTaxLc ;

	    @Column(name="PREMIUM_INCLUDED_TAX_FC")
	    private BigDecimal premiumIncludedTaxFc ;

	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="ENTRY_DATE")
	    private Date entryDate ;

	    @Column(name="STATUS", length=20)
	    private String status ;

	    @Column(name="CREATED_BY", length=100)
	    private String createdBy ;

	    @Column(name="TAX_RATE")
	    private BigDecimal taxRate ;

	    @Column(name="TAX_AMOUNT")
	    private BigDecimal taxAmount ;

	    @Column(name="TAX_DESC", length=100)
	    private String taxDesc ;

	    @Column(name="TAX_CALC_TYPE", length=1)
	    private String taxCalcType ;

	    @Column(name="IS_TAX_EXTEMPTED", length=30)
	    private String isTaxExtempted ;

	    @Column(name="TAX_EXEMPT_TYPE", length=20)
	    private String taxExemptType ;

	    @Column(name="TAX_EXEMPT_CODE", length=20)
	    private String taxExemptCode ;

	    @Column(name="MAX_LODING_AMOUNT")
	    private BigDecimal maxLodingAmount ;

	    @Column(name="Is_REFERRAL", length=5)
	    private String isReferral ;

	    @Column(name="REFERRAL_DESCRIPTION", length=1000)
	    private String referralDescription ;

	    @Column(name="USER_OPT", length=5)
	    private String userOpt ;

	    @Column(name="ACTUAL_RATE")
	    private BigDecimal actualRate ;

	    @Column(name="REGUL_SUM_INSURED")
	    private BigDecimal regulSumInsured ;
	    
	    //--- ENTITY LINKS ( RELATIONSHIP )

	    @Column(name="COVER_BASED_ON", length=100)
	    private String coverBasedOn ;

	    @Column(name="REGULATORY_CODE", length=50)
	    private String regulatoryCode ;

	    @Column(name="MULTI_SELECT_YN")
	    private String multiSelectYn;
	    
	    @Column(name="EXCESS_AMOUNT")
	    private BigDecimal excessAmount ;
	    
	    @Column(name="EXCESS_PERCENT")
	    private BigDecimal excessPercent ;
	    
	    @Column(name="EXCESS_DESC")
	    private String excessDesc ;
	    
	    @Column(name="MINIMUM_PREMIUM_YN")
	    private String minimumPremiumYn ;
	    
	    @Column(name="PRO_RATA_YN")
	    private String proRataYn ;
	    
	    @Column(name="PRO_RATA_PERCENT")
	    private BigDecimal proRataPercent ;

	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="COVER_PERIOD_FROM")
	    private Date coverPeriodFrom;
	    
	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="COVER_PERIOD_TO")
	    private Date coverPeriodTo;
	    
	    @Column(name="NO_OF_DAYS")
	    private BigDecimal noOfDays;
	    	    
	    @Column(name="DIFF_PREMIUM_INCLUDED_TAX_LC")
	    private BigDecimal diffPremiumIncludedTaxLc ;

	    @Column(name="DIFF_PREMIUM_INCLUDED_TAX_FC")
	    private BigDecimal diffPremiumIncludedTaxFc ;
	 
	    @Column(name="REGULATORY_RATE")
	    private BigDecimal regulatoryRate ;
	    
	    @Column(name="REGULATORY_SUMINSURED")
	    private BigDecimal regulatorySuminsured ;
	    
	    @Column(name="COVERAGE_LIMIT")
	    private BigDecimal coverageLimit ;

	    @Column(name="SUM_INSURED_LC")    
	    private BigDecimal sumInsuredLc ;
	    	    
	    @Column(name="MINIMUM_PREMIUM_FC")
	    private BigDecimal minimumPremiumFc ;
	    
	    @Column(name="TAX_AMOUNT_LC")
	    private BigDecimal taxAmountLc ;
	    
	    @Column(name="MIN_COVERAGE_LIMIT")
	    private BigDecimal minCoverageLimit;
	    
	    @Column(name="FREE_COVER_LIMIT")
	    private BigDecimal freeCoverLimit;
	    
	    @Column(name="COVER_NAME_LOCAL")
	    private String coverNameLocal ;

	    @Column(name="COVER_DESC_LOCAL")
	    private String coverDescLocal ;

	    @Column(name="SUB_COVER_NAME_LOCAL")
	    private String subCoverNameLocal ;

	    @Column(name="SUB_COVER_DESC_LOCAL")
	    private String subCoverDescLocal ;

	    @Column(name="TAX_DESC_LOCAL")
	    private String taxDescLocal ;

	    @Column(name="EXCESS_DESC_LOCAL")
	    private String excessDescLocal ;
	    
	    @Column(name="MININUM_RATE")
	    private BigDecimal minimumRate ;
	    
	    @Column(name="MINIMUM_RATEYN")
	    private String minimumRateYn;

}
