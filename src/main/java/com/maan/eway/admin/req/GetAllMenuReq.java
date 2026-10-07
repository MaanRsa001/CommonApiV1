package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetAllMenuReq {

	
 @JsonProperty("Limit")
  private Integer limit;
  
  @JsonProperty("Offset")
  private Integer offset;
  
  @JsonProperty("GetType")
  private String getType;
  
  @JsonProperty("MenuId")
  private String menuId;
  
  @JsonProperty("CompanyId")
  private String companyId;
}
