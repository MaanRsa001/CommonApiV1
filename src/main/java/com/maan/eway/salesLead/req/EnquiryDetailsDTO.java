package com.maan.eway.salesLead.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryDetailsDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @JsonProperty("EnquiryId")
    private String enquiryId;
    
    @JsonProperty("LeadId")
    private String leadId;
    
    @JsonProperty("EnquiryDescription")
    private String enquiryDescription;
    
    @JsonProperty("ClientName")
    private String clientName;
    
    @JsonProperty("ClientCodeDesc")
    private String ClientCodeDesc;
    
    @JsonProperty("LobId")
    private String lobId;
    
    @JsonProperty("LobDesc")
    private String lobDesc;
    
    @JsonProperty("ProductId")
    private String productId;
    
    @JsonProperty("SumInsured")
    private Double sumInsured;
    
    @JsonProperty("SuggestPremium")
    private Double suggestPremium;
    
    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("RejectedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date rejectedDate;
    
    @JsonProperty("EntryDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date entryDate;
    
    @JsonProperty("RejectedReason")
    private String rejectedReason;
    
    @JsonProperty("Status")
    private String status;
    
    @JsonProperty("StatusDesc")
    private String statusDesc;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("ReceiptOfenquiry")
    private String receiptOfenquiry;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("ExceptedDateCommBussiness")
    private String exceptedDateCommBussiness;
    
    @JsonProperty("UnderWritters")
    private String underWritters;
    
    @JsonProperty("SalesRemarks")
    private String salesRemarks;
    
    @JsonProperty("UWRemarks")
    private String uwRemarks;
    
    @JsonProperty("BusniessType")
    private String busniessType;
    
    @JsonProperty("QuotesCount")
    private String quotesCount;
    
}