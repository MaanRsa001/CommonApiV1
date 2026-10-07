package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerGetReq {

	@JsonProperty("CustomerId")
	private String customerId;
	
/*
	@JsonProperty("GstNo")
	private String gstNo; */
}
