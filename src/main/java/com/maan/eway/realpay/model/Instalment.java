package com.maan.eway.realpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "instalment")
@Data
@ToString(exclude = { "clientAccount", "contract" })
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Instalment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instalment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_account_id")
    private ClientAccountDetails clientAccount;

    @ManyToOne
    @JoinColumn(name = "contracts_id")
    private Contract contract;

    @Column(name = "quote_no")
    private String quoteNo;

    @Column(name = "payment_id")
    private String paymentId; // new

    @Column(name = "product_id", length = 100)
    private String productId;

    @Column(name = "no_of_instalment")
    private Long noOfInstalment;

    @Column(name = "instalment_action_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date instalmentActionDate;

    @Column(name = "client_number", length = 20)
    @Size(max = 20)
    private String clientNumber;

    @Column(name = "contract_sequence")
    private Integer contractSequence;

    @Column(name = "contract_number", length = 20)
    @Size(max = 20)
    private String contractNumber;

    @Column(name = "tracking_code", length = 10)
    @Size(max = 10)
    private String trackingCode;

    @Column(name = "instalment_amount", precision = 17, scale = 2)
    private BigDecimal instalmentAmount;

    @Column(name = "debit_sequence_type", length = 10)
    private String debitSequenceType;

    @Column(name = "ctc_amount", precision = 17, scale = 2)
    private BigDecimal ctcAmount;

    @Column(name = "instalment_reference_number")
    private String instalmentReferenceNumber;

    @Column(name = "instalment_status")
    private String instalmentStatus;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "last_update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate;

    @Column(name = "sync_status", length = 1)
    @Size(max = 1)
    private String syncStatus;

    @Column(name = "status")
    private String status;

    @Column(name = "message")
    private String message;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "tracking_start_date_time")
    private LocalDateTime trackingStartDateTime;

    @Column(name = "webhook_updated")
    private String webhookUpdated;

    @Column(name = "webhook_updated_date")
    private LocalDateTime webhookUpdatedDate;

    @Column(name = "last_processed_status")
    private String lastProcessedStatus;

    @Column(name = "last_processed_response_code")
    private String lastProcessedResponseCode;

    @Column(name = "last_webhook_processed_date")
    private LocalDateTime lastWebhookProcessedDate;

    @Column(name = "arrear_month")
    private Long arrearMonth;

    @Column(name = "prev_quot_no")
    private String prevQuoteNo;

    @Column(name = "endorsement_reason")
    private String endorsementReason;

    @Column(name = "prev_policy_no")
    private String prevPolicyNo;

    @Column(name="is_paid_cash")
    private String isPaidCash;

    @Column(name="payee_name")
    private String payeeName;

    @Column(name="bank_name")
    private String bankName;

    @Column(name="micr_number")
    private String micrNumber;

    @Column(name="reference_number")
    private String referenceNumber;

    @Column(name="paid_date")
    private String paidDate;

    public String getWebhookUpdated() {
        return webhookUpdated;
    }

    public void setWebhookUpdated(String webhookUpdated) {
        this.webhookUpdated = webhookUpdated;
    }

    public LocalDateTime getWebhookUpdatedDate() {
        return webhookUpdatedDate;
    }

    public void setWebhookUpdatedDate(LocalDateTime webhookUpdatedDate) {
        this.webhookUpdatedDate = webhookUpdatedDate;
    }

    public LocalDateTime getTrackingStartDateTime() {
        return trackingStartDateTime;
    }

    public void setTrackingStartDateTime(LocalDateTime trackingStartDateTime) {
        this.trackingStartDateTime = trackingStartDateTime;
    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        if (this.syncStatus == null) {
            this.syncStatus = "P";
        }

        if (this.instalmentActionDate == null) {
            this.instalmentActionDate = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public boolean belongsToArrearContract() {
        return (this.contract != null && this.contract.isArrearContract()) ||
                (this.contractNumber != null && this.contractNumber.contains("-ARR-")) ||
                (this.instalmentReferenceNumber != null && this.instalmentReferenceNumber.contains("-ARR-")) ||
                (this.message != null && (this.message.contains("ARREAR_RECOVERY") ||
                        this.message.contains("NEW_MANDATE") ||
                        this.message.contains("DISPUTED_RECOVERY")));
    }

    public boolean belongsToOriginalContract() {
        return !belongsToArrearContract();
    }
}
