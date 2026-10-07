package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement
@JsonDeserialize
public class DropDownRes {

	/**
	 * 
	 */
	@JsonProperty("TitleType")
	private String titletype;
	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("BodyType")
	private String bodyType;
	@JsonProperty("RiskId")
	private String riskId;
	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
	@JsonProperty("IsCommercial")
	private String isCommercial;
	
	@JsonProperty("OaCode")
	private Integer oaCode;
	
	@JsonProperty("Param1")
	private String param1;
	
	@JsonProperty("Param2")
	private String param2;
}
