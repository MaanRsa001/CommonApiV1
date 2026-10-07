package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UwQuestionMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("InsuranceId")
    private String    companyId ;
    
	@JsonProperty("ProductId")
    private String    productId ;
    
	@JsonProperty("BranchCode")
    private String    branchCode ;
	
	@JsonProperty("SectionId")
    private String    sectionId ;
    
	
	@JsonProperty("UwQuestionId")
    private String   uwQuestionId ;
    
	@JsonProperty("UwQuestionDesc")
    private String   uwQuestionDesc ;
    
	@JsonProperty("QuestionType")
    private String   questionType ;
    
	@JsonFormat(pattern ="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("Status")
	private String status;

	@JsonProperty("MandatoryYn")
	private String mandatoryYn;

	@JsonProperty("DataType")
	private String dataType;
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

//	@JsonProperty("CoreAppCode")
//	private String coreAppCode;
//	
//	@JsonProperty("RegulatoryCode")
//	private String regulatoryCode;
	
	@JsonProperty("Options")
	private List<OptionsReq> optionsReq;	

	@JsonProperty("QuestionCategory")
	private String questionCategory;
	

	@JsonProperty("questionCategoryDesc")
	private String questionCategoryDesc;
}
