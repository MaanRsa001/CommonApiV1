package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.admin.req.UserTypeReq;

import lombok.Data;
@Data
public class GetmenuDetailsRes2 {
	@JsonProperty("MenuName")
	private String menuName;
	
    @JsonProperty("MenuURL")
	private String menuUrl;
	
    @JsonProperty("MenuType")
	private String menuType;
    
    @JsonProperty("ParentMenu")
   	private String parentMenu;
	
    @JsonProperty("ProductId")
	private String productId;
    
    @JsonProperty("Status")
	private String status;
	
    @JsonProperty("CompanyId")
   	private String CompanyId;
    
    @JsonProperty("UserTypeList")
   	private List<UserTypeReq> usertypelist;
    
     @JsonProperty("CreatedBY")
   	private String createdBy;
    
     @JsonProperty("MenuLogo")
    private String menulogo;
     
     @JsonProperty("DisplayOrder")
     private String displayOrder;
     
     @JsonProperty("EntryDate")
     private String entryDate;
     
     @JsonProperty("MenuId")
 	  private String menuId;
     
     @JsonProperty("CodeDescLocal")
     private String codeDescLocal;
}
