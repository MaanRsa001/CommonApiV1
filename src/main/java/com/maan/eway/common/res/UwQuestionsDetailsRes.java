package com.maan.eway.common.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UwQuestionsDetailsRes implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("InsuranceId")
    private String    companyId ;
    
	@JsonProperty("ProductId")
    private String    productId ;
	
	@JsonProperty("SectionId")
    private String    sectionId ;
    

	@JsonProperty("BranchCode")
    private String    branchCode ;
	@JsonProperty("RequestReferenceNo")
    private String    requestReferenceNo ;   
	
	@JsonProperty("VehicleId")
    private String    vehicleId;   
	
	@JsonProperty("UwQuestionId")
    private String   uwQuestionId ;
    
	@JsonProperty("UwQuestionDesc")
    private String   uwQuestionDesc ;
    
	@JsonProperty("QuestionType")
    private String   questionType ;
    
	@JsonProperty("Value")
    private String   value;
    
	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;

	
	@JsonProperty("Status")
	private String status;

	
	@JsonProperty("IsReferral")
	private String isReferral;

	@JsonProperty("TextValue")
	private String textValue;
	
}
