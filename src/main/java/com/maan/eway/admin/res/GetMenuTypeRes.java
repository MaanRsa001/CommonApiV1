package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class GetMenuTypeRes {
	
	@JsonProperty("MenuName")
	private String menuName;
	
	 @JsonProperty("MenuId")
	  private String menuId;
	 
	 @JsonProperty("UserType")
	  private String usertype;


}
