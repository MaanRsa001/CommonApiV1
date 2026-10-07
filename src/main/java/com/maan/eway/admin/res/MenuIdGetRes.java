package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MenuIdGetRes {

	@JsonProperty("MenuId")
	private List<String> menuId;
	}
