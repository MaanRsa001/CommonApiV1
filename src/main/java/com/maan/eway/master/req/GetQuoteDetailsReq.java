package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetQuoteDetailsReq {

	@JsonProperty("CustomerId")
	private String customerId ;
	
	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("CreatedBy")
	private String createdBy ;

}
