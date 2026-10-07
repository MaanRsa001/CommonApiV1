package com.maan.eway.realpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contracts_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_account_id")
    private ClientAccountDetails clientAccount;

    @Column(name = "client_number", length = 20)
    @Size(max = 20)
    private String clientNumber;

    @Column(name = "contract_number", length = 20)
    @Size(max = 20)
    @NotNull
    private String contractNumber;

    @Column(name = "contract_type", length = 20)
    private String contractType;

    @Column(name = "original_contract_number", length = 20)
    private String originalContractNumber;

    @Column(name = "mandate_type", length = 20)
    private String mandateType;

    @Column(name = "contract_sequence")
    private String contractSequence;

    @Column(name = "quote_no")
    private String quoteNo;

    @Column(name = "payment_id")
    private String paymentId; // new

    @Column(name = "frequency_code", length = 4)
    @Size(max = 4)
    private String frequencyCode;

    @Column(name = "collection_day")
    private Integer collectionDay;

    @Column(name = "tracking_code", length = 10)
    @Size(max = 10)
    private String trackingCode;

    @Column(name = "first_collection_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstCollectionDate;

    @Column(name = "first_collection_amount", precision = 17, scale = 2)
    private BigDecimal firstCollectionAmount;

    @Column(name = "instalment_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date instalmentStartDate;

    @Column(name = "instalment_amount", precision = 17, scale = 2)
    private BigDecimal instalmentAmount;

    @Column(name = "debit_sequence_type")
    private String debitSequenceType;

    @Column(name = "number_of_instalments")
    private Integer numberOfInstalments;

    @Column(name = "ctc_percentage")
    private Integer ctcPercentage;

    private String status;
    private String message;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Instalment> instalments;

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
        if (this.contractType == null) {
            this.contractType = "ORIGINAL";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public boolean isArrearContract() {
        return "ARREAR".equalsIgnoreCase(this.contractType) ||
                this.originalContractNumber != null ||
                (this.contractNumber != null && this.contractNumber.contains("-ARR-"));
    }

    public boolean isOriginalContract() {
        return !isArrearContract();
    }
}
