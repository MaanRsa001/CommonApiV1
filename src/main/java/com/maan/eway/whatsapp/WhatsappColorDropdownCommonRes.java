package com.maan.eway.whatsapp;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.error.Error;

import lombok.Data;

@Data
public class WhatsappColorDropdownCommonRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")
	private Boolean isError;

	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;

	@JsonProperty("Result")
	private List<WhatsappColorDropdownItemRes> result;

	@JsonProperty("ErroCode")
	private int erroCode;
}
