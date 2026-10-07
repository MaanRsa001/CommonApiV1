package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ExchangeMasterGetRes {


	@JsonProperty("SNo")
	private String sNo;
	
	@JsonProperty("ExchangeId")
	private String exchangeId;
	
	@JsonProperty("ExchangeRate")
	private String exchangeRate;
	
	@JsonProperty("CurrencyId")
	private String currencyId;
	
	@JsonProperty("CurrencyName")
	private String currencyName;
	
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("AmendId")
	private String amendId;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
}
