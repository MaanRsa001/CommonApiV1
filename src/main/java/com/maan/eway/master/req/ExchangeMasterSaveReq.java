package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeMasterSaveReq {

	@JsonProperty("SNo")
	private String sNo;
	
	@JsonProperty("ExchangeId")
	private String exchangeId;
	
	@JsonProperty("ExchangeRate")
	private String exchangeRate;
	
	@JsonProperty("CurrencyId")
	private String currencyId;
	
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
}
