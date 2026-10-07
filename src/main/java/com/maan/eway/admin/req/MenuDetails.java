package com.maan.eway.admin.req;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class MenuDetails {
 
	
	
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
     
     @JsonFormat(pattern = "dd/MM/yyyy")
     @JsonProperty("EntryDate")
     private Date entryDate;
     
     @JsonProperty("MenuId")
 	  private String menuId;
     
     @JsonProperty("InsertType")
 	 private String Inserttype;
     
     @JsonProperty("CodeDescLocal")
     private String codeDescLocal;
     
}
