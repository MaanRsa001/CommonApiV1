package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CurrencyMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("CurrencyId")
	private String currencyId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("Sno")
	private String sno;

	@JsonProperty("CurrencyName")
	private String currencyName;

	@JsonProperty("CurrencyShortCode")
	private String currencyShortCode;

	@JsonProperty("CurrencyShortName")
	private String currencyShortName;
	
//    @JsonProperty("Rfactor")
//	private String rfactor;

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

//	@JsonProperty("AmendId")
//	private Integer amendId;

	@JsonProperty("Remarks")
	private String remarks;
	

	@JsonProperty("CreatedBy")
	private String createdBy;
	

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("MinDiscount")
	private String minDiscount;
	
	@JsonProperty("MaxLoading")
	private String maxLoading;
	
	@JsonProperty("DecimalDigit")
	private String decimalDigit;
	
	@JsonProperty("ShortName")
	private String shortName;
	
	@JsonProperty("Rfactor")
	private String rfactor;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
}
