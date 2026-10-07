package com.maan.eway.excess.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExcessTransactionReq {


    @JsonProperty("RequestReferenceNo")
    private String requestReferenceNo;
    
    @JsonProperty("ExcessDetails")
    private List<ExcessDetailsReq> excessDetails;
}

