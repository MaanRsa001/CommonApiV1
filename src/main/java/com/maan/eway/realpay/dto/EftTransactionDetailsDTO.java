package com.maan.eway.realpay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EftTransactionDetailsDTO {

    private Long id;

    // Mode & Type
    private String modeOfPayment;
    private String paymentType;

    // Customer Info
    private String customerCode;
    private String customerCategory;

    // Insurance References
    private String quoteNo;
    private String policyNo;
    private String policyReference;

    // Payment Info
    private BigDecimal amountPayment;
    private String paymentReference;

    // Bank/EFT/Common Fields
    private String bankCode;
    private String bankAccountNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfPayment;

    // Cheque Specific
    private String chequeNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate chequeDate;

    // Audit Fields
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;

    private String status;

    // Enterprise Add-ons
    private String branchCode;
    private String receiptNumber;
    private String currencyCode;
    private BigDecimal exchangeRate;
    private String approvalStatus;
    private Boolean postedToGl;

    private Long uwSysId;
    private Integer polIdx;
    private Integer installmentNo;
    private String payeeType;
    private String payeeName;
    private String payeeContact;
    private String payeeEmail;
}
