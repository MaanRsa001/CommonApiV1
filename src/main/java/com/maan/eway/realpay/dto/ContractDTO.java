package com.maan.eway.realpay.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDTO {
    private Long id;

    private Long clientAccountId;

    @Size(max = 20, message = "Client number must not exceed 20 characters")
    private String clientNumber;

    @Size(max = 20, message = "Contract number must not exceed 20 characters")
    private String contractNumber;
    
    @Size(max = 20, message = "Contract type must not exceed 20 characters")
    private String contractType; 
    
    @Size(max = 20, message = "Original contract number must not exceed 20 characters")
    private String originalContractNumber; 
    
    @Size(max = 20, message = "Mandate type must not exceed 20 characters")
    private String mandateType; 
    
    private String quoteNo;
    
    private String contractSequence;

    @Size(max = 4, message = "Frequency Code must not exceed 4 characters")
    private String frequencyCode;

    @Digits(integer = 2, fraction = 0, message = "Collection Day must not exceed 2 digits")
    private Integer collectionDay;

    @Size(max = 4, message = "Tracking Code must not exceed 4 characters")
    private String trackingCode;
    
    @Size(max = 4, message = "Debit Sequence Type must not exceed 4 characters")
    private String debitSequenceType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstCollectionDate;

    @Digits(integer = 15, fraction = 2, message = "First Collection Amount must not exceed 15 integer and 2 fraction digits")
    private BigDecimal firstCollectionAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date instalmentStartDate;

    @Digits(integer = 15, fraction = 2, message = "Instalment Amount must not exceed 15 integer and 2 fraction digits")
    private BigDecimal instalmentAmount;

    @Digits(integer = 4, fraction = 0, message = "Number Of Instalments must not exceed 4 digits")
    private Integer numberOfInstalments;

    @Digits(integer = 3, fraction = 0, message = "CTC Percentage must not exceed 3 digits")
    private Integer ctcPercentage;
    
    private String status;
    private String message;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    private String requestBody;
    private String responseBody;
    
    public boolean isArrearContract() {
        return "ARREAR".equalsIgnoreCase(this.contractType) || 
               this.originalContractNumber != null ||
               (this.contractNumber != null && this.contractNumber.contains("-ARR-"));
    }
    
    public boolean isOriginalContract() {
        return !isArrearContract();
    }
}
