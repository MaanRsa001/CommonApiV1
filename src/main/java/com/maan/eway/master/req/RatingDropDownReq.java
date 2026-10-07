package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RatingDropDownReq {

	@JsonProperty("ProductId")
	private String productId ;
}
