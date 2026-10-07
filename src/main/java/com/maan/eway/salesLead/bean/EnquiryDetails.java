package com.maan.eway.salesLead.bean;

import java.io.Serializable;
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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@DynamicUpdate
@Table(name = "eway_enquiry_details")
@Data
@IdClass(EnquiryDetailsId.class)
public class EnquiryDetails implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENQUIRY_ID", length = 20, nullable = false)
	private String enquiryId;
	
	@Id
	@Column(name ="LEAD_ID", nullable = false)
	private String leadId;
	
	@Id
	@Column(name="AMEND_ID", nullable = false)
	private Integer amendId;
	
	@Column(name ="ENQUIRY_DESCRIPTION")
	private String enquiryDescription;
	
	@Column(name ="LOB_ID")
	private String lobId;
	
	@Column(name ="PRODUCT_ID")
	private String productId;
	
	@Column(name ="SUM_INSURED")
	private Double sumInsured;
	
	@Column(name ="SUGGEST_PREMIUM")
	private Double suggestPremium;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "SALES_REMARKS")
	private String salesRemarks;
	
	@Column(name = "UW_REMARKS")
	private String uwRemarks;
	
	@Column(name ="BUSINESS_TYPE")
	private String businessType;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REJECTED_DATE")
	private Date rejectedDate;
	
	@Column(name = "REJECTED_REASON")
	private String rejectedReason;
	
	@Column(name ="STATUS")
	private String status;
	
	@Column(name ="STATUS_DESC")
	private String statusDesc;
	
	@Column(name ="RECEIPT_OF_ENQUIRY")
	private String receiptOfenquiry;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name ="EXCEPTED_DATE_COMM_BUSSINESS")
	private Date exceptedDateCommBussiness;
	
	@Column(name ="UNDER_WRITTERS")
	private String underwritters;
	
	
	
}
