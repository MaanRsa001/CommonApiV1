package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyTaxChangeStatusReq {

	 @JsonProperty("ProductId")
	 private String productId;
	 
	 @JsonProperty("TaxId")
	 private String taxId;
	 
	 @JsonProperty("InsuranceId")
	 private String companyId;
	 

	 @JsonFormat(pattern = "dd/MM/yyyy")
	 @JsonProperty("EffectiveDateStart")
	 private Date effectiveDateStart;
	 
	 @JsonProperty("Status")
	 private String status;
}
