package com.maan.eway.viewAll.dto;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UgandaDebitNotePIRes {
	
	@JsonProperty("Personal Info")
	private LinkedHashMap<String,Object> piInfo;
	
	@JsonProperty("Policy Info")
	private LinkedHashMap<String,Object> policyInfo;
	
	@JsonProperty("Premium Info")
	private LinkedHashMap<String,Object> premiumInfo;
	
	@JsonProperty("Company Info")
	private LinkedHashMap<String,Object> companyInfo;
	
	@JsonProperty("Endt Info")
	private LinkedHashMap<String,Object> endtInfo;

}
