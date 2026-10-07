package com.maan.eway.master.req;



import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductGroupGetReq {
	
	 @JsonProperty("GroupId")
	    private Integer    groupId ;
	
	 @JsonProperty("ProductId")
	    private Integer    productId ;

	  @JsonProperty("BranchCode")
	    private String     branchCode ;

	  @JsonProperty("CompanyId")
	    private String     companyId ;

//	  @JsonProperty("EffectiveDateStart") 
//	    private Date       effectiveDateStart ;

//	  @JsonProperty("Status")
//	    private String     status ;

}
