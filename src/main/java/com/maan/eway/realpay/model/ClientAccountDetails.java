package com.maan.eway.realpay.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "client_account_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "contracts", "instalments" })
public class ClientAccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_account_id")
    private Long id;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "quote_no")
    private String quoteNo;

    @Column(name = "merchant_reference")
    private String merchantReference;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_type_desc")
    private String paymentTypeDesc;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "client_number", length = 20)
    @Size(max = 20)
    private String clientNumber;

    @Column(name = "client_name", length = 35)
    @Size(max = 35)
    private String clientName;

    @Column(name = "id_type", length = 1)
    @Size(max = 1)
    private String idType;

    @Column(name = "id_number", length = 33)
    @Size(max = 33)
    private String idNumber;

    @Column(name = "cellphone_number", length = 12)
    @Size(max = 12)
    private String cellphoneNumber;

    @Column(length = 90)
    @Size(max = 90)
    private String email;

    @Column(name = "bank_code")
    private Integer bankCode;

    @Column(name = "branch_code")
    private Integer branchCode;

    @Column(name = "account_type")
    private Integer accountType;

    @Column(name = "account_number")
    private Long accountNumber;

    @Column(name = "account_holder_name", length = 50)
    @Size(max = 50)
    private String accountHolderName;

    @Column(name = "employee_group_code", length = 10)
    private String employeeGroupCode;

    @Column(name = "beneficiary_user", length = 50)
    private String beneficiaryUser;

    @Column(name = "pop_ind", length = 1)
    @Size(max = 1)
    private String popInd;

    @Column(name = "po_bank_code")
    private Integer poBankCode;

    @Column(name = "po_branch_code")
    private Integer poBranchCode;

    @Column(name = "po_account_holder_name", length = 50)
    @Size(max = 50)
    private String poAccountHolderName;

    @Column(name = "po_account_number")
    private Long poAccountNumber;

    @Column(name = "po_account_type")
    private Integer poAccountType;

    @Column(name = "card_number", length = 16)
    @Size(max = 16)
    private String cardNumber;

    @Column(name = "card_expiry")
    private Integer cardExpiry;

    @Column(name = "payment_preference")
    private String paymentPreference;

    @Column(name = "tracking_code", length = 10)
    private String trackingCode;

    @Column(name = "debit_sequence_type")
    private String debitSequenceType;

    @Column(name = "frequency_code")
    private String frequencyCode;

    // @Column(name = "collection_day")
    // private Integer collectionDay;

    @OneToMany(mappedBy = "clientAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contract> contracts;

    @OneToMany(mappedBy = "clientAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Instalment> instalments;

    private String status;
    private String message;

    @Column(name = "client_reference")
    private String clientReference;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "prev_quot_no")
    private String prevQuoteNo;

    @Column(name = "endorsement_reason")
    private String endorsementReason;

    @Column(name = "prev_policy_no")
    private String prevPolicyNo;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
