package com.maan.eway.master.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OptionsRes {

	@JsonProperty("UwQuesOptionId")
    private String   uwQuesOptionId ;
	
	@JsonProperty("UwQuesOptionDesc")
    private String   uwQuesOptionDesc ;
    
	@JsonProperty("Status")
    private String   status ;
    
	@JsonProperty("LoadingPercent")
    private String   loadingPercent ; 
    
	@JsonProperty("DependentYn")
    private String   dependentYn ;
    
	@JsonProperty("DependentUnderwriterId")
    private List<String>   dependentUnderwriterId ; 
	
	@JsonProperty("DependentUwAction")
    private String   dependentUwAction ;
  	
//	@JsonFormat(pattern ="dd/MM/yyyy")
//	@JsonProperty("EffectiveDateStart")
//	private Date effectiveDateStart;
//
//	@JsonFormat(pattern ="dd/MM/yyyy")
//	@JsonProperty("EffectiveDateEnd")
//	private Date effectiveDateEnd;
//
//	@JsonProperty("AmendId")
//	private Integer amendId;
	
	@JsonProperty("ReferralYn")
    private String  referralYn;
	
}
