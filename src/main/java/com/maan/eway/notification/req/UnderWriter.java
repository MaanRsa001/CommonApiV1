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
public class UnderWriter{
	@JsonProperty("Uwname")
    private String     uwName       ;
	@JsonProperty("Uwmailid")
    private String     uwMailid     ;
	@JsonProperty("Uwphonecode")
    private Integer    uwPhonecode  ;
	@JsonProperty("Uwphoneno")
    private BigDecimal uwPhoneNo    ;
	@JsonProperty("Uwmessengercode")
    private Integer    uwMessengerCode ;
	@JsonProperty("Uwmessengerphone")
    private BigDecimal uwMessengerPhone ;
	
	@JsonProperty("UwLoginId")
    private String     uwLoginId;
	@JsonProperty("UwuserType")
    private String     uwuserType;
	@JsonProperty("UwsubuserType")
    private String     uwsubuserType;

}