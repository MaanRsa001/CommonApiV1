package com.maan.eway.viewAll.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OmanJasperDto {
	
	@JsonProperty("PolicyDetails")
	private PolicyDetailsForOman  polcy;
	
	@JsonProperty("SponsorDetails")
	private SponsorDetailsFroOman customer;
	
	@JsonProperty("AgentDtails")
	private  BrokerDtailsforOman broker;

	@JsonProperty("WorkerDtails")
	private  WorkerDtails woker;
	
	@JsonProperty("Premium")
	private  PremiumDto premium;
}
