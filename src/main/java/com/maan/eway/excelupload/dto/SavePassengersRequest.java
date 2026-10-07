package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

    @Data
    public class SavePassengersRequest {
    	
        @JsonProperty("QuoteNo")
        private String quoteNo;

        @JsonProperty("CreatedBy")
        private String createdBy;

        @JsonProperty("LocationId")
        private String locationId;

        @JsonProperty("PassengerList")
        private List<PassengerDto> passengerList;
    }
