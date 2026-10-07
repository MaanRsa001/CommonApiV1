package com.maan.eway.common.res;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.error.Error;

import lombok.Data;

@Data
public class CommonRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")
	private Boolean isError;

	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;

	// Dynamic
	@JsonProperty("Result")
	private Object commonResponse;

	@JsonProperty("BenifitCover")
	private Object benifitCover;

	@JsonProperty("ErroCode")
	private int erroCode;

	private List<EserviceMotorDetails> motorDetailsList;

	public List<EserviceMotorDetails> getMotorDetailsList() {
	    return motorDetailsList;
	}

	public void setMotorDetailsList(List<EserviceMotorDetails> motorDetailsList) {
	    this.motorDetailsList = motorDetailsList;
	}

	@JsonProperty("RequestRefNo")
	private String requestRefNo;

	/*
	 * @JsonProperty("AdditionalData") private DefaultAllResponse defaultValue;
	 */
}
