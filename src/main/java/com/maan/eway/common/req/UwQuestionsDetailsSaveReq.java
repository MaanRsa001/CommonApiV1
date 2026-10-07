package com.maan.eway.common.req;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UwQuestionsDetailsSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("InsuranceId")
    private String    companyId ;
    
	@JsonProperty("ProductId")
    private String    productId ;
    
	@JsonProperty("BranchCode")
    private String   branchCode ;
	
	@JsonProperty("SectionId")
    private String    sectionId ;
	
	@JsonProperty("LocationId")
    private String    LocationId ;
	
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
    private String   value;  //option

	@JsonProperty("MandatoryYn")
    private String  mandatoryYn;

	
	@JsonProperty("Remarks")
	private String remarks;
	
	 
	@JsonProperty("Status")
	private String status;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("TextValue")
	private String textValue;
	
	@JsonProperty("LoadingPercent")
	private BigDecimal loadingPercent;
	
	@JsonProperty("QuestionCategory")
	private String questionCategory;
	
	@JsonProperty("QuestionCategoryDesc")
	private String questionCategoryDesc;

}
