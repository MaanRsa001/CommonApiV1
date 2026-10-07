package com.maan.eway.auth.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LobProductRes {
	@JsonProperty("LobId")
    private String lobId;
	@JsonProperty("LobCode")
	private String lobCode;
	@JsonProperty("LobName")
    private String lobName;
	@JsonProperty("LobImage")
	private String lobImage;
	@JsonProperty("ProductList")
    private List<ProductDropDownRes> productList;
}
