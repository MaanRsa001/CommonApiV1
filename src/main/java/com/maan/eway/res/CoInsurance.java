package com.maan.eway.res;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class CoInsurance {
	
	
	
	
	
	@JsonProperty("Sno")
	private int sno ;
	

	
	@JsonProperty("Insurancecompanyid")
	private int insurancecompanyid ;
	
	@JsonProperty("Insurancecompanyname")
	private String insurancecompanyname ;

	@JsonProperty("Sharedpercentage")
	private BigDecimal  sharedpercentage ;

	
	@JsonProperty("Leaderparticipant")
	private String leaderparticipant ;
	
	

	@JsonProperty("Requestreferenceno")
	private String requestreferenceno ;
	
	
	
	

	

}
