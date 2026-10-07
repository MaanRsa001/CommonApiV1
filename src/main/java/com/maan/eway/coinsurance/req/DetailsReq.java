package com.maan.eway.coinsurance.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class DetailsReq {

	@JsonProperty("QuoteNo")
	@NotBlank(message = "QuoteNo is Empty")
	private String quoteNo;
	
	@JsonProperty("Sno")
	private String slNo;
	
	@JsonProperty("InsuranceCompanyId")
	private String insuranceCompanyId;

	@JsonProperty("InsuranceCompanyDesc")
	private String insuranceCompanyDesc;

	@JsonProperty("SharePrecentage")
	@NotBlank(message = "sharePrecentage is Empty")
	private String sharePrecentage;

	@JsonProperty("CoInsuranceRole")
	@NotBlank(message = "coInsuranceRole is Empty")
	private String coInsuranceRole;

	@JsonProperty("commissionPre")
	private String commissionPre;

	@JsonProperty("PolicyStartDate")
	 @Temporal(TemporalType.DATE)
    private Date policyStartDate;

	@JsonProperty("PolicyEndDate")
	 @Temporal(TemporalType.DATE)
    private Date policyEndDate;

}
