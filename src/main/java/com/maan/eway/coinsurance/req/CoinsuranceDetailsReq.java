package com.maan.eway.coinsurance.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class CoinsuranceDetailsReq {


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

	

}
