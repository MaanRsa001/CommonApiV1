package com.maan.eway.mtpintegration.entity;
/*
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "MTP_PAYMENT_TRANSACTION")
public class MtpPaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PAYMENT_REQUEST_ID", length = 100)
    private String paymentRequestId;

    @Column(name = "PAYMENT_CHANNEL", length = 100)
    private String paymentChannel;

    @Column(name = "NUMBER_PLATE", length = 50)
    private String numberPlate;

    @Column(name = "MSISDN", length = 30)
    private String msisdn;

    @Column(name = "ASSESSMENT_TYPE", length = 10)
    private String assessmentType;

    @Column(name = "PARTNER_IDENTIFIER", length = 100)
    private String partnerIdentifier;

    @Column(name = "RETURN_CODE")
    private Integer returnCode;

    @Column(name = "RETURN_MESSAGE", length = 500)
    private String returnMessage;

    @Column(name = "AMOUNT", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentRequestId() {
		return paymentRequestId;
	}

	public void setPaymentRequestId(String paymentRequestId) {
		this.paymentRequestId = paymentRequestId;
	}

	public String getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(String paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public String getNumberPlate() {
		return numberPlate;
	}

	public void setNumberPlate(String numberPlate) {
		this.numberPlate = numberPlate;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getAssessmentType() {
		return assessmentType;
	}

	public void setAssessmentType(String assessmentType) {
		this.assessmentType = assessmentType;
	}

	public String getPartnerIdentifier() {
		return partnerIdentifier;
	}

	public void setPartnerIdentifier(String partnerIdentifier) {
		this.partnerIdentifier = partnerIdentifier;
	}

	public Integer getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(Integer returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

    // Generate getters/setters in STS
}*/


import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "MTP_PAYMENT_TRANSACTION")
public class MtpPaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PAYMENT_REQUEST_ID", length = 100)
    private String paymentRequestId;

    @Column(name = "PAYMENT_CHANNEL", length = 100)
    private String paymentChannel;

    @Column(name = "NUMBER_PLATE", length = 50)
    private String numberPlate;

    @Column(name = "MSISDN", length = 30)
    private String msisdn;

    @Column(name = "ASSESSMENT_TYPE", length = 10)
    private String assessmentType;

    @Column(name = "PARTNER_IDENTIFIER", length = 100)
    private String partnerIdentifier;

    @Column(name = "RETURN_CODE")
    private Integer returnCode;

    @Column(name = "RETURN_MESSAGE", length = 1000)
    private String returnMessage;

    @Column(name = "AMOUNT", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "STICKER_REFERENCE", length = 100)
    private String stickerReference;

    @Column(name = "TRANSACTION_ID", length = 100)
    private String transactionId;

    @Column(name = "DOWNLOAD_LINK", length = 1000)
    private String downloadLink;

    @Lob
    @Column(name = "INIT_REQUEST_JSON")
    private String initRequestJson;

    @Lob
    @Column(name = "INIT_RESPONSE_JSON")
    private String initResponseJson;

    @Lob
    @Column(name = "STATUS_REQUEST_JSON")
    private String statusRequestJson;

    @Lob
    @Column(name = "STATUS_RESPONSE_JSON")
    private String statusResponseJson;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentRequestId() { return paymentRequestId; }
    public void setPaymentRequestId(String paymentRequestId) { this.paymentRequestId = paymentRequestId; }

    public String getPaymentChannel() { return paymentChannel; }
    public void setPaymentChannel(String paymentChannel) { this.paymentChannel = paymentChannel; }

    public String getNumberPlate() { return numberPlate; }
    public void setNumberPlate(String numberPlate) { this.numberPlate = numberPlate; }

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }

    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }

    public String getPartnerIdentifier() { return partnerIdentifier; }
    public void setPartnerIdentifier(String partnerIdentifier) { this.partnerIdentifier = partnerIdentifier; }

    public Integer getReturnCode() { return returnCode; }
    public void setReturnCode(Integer returnCode) { this.returnCode = returnCode; }

    public String getReturnMessage() { return returnMessage; }
    public void setReturnMessage(String returnMessage) { this.returnMessage = returnMessage; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStickerReference() { return stickerReference; }
    public void setStickerReference(String stickerReference) { this.stickerReference = stickerReference; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getDownloadLink() { return downloadLink; }
    public void setDownloadLink(String downloadLink) { this.downloadLink = downloadLink; }

    public String getInitRequestJson() { return initRequestJson; }
    public void setInitRequestJson(String initRequestJson) { this.initRequestJson = initRequestJson; }

    public String getInitResponseJson() { return initResponseJson; }
    public void setInitResponseJson(String initResponseJson) { this.initResponseJson = initResponseJson; }

    public String getStatusRequestJson() { return statusRequestJson; }
    public void setStatusRequestJson(String statusRequestJson) { this.statusRequestJson = statusRequestJson; }

    public String getStatusResponseJson() { return statusResponseJson; }
    public void setStatusResponseJson(String statusResponseJson) { this.statusResponseJson = statusResponseJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

