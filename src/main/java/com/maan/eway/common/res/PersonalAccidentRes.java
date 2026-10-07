package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PersonalAccidentRes {
	
	
	
	@JsonProperty("PersonaId")
	private String personId;	

	@JsonProperty("QuoteNo")
	private String quoteNo;
	

	@JsonProperty("SerialNo")
	private BigDecimal serialNo;

	@JsonProperty("SectionDesc")
	private String sectionDesc;

	@JsonProperty("PersonName")
	private String personName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("Dob")
	private Date dob;

	@JsonProperty("Age")
	private Integer age;

	
	@JsonProperty("Height")
	private BigDecimal height;

	@JsonProperty("Weight")
	private BigDecimal weight;

	@JsonProperty("Description")
	private String description;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;


	@JsonProperty("CreatedBy")
	private String createdBy;


	@JsonProperty("Status")
    private String status;


	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;


	@JsonProperty("OccupationId")
	private String occupationId;


	@JsonProperty("CategoryId")
	private String categoryId;


	@JsonProperty("OccupationDesc")
	private String occupationDesc;


	@JsonProperty("Salary")
	private BigDecimal salary;


	@JsonProperty("Type")
	private String type;

	@JsonProperty("TypeDesc")
	private String typeDesc;

	@JsonProperty("PolicyNo")
    private String     policyNo;

}
