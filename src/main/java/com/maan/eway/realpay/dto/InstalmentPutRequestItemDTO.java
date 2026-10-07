package com.maan.eway.realpay.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstalmentPutRequestItemDTO {

    @JsonProperty("ClientNumber")
    private String clientNumber;

    @JsonProperty("ContractSequence")
    private Long contractSequence;

    @JsonProperty("ContractNumber")
    private String contractNumber;

    @JsonProperty("InstalmentSequence")
    private Long instalmentSequence;

    @JsonProperty("InstalmentActionDate")
    private String instalmentActionDate;

    @JsonProperty("TrackingCode")
    private String trackingCode;

    @JsonProperty("InstalmentAmount")
    private BigDecimal instalmentAmount;

    @JsonProperty("InstalmentStatus")
    private String instalmentStatus;

    @JsonProperty("DebitSequenceType")
    private String debitSequenceType;

    public InstalmentPutRequestItemDTO constructInstallmentUpdateReq(String clientNumber,Long contractSequence,String contractNumber,
                                                                     Long instalmentSequence, String instalmentActionDate, String trackingCode,
                                                                     BigDecimal instalmentAmount, String instalmentStatus, String debitSequenceType){
        return InstalmentPutRequestItemDTO.builder()
                .clientNumber(clientNumber).contractSequence(contractSequence).contractNumber(contractNumber)
                .instalmentSequence(instalmentSequence).instalmentActionDate(instalmentActionDate).trackingCode(trackingCode)
                .instalmentAmount(instalmentAmount).instalmentStatus(instalmentStatus).debitSequenceType(debitSequenceType)
                .build();
    }
}
