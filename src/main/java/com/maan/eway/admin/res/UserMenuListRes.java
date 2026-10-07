package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserMenuListRes {

	@JsonProperty("MenuId")
	private Integer menuId;
	

	@JsonProperty("MenuName")
	private String menuName;
	

	@JsonProperty("MenuUrl")
	private String menuUrl;


	@JsonProperty("ParentMenu")
	private String parentMenu;
	
	@JsonProperty("DisplayOrder")
	private Integer displayOrder;
}
