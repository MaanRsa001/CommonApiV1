package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BankMasterRes implements Serializable {

    private static final long serialVersionUID = 1L;


	@JsonProperty("BankCode")
    private String     bankCode     ;
	
	@JsonProperty("InsuranceId")
    private String     companyId;

	@JsonProperty("BranchCode")
    private String     branchCode;
		
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("BankShortName")
    private String     bankShortName ;
	
	@JsonProperty("BankFullName")
    private String     bankFullName ;	
	
	@JsonProperty("Status")
    private String     status ;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
		
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
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	

}
