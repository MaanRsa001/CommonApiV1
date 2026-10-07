package com.maan.eway.excess.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExcessTransactionRes {

    @JsonProperty("ExcessId")
    private Integer excessId;

    @JsonProperty("RequestReferenceNo")
    private String requestReferenceNo;

    @JsonProperty("ProductId")
    private String productId;

    @JsonProperty("SectionId")
    private String sectionId;

    @JsonProperty("CoverId")
    private String coverId;

    @JsonProperty("ExcessPercentage")
    private Integer excessPercentage;

    @JsonProperty("ExcessAmount")
    private Double excessAmount;

    @JsonProperty("ExcessDescription")
    private String excessDescription;

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("EntryDate")
    private Date entryDate;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("RegulatoryCode")
    private String regulatoryCode;

    @JsonProperty("CoreAppCode")
    private String coreAppCode;

    @JsonProperty("BranchCode")
    private String branchCode;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("CoverName")
    private String coverName;
    
    @JsonProperty("LocationId")
    private String locadationId;
    
    @JsonProperty("VehicleId")
    private String riskId;
}

