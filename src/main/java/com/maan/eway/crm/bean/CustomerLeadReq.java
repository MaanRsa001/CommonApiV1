package com.maan.eway.crm.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerLeadReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("InsuranceId")
    private String     companyId;
    
    @JsonProperty("LeadSeqNo")
    private Long leadSeqNo;
}
