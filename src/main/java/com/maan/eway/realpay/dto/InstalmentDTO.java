package com.maan.eway.realpay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstalmentDTO {
    private Long id;

    private Long clientAccountId;

    private Long contractId;

    @Size(max = 100, message = "Quote number must not exceed 100 characters")
    private String quoteNo;

    @Size(max = 100, message = "Product ID must not exceed 100 characters")
    private String productId;

    private Long noOfInstalment;

    @NotNull(message = "Instalment action date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date instalmentActionDate; 

    @Size(max = 20, message = "Client number must not exceed 20 characters")
    private String clientNumber;

    @NotNull(message = "Contract sequence is required")
    private Integer contractSequence;

    @Size(max = 20, message = "Contract number must not exceed 20 characters")
    private String contractNumber;

    @Size(max = 4, message = "Tracking Code must not exceed 4 characters")
    private String trackingCode;

    @NotNull(message = "Instalment amount is required")
    @Digits(integer = 15, fraction = 2, message = "Instalment Amount must not exceed 15 integer and 2 fraction digits")
    private BigDecimal instalmentAmount;

    @Size(max = 4, message = "Debit Sequence Type must not exceed 4 characters")
    private String debitSequenceType;

    @NotNull(message = "CTC Amount is required")
    @Digits(integer = 15, fraction = 2, message = "CTC Amount must not exceed 15 integer and 2 fraction digits")
    private BigDecimal ctcAmount;
    
    private String instalmentReferenceNumber;
    private String instalmentStatus;
    
    private String responseCode;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;
    
    private String status;
    private String message;

    private String syncStatus;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    
    private String requestBody;
    private String responseBody;
    
    private String webhookUpdated; // "Y" or "N"

    private LocalDateTime webhookUpdatedDate;
    
    public boolean belongsToArrearContract() {
        return (this.contractNumber != null && this.contractNumber.contains("-ARR-")) ||
               (this.instalmentReferenceNumber != null && this.instalmentReferenceNumber.contains("-ARR-")) ||
               (this.message != null && (this.message.contains("ARREAR_RECOVERY") || 
                                        this.message.contains("NEW_MANDATE") ||
                                        this.message.contains("DISPUTED_RECOVERY")));
    }
    
    public boolean belongsToOriginalContract() {
        return !belongsToArrearContract();
    }
}
