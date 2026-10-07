package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerDtailsforOman {

	@JsonProperty("AgentName")
	private String brokerName;
	
	@JsonProperty("CRNo")
	private String cRNo;
	
	@JsonProperty("VATorTaxNo")
	private String vATorTaxNo;
	
	@JsonProperty("MobileNoBroker")
	private String mobileNoBroker;
	
	@JsonProperty("EmailAddres")
	private String emailAddres;
	
	
	
}
