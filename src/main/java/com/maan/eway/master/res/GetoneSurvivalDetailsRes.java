package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetoneSurvivalDetailsRes {
	
	@JsonProperty("EndOfYear")
    private Integer     endOfYear ;
	
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart; 
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd; 



	@JsonProperty("SurrenderPercentage")
	private Integer amount; //surrenderPercentage
	
	@JsonProperty("Status")
    private String status ;
	
	@JsonProperty("CalcType")
	private String calcType;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
		
	@JsonProperty("Remarks")
	private String remarks;

}
