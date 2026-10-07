package com.maan.eway.bean;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "eway_payment_process_detail")
@IdClass(PaymentProcessDetailId.class)
public class PaymentProcessDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "QUOTE_NO",nullable = false)
	private String quoteNo;

	@Id
	@Column(name = "PAYMENT_ID",nullable = false)
	private String paymentId;
	
	@Column(name = "PAYMENT_TYPE",nullable = false)
	private String paymentType;
	
	@Column(name = "POLICY_NO")
	private String policyNo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "CREDIT_CONTROLLER_STATUS")
	private String creditControllerStatus;
	
	@Column(name = "SURVEYOR_CODE")
	private String surveyorCode;
	
	@Column(name = "SURVEYOR_STATUS")
	private String surveyorStatus;
	
	@Column(name = "UNDERWRITTER_CODE")
	private String underWritterCode;
	
	@Column(name = "UNDERWRITTER_STATUS")
	private String underWritterStatus;
	
	@Column(name = "UW_REMARKS")
	private String uwRemarks;
	
	@Column(name = "SS_REMARKS")
	private String ssRemarks;
	
	@Column(name = "CC_REMARKS")
	private String ccRemarks;
	
	@Column(name = "COMPANY_ID")
	private String companyId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CC_UPDATED_DATE")
	private Date ccUpdatedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SURVEYOR_UPDATED_DATE")
	private Date surveyorUpdatedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UNDERWRITTER_UPDATED_DATE")
	private Date underwritterUpdatedDate;

	@Column(name = "TYPE")
	private String type;
	
}
