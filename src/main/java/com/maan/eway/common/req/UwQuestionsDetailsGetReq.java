package com.maan.eway.common.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UwQuestionsDetailsGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("InsuranceId")
    private String    companyId ;
    
	@JsonProperty("ProductId")
    private String    productId ;
	
	@JsonProperty("SectionId")
    private String    sectionId ;
    
	@JsonProperty("RequestReferenceNo")
    private String    requestReferenceNo ;   
	
	@JsonProperty("VehicleId")
    private String    vehicleId;   
	
	@JsonProperty("UwQuestionId")
    private String   uwQuestionId ;

	@JsonProperty("BranchCode")
    private String    branchCode;
   
}
