package com.maan.eway.otp.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.error.Error;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class OtpConfirm {
	@JsonProperty("isError")
	private Boolean isError;
	
	@JsonProperty("OtpToken")
	private Long otpToken;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("Errors")
	private List<Error> errorlist;
	
	@JsonProperty("OTP")
	private String otp;
	@JsonProperty("LoginResponse")
	private CommonLoginRes res;
}
