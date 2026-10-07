package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(YiSmiDetailId.class)
@Table(name="YI_SMI_DETAIL")
public class YiSmiDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "SERVICE_ID")
    private String serviceId;

    @Column(name = "SERVICE_ACTION")
    private String serviceAction;

    @Id
    @Column(name = "QUOTATION_POLICY_NO", nullable = false)
    private String quotationPolicyNo;

    @Column(name = "SEC_CODE")
    private String secCode;

    @Column(name = "L1S1_ID")
    private Double l1s1Id;

    @Id
    @Column(name = "CVR_ID", nullable = false)
    private Double cvrId;

    @Column(name = "SUM_INSURED")
    private Double sumInsured;

    @Column(name = "ITERATION_NO")
    private String iterationNo;

    @Column(name = "SI_MODIFIED_YN")
    private String siModifiedYn;

    @Column(name = "RATE")
    private Double rate;

    @Column(name = "RATE_MODIFIED_YN")
    private String rateModifiedYn;

    @Column(name = "PREMIUM")
    private Double premium;

    @Column(name = "PREMIUM_MODIFIED_YN")
    private String premiumModifiedYn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQUEST_TIME")
    private Date requestTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RESPONSE_TIME")
    private Date responseTime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "P_WS_RESPONSE_TYPE")
    private String pWsResponseType;

    @Column(name = "P_WS_ERROR")
    private String pWsError;

    @Column(name = "REQUESTREFERENCENO")
    private String requestReferenceNo;

    @Column(name = "PROD_CODE")
    private String prodCode;

    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Id
    @Column(name = "prs_smi_code")
    private Integer prsSmiCode;

    @Id
    @Column(name = "prs_smi_desc")
    private String prsSmiDesc;

    @Column(name = "prs_rate")
    private String prsRate;

    @Column(name = "PRS_RATE_PER")
    private String prsRatePer;

    @Column(name = "PRS_CVR_TYPE")
    private String prsCvrType;

    @Id
    @Column(name = "PRS_SI_FC")
    private String prsSiFc;
    @Id
    @Column(name = "PRS_SI_LC_1")
    private String prsSiLc1;
    @Id
    @Column(name = "PRS_PREM_FC")
    private String prsPremFc;
    @Id
    @Column(name = "PRS_PREM_LC_1")
    private String prsPremLc1;

    @Column(name = "CVR_END_NO_IDX")
    private String cvrEndNoIdx;
}