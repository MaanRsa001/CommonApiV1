package com.maan.eway.common.res;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.maan.eway.error.Error;
import com.maan.eway.res.DropDownRes;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement
public class CommonDropdown implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("Message")
	@SerializedName("Message")
	private String message;

	@JsonProperty("IsError")
	@SerializedName("IsError")
	private Boolean isError;
	
	@JsonProperty("ErrorMessage")
	@SerializedName("ErrorMessage")
	private List<Error> errorMessage;

	//Dynamic
	@JsonProperty("Result")
	@SerializedName("Result")
	private DropDownRes []commonResponse;
	
	@JsonProperty("ErroCode")
	@SerializedName("ErroCode")
	private int erroCode;

	

/*	@JsonProperty("AdditionalData")
	private DefaultAllResponse defaultValue; */
}
