package com.maan.eway.integration.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PremiaListRequest {
private static final long serialVersionUID = 1L;
@JsonProperty("QuoteNo")
private List<String> quoteNo;

@JsonProperty("PremiaIds")
private List<String> premiaIds; 
}
