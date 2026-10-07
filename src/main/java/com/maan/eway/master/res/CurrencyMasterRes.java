package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CurrencyMasterRes implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("CurrencyId")
	private String currencyId;


	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
/*	@JsonProperty("Sno")
	private String sno; */

	@JsonProperty("CurrencyName")
	private String currencyName;

	@JsonProperty("ShortName")
	private String shortName;

	@JsonProperty("Rfactor")
	private String rfactor;

	@JsonProperty("SubCurrency")
	private String subCurrency;

	@JsonProperty("Status")
	private String status;

	
	@JsonProperty("ExMinlmt")
	private String exMinlmt;
	
	@JsonProperty("ExMaxlmt")
	private String exMaxlmt;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	@JsonProperty("CurrencyShortCode")
	private String currencyShortCode;

	@JsonProperty("AmendId")
	private Integer amendId;

	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonProperty("MinDiscount")
	private String minDiscount;
	
	@JsonProperty("MaxLoading")
	private String maxLoading;
	
	@JsonProperty("DecimalDigit")
	private String decimalDigit;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
}
