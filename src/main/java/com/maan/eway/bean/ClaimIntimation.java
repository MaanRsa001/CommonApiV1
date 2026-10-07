package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@DynamicInsert
@DynamicUpdate
@Table(name = "claim_intimation")
@IdClass(ClaimIntimationId.class)
public class ClaimIntimation implements Serializable{
	private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "COMPANY_ID", nullable = false)
    private String companyId;

    @Id
    @Column(name = "CLAIM_REF_NO", nullable = false, length = 100)
    private String claimRefNo;

    @Id
    @Column(name = "POLICY_NO", nullable = false, length = 100)
    private String policyNo;

    @Column(name = "BRANCH_CODE")
    private String branchCode;
    
    @Column(name = "PRODUCT_ID")
    private String productId;
    
    @Column(name = "PRODUCT_NAME")
    private String productName;
    
    @Column(name = "COMPANY_NAME")
    private String companyName;
    
    @Column(name ="BRANCH_NAME")
    private String branchName;
    
    @Column(name = "CLAIM_NO", nullable = false, length = 100)
    private String claimNo;

    @Column(name = "PRODUCT_TYPE")
    private String productType;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "CLAIM_INTIMATION_DATE")
    private Date claimIntimationDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "LOSS_DATE")
    private Date lossDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "INTIMATION_DATE")
    private Date intimationDate;

    @Column(name = "LOSS_LOCATION")
    private String lossLocation;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODE_OF_INTIMATION")
    private String modeOfIntimation;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_DATE")
    private Date effectiveDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name="POLICY_START_DATE")
    private Date policyStartDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name="POLICY_END_DATE")
    private Date policyEndDate;
    
    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "LOGIN_ID")
    private String loginId;
    
    @Column(name = "LOSS_AMOUNT")
    private BigDecimal lossAmount;
    
    @Column(name = "RECOVERY_YN")
    private String recoverYN;
    
    @Column(name = "EVENT_ID")
    private String eventId;
    
    @Column(name = "EVENT_TYPE")
    private String eventType;
    
    @Column(name = "UW_SYS_ID")
    private Integer uwSysid;
    
    @Column(name = "AH_POL_IDX")
    private Integer ahPolIdx;
    
    @Column(name = "UW_LOB")
    private String uwLob;
    
    @Column(name = "CURRENCY_CODE")
    private String currencyCode;
    
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    
    @Column(name = "CLAIM_STATUS")
    private String claimStatus;
    
    @Column(name = "CLAIM_STATUS_DESC")
    private String claimStatusDesc;
    
    @Column(name = "NEXT_CLAIM_STATUS")
    private String nextClaimStatus;
    
    @Column(name = "NEXT_CLAIM_STATUS_DESC")
    private String nextClaimStatusDesc;
    
    @Column(name = "END_NO")
    private String endNo;
    
    @Column(name = "COMPANY_CODE")
    private String companyCode;
    
    @Column(name = "DIVISION_CODE")
    private String divisionCode;

    @Column(name = "CUST_CODE")
    private String custCode;
    
    @Column(name = "CUST_NAME")
    private String custName;
    
    @Column(name = "DEPT_CODE")
    private String deptCode;
    
    @Column(name = "BROKER_CODE")
    private String brokerCode;
    
    @Column(name = "BROKER_NAME")
    private String brokerName;
    
    @Column(name = "SOURCE_TYPE")
    private String sourceType;
    
    @Column(name = "SOURCE_TYPE_ID")
    private String sourceTypeId;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "UPDATE_DATE")
    private Date updateDate;
    
    @Column(name = "UPDATED_BY")
    private String updatedBy;

}
