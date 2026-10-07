package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class getEmiDetailsListRes {
	
	@JsonProperty("InstallmentId")
	private String installmentId;
	
	@JsonProperty("DueDate")
	private String dueDate;
	
	@JsonProperty("DueAmount")
	private String dueAmount;
	
	@JsonProperty("InstallmentIdDesc")
	private String installmentIdDesc;

}
