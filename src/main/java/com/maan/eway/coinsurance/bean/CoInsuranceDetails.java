package com.maan.eway.coinsurance.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "co_insurance_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
@ToString
@IdClass(CoInsuranceDetailsId.class)
public class CoInsuranceDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "QUOTE_NO", nullable = false, length = 100)
    private String quoteNo;
    @Id
    @Column(name = "S_NO")
    private Integer slNo;

    @Column(name = "COMPANY_ID")
    private Integer companyId;

    @Column(name = "PRODUCT_ID")
    private Integer productId;

    @Column(name = "PRODUCT_DESC", length = 100)
    private String productDesc;

    @Column(name = "INSURANCE_COMPANY_ID", length = 150)
    private String insuranceCompanyId;

    @Column(name = "INSURANCE_COMPANY_DESC", length = 150)
    private String insuranceCompanyDesc;

    @Column(name = "SHARE_PERCENTAGE", precision = 10, scale = 5)
    private BigDecimal sharePercentage;

    @Column(name = "CO_INSURER_ROLE", length = 10)
    private String coInsurerRole;

    @Column(name = "POLICY_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date policyStartDate;

    @Column(name = "POLICY_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date policyEndDate;

    @Column(name = "SUM_INSURED_LC")
    private BigDecimal sumInsuredLc;

    @Column(name = "SUM_INSURED_FC")
    private BigDecimal sumInsuredFc;

    @Column(name = "PREMIUM_LC")
    private BigDecimal premiumLc;

    @Column(name = "PREMIUM_FC")
    private BigDecimal premiumFc;

    @Column(name = "TAX_AMOUNT_LC")
    private BigDecimal taxAmountLc;

    @Column(name = "TAX_AMOUNT_FC")
    private BigDecimal taxAmountFc;
    
    @Column(name = "COMMISSION_PERCENTAGE" ,precision = 10, scale = 5)
    private BigDecimal commissionPercentage;
    
    @Column(name = "COMMISSION_AMOUNT")
    private BigDecimal commissionAmount;

    @Column(name = "IS_FINACIAL_ENDT", length = 5)
    private String isFinancialEndt;

    @Column(name = "ENDT_STATUS", length = 5)
    private String endtStatus;

    @Column(name = "ENDT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endtDate;

    @Column(name = "ENDT_BY", length = 100)
    private String endtBy;

    @Column(name = "ENDT_CATEG_DESC", length = 100)
    private String endtCategDesc;

    @Column(name = "ENDORSEMENT_REMARKS", length = 100)
    private String endorsementRemarks;

    @Column(name = "ENDORSEMENT_EFFDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endorsementEffDate;

    @Column(name = "ENDT_PREV_POLICY_NO", length = 100)
    private String endtPrevPolicyNo;

    @Column(name = "ENDT_PREV_QUOTE_NO", length = 50)
    private String endtPrevQuoteNo;

    @Column(name = "ENDT_COUNT")
    private Integer endtCount;

    @Column(name = "IS_CHARG_REFUND", length = 10)
    private String isChargRefund;

    @Column(name = "ENDT_TYPE_ID", length = 100)
    private String endtTypeId;

    @Column(name = "ENDT_TYPE_DESC", length = 100)
    private String endtTypeDesc;

    @Column(name = "ENDT_PREMIUM_LC")
    private Double endtPremiumLc;

    @Column(name = "ENDT_PREMIUM_TAX")
    private Double endtPremiumTax;

    @Column(name = "ENDT_COMMISSION", precision = 15, scale = 2)
    private BigDecimal endtCommission;

    @Column(name = "ENTRY_DATE")
    @Temporal(TemporalType.DATE)
    private Date entryDate;

    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "EFFECTIVE_DATE_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveDateStart;

    @Column(name = "EFFECTIVE_DATE_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveDateEnd;

}
 
