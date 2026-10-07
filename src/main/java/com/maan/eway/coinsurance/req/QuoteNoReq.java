package com.maan.eway.coinsurance.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class QuoteNoReq {

	@JsonProperty("QuoteNo")
	   @NotBlank(message = "QuoteNo is Empty")
	    private String quoteNo;
}
