package com.maan.eway.notification.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class FollowUpDetailsPageRes {

	

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;
	
//	@JsonProperty("Status")
//    private String status;
//    
//	@JsonProperty("StatusDesc")
//    private String statusDesc;    
    
	@JsonProperty("BranchCode")
	private String branchCode;
    

	@JsonProperty("FollowupDetailsRes")
	private List<FollowUpDetailsListRes> followupDetailsRes;
}
