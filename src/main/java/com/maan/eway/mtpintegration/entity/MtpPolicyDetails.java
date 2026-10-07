package com.maan.eway.mtpintegration.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "mtp_policy_details")
public class MtpPolicyDetails {

	@Id
	@Column(name = "REGNO", length = 50)
	private String regno;

	@Column(name = "RETURN_CODE")
	private Integer returnCode;

	@Column(name = "RETURN_MESSAGE")
	private String returnMessage;

	@Column(name = "POLICY_NUMBER")
	private String policyNumber;

	@Column(name = "POLICY_STATUS")
	private String policyStatus;

	@Column(name = "POLICY_HOLDER_NAME")
	private String policyHolderName;

	@Column(name = "INSURANCE_COMPANY_NAME")
	private String insuranceCompanyName;

	@Column(name = "AGGREGATOR_TRANSACTION_ID")
	private String aggregatorTransactionId;

	@Column(name = "AGENT_NAME")
	private String agentName;

	@Column(name = "AGENT_PHONE")
	private String agentPhone;

	@Column(name = "PAYER_NAME")
	private String payerName;

	@Column(name = "PAYER_MOBILE")
	private String payerMobile;

	@Column(name = "PAYMENT_REFERENCE")
	private String paymentReference;

	@Column(name = "ASSESSMENT_TYPE")
	private String assessmentType;

	@Column(name = "STICKER_TYPE")
	private String stickerType;

	@Column(name = "COVER_DESCRIPTION")
	private String coverDescription;

	@Temporal(TemporalType.DATE)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "END_DATE")
	private Date endDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_CREATED")
	private Date dateCreated;

	@Temporal(TemporalType.DATE)
	@Column(name = "PAYMENT_RECEIVED_DATE")
	private Date paymentReceivedDate;

	@Column(name = "ASSESSED_VAT")
	private Double assessedVat;

	@Column(name = "ASSESSED_TRAINING_LEVY")
	private Double assessedTrainingLevy;

	@Column(name = "ASSESSED_PREMIUM")
	private Double assessedPremium;

	@Column(name = "ASSESSED_STAMP_DUTY")
	private Double assessedStampDuty;

	@Column(name = "ASSESSED_STICKER_FEES")
	private Double assessedStickerFees;

	@Column(name = "TOTAL_ASSESSMENT_AMOUNT")
	private Double totalAssessmentAmount;

	@Column(name = "AMOUNT")
	private Double amount;

	@Column(name = "SHORT_TERM_DURATION")
	private Integer shortTermDuration;

	@Column(name = "RUNNING")
	private String running;

	@Column(name = "EXPIRED")
	private String expired;

	@Column(name = "SHORT_TERM")
	private String shortTerm;

	@Column(name = "PRORATED")
	private String prorated;

	@Column(name = "FUTURE")
	private String future;

	@Lob
	@Column(name = "REQUEST_JSON", columnDefinition = "LONGTEXT")
	private String requestJson;

	@Lob
	@Column(name = "RESPONSE_JSON", columnDefinition = "LONGTEXT")
	private String responseJson;

	@Temporal(TemporalType.DATE)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "CHASSIS_NUMBER", length = 100)
	private String chassisNumber;

	@Column(name = "ENGINE_NUMBER", length = 100)
	private String engineNumber;

	@Column(name = "SEATING_CAPACITY")
	private Integer seatingCapacity;

	@Column(name = "STICKER_VEHICLE_TYPE", length = 100)
	private String stickerVehicleType;
}
