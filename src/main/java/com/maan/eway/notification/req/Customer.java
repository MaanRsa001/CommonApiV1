package com.maan.eway.notification.req;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer{
	@JsonProperty("Customername")
    private String     customerName ;
	@JsonProperty("Customermailid")
    private String     customerMailid ;
	@JsonProperty("Customerphoneno")
    private BigDecimal customerPhoneNo ;
	@JsonProperty("Customerphonecode")
    private Integer    customerPhoneCode ;
	@JsonProperty("Customermessengercode")
    private Integer    customerMessengerCode ;
	@JsonProperty("Customermessengerphone")
    private BigDecimal customerMessengerPhone ;
	@JsonProperty("CustomerRefno")
    private String     customerRefno ;

}