package com.maan.eway.realpay.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "eft_transaction_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EftTransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mode & Type
    @Column(name = "mode_of_payment", length = 20, nullable = false)
    private String modeOfPayment;

    @Column(name = "payment_type", length = 20, nullable = false)
    private String paymentType;

    // Customer Info
    @Column(name = "customer_code", length = 50, nullable = false)
    private String customerCode;

    @Column(name = "customer_category", length = 20, nullable = false)
    private String customerCategory;

    // Insurance References
    @Column(name = "quote_no", length = 100)
    private String quoteNo;

    @Column(name = "policy_no", length = 100)
    private String policyNo;

    @Column(name = "policy_reference", length = 100)
    private String policyReference;

    // Payment Info
    @Column(name = "amount_payment", precision = 18, scale = 2, nullable = false)
    private BigDecimal amountPayment;

    @Column(name = "payment_reference", length = 100, nullable = false)
    private String paymentReference;

    // Bank/EFT/Common Fields
    @Column(name = "bank_code", length = 20)
    private String bankCode;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "date_of_payment")
    private LocalDate dateOfPayment;

    // Cheque Specific
    @Column(name = "cheque_no", length = 50)
    private String chequeNo;

    @Column(name = "cheque_date")
    private LocalDate chequeDate;

    // Audit Fields
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "status", length = 20)
    private String status;

    // Enterprise Add-ons
    @Column(name = "branch_code", length = 20)
    private String branchCode;

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @Column(name = "currency_code", length = 10)
    private String currencyCode;

    @Column(name = "exchange_rate", precision = 18, scale = 6)
    private BigDecimal exchangeRate;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus;

    @Column(name = "posted_to_gl")
    private Boolean postedToGl;

    @Column(name = "uw_sys_id", nullable = false)
    private Long uwSysId;

    @Column(name = "pol_idx", nullable = false)
    private Integer polIdx;

    @Column(name = "installment_no", nullable = false)
    private Integer installmentNo;

    @Column(name = "payee_type", length = 100)
    private String payeeType;

    @Column(name = "payee_name", length = 255)
    private String payeeName;

    @Column(name = "payee_contact", length = 255)
    private String payeeContact;

    @Column(name = "payee_email", length = 255)
    private String payeeEmail;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
        if (this.approvalStatus == null) {
            this.approvalStatus = "PENDING";
        }
        if (this.postedToGl == null) {
            this.postedToGl = false;
        }
        if (this.currencyCode == null) {
            this.currencyCode = "INR";
        }
        if (this.exchangeRate == null) {
            this.exchangeRate = BigDecimal.ONE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
