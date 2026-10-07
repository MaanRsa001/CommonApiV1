package com.maan.eway.coinsurance.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "co_insurance_header")
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@Builder
@ToString
public class CoInsuranceHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "QUOTE_NO", nullable = false, length = 100)
    private String quoteNo;

    @Column(name = "COMPANY_ID")
    private Integer companyId;

    @Column(name = "PRODUCT_ID")
    private Integer productId;

    @Column(name = "PRODUCT_DESC", length = 100)
    private String productDesc;

    @Column(name = "CUSTOMER_NAME", length = 100)
    private String customerName;

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

    @Column(name = "TOTAL_PREMIUM_LC")
    private BigDecimal totalPremiumLc;

    @Column(name = "TOTAL_PREMIUM_FC")
    private BigDecimal totalPremiumFc;

    @Column(name = "TAX_AMOUNT_LC")
    private BigDecimal taxAmountLc;

    @Column(name = "TAX_AMOUNT_FC")
    private BigDecimal taxAmountFc;

    @Column(name = "CURRENCY_ID", length = 20)
    private String currencyId;

    @Column(name = "ENTRY_DATE")
    @Temporal(TemporalType.DATE)
    private Date entryDate;

    @Column(name = "STATUS", length = 10)
    private String status;
    
    @Column(name = "EXCHANGE_RATE")
    private BigDecimal exchangeRate;

}

