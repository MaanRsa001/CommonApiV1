package com.maan.eway.realpay.dto;

import lombok.Data;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
public class ClientAccountDetailsDTO {

    private Long id;

    private String paymentId;

    @NotBlank(message = "Quote number is required")
    private String quoteNo;

    private String merchantReference;
    private String paymentType;
    private String paymentTypeDesc;

    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotBlank(message = "Client number is required")
    @Size(max = 20, message = "Client number must not exceed 20 characters")
    private String clientNumber;

    @NotBlank(message = "Client name is required")
    @Size(max = 35, message = "Client name must not exceed 35 characters")
    private String clientName;

    @NotBlank(message = "ID type is required")
    @Size(max = 1, message = "Id type must not exceed 1 character")
    private String idType;

    @NotBlank(message = "ID number is required")
    @Size(max = 33, message = "Id number must not exceed 33 characters")
    private String idNumber;

    @Size(max = 12, message = "Cellphone number must not exceed 12 characters")
    private String cellphoneNumber;

    @Email(message = "Email should be valid")
    @Size(max = 90, message = "Email must not exceed 90 characters")
    private String email;

    @NotNull(message = "Bank code cannot be null")
    @Min(value = 1, message = "Bank code must be a positive number")
    private Integer bankCode;

    @NotNull(message = "Branch code cannot be null")
    @Min(value = 1, message = "Branch code must be a positive number")
    private Integer branchCode;

    @NotNull(message = "Account type cannot be null")
    @Min(value = 1, message = "Account type must be a positive number")
    private Integer accountType;

    @NotNull(message = "Account number cannot be null")
    @Min(value = 1, message = "Account number must be a positive number")
    private Long accountNumber;

    @NotBlank(message = "Account holder name is required")
    @Size(max = 50, message = "Account Holder Name must not exceed 50 characters")
    private String accountHolderName;

    @Size(max = 10)
    private String employeeGroupCode;

    private String beneficiaryUser;

    @Size(max = 1, message = "PopInd must not exceed 1 character")
    private String popInd;

    @Min(value = 0, message = "Po Bank Code must be a positive number")
    private Integer poBankCode;

    @Min(value = 0, message = "PO Branch Code must be a positive number")
    private Integer poBranchCode;

    @Size(max = 50, message = "PO Account Holder Name must not exceed 50 characters")
    private String poAccountHolderName;

    @Min(value = 0, message = "Po Account Number must be a positive number")
    private Long poAccountNumber;

    @Min(value = 0, message = "Po Account Type must be a positive number")
    private Integer poAccountType;

    @Size(max = 16, message = "Card Number must not exceed 16 characters")
    private String cardNumber;

    @Min(value = 0, message = "Card Expiry must be a positive number")
    private Integer cardExpiry;

    @Column(name = "payment_preference")
    private String paymentPreference; // "EFT" or "DOUBLE_DEBIT_ORDER_ARREAR"

    private String status;
    private String message;

    @Column(name = "client_reference")
    private String clientReference;

    private String debitSequenceType;
    private String frequencyCode;
    private Integer collectionDay;
    @Size(max = 10)
    private String trackingCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;

    private String requestBody;
    private String responseBody;

    private String isFirstInstalmentPaid;
    private String payeeName;
    private String bankName;
    private String micrNumber;
    private String referenceNumber;
    private String paidDate;
}
