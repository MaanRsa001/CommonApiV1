package com.maan.eway.salesLead.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "quote_information_ipclms")
@IdClass(QuoteInformationId.class)
@Builder
public class QuoteInformationIpclms {

    @Id
    @Column(name="ENQUIRY_ID")
    private String enquiryId;

    @Id
    @Column(name="QUOTE_NO")
    private String quoteNo;
    
    @Id
    @Column(name="AMEND_ID")
    private Integer amendId;

    @Column(name="QUOTATION_DESCRIPTION")
    private String quotationDescription;
    
    @Column(name="SUM_INSURED")
    private Double sumInsured;
    
    @Column(name="PREMIUM_RATE")
    private Double premiumRate;
    
    @Column(name="PREMIUM_AMOUNT")
    private Double premiumAmount;
    
    @Column(name="TECHNICAL_DISCOUNT")
    private Double technicalDiscount;
    
    @Column(name="ADDITIONAL_DISCOUNT")
    private Double additionalDiscount;
    
    @Column(name="QUOTE_STATUS")
    private String quoteStatus;
    
    @Column(name="QUOTE_STATUS_DESC")
    private String quoteStatusDesc;
    
    @Column(name="QUOTE_REMARKS")
    private String quoteRemarks;
    
    @Column(name="SALES_REMARKS")
    private String salesRemarks;
    
    @Column(name="UW_REMARKS")
    private String uwRemarks;
    
    @Column(name="LOGIN_ID")
    private String loginId;
    
    @Column(name="LEAD_ID")
    private String leadId;
    
    @Column(name = "ENQUIRY_CREATED_BY")
    private String enquiryStringCreatedBy;
}