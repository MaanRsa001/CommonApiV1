package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InsertSurvivalReq {

	@JsonProperty("SurvivalBenefit")
    private List<SurvivalReq>    survivalReq;

	@JsonProperty("ProductId")
    private String     productId;
	
	@JsonProperty("SectionId")
    private String     sectionId;
	
	@JsonProperty("InsuranceId")
    private String     companyId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;  	//doubt
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd; //res
	
	@JsonProperty("PolicyTerm")
    private Integer     policyTerm ;
	
	@JsonProperty("SaveType")
    private String     saveType ;
	

}
