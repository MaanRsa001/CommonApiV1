package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FdFactorCalcRes {

	 //--- ENTITY PRIMARY KEY 
	@JsonProperty("FactorId")
    private Integer    factorId ;

    //--- ENTITY DATA FIELDS 
	@JsonProperty("FactorName")
    private String     factorName ;

	@JsonProperty("OwnDamage")
    private Double     ownDamage ;

	@JsonProperty("Windscreen")
    private Double     windscreen ;

	@JsonProperty("Theft")
    private Double     theft ;

	@JsonProperty("Fire")
    private Double     fire ;

	@JsonProperty("ThirdParty")
    private Double     thirdParty ;

   
}
