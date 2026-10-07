package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LovDetailsGetRes {

	  //----------------------------------------------------------------------
    // ENTITY PRIMARY KEY 
    //----------------------------------------------------------------------
	@JsonProperty("ItemId")
    private String  itemId       ;

    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS 
    //----------------------------------------------------------------------    
	@JsonProperty("ItemType")
    private String     itemType     ;
	@JsonProperty("ItemCode")
    private String     itemCode     ;
	@JsonProperty("ItemValue")
    private String     itemValue    ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date       entryDate    ;
	@JsonProperty("CreatedBy")
    private String     createdBy       ;
	@JsonProperty("UpdatedBy")
    private String     updatedBy       ;
	@JsonProperty("Status")
    private String     status       ;
	@JsonProperty("InsuranceId")
    private String     insuranceId    ;
	@JsonProperty("BranchCode")
    private String     branchCode   ;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
    private Date       effectiveDateStart    ;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
    private Date       effectiveDateEnd ;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
    private Date       updatedDate;
	

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;

}
