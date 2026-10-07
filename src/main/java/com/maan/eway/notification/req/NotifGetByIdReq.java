package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NotifGetByIdReq {

	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	
	@JsonProperty("ProductId")
	private String  productId ;

	@JsonProperty("NotificationNo")
	private String notificationNo;
	
}
