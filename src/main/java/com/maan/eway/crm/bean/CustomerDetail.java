package com.maan.eway.crm.bean;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetail {

	private String loginId;
	
	@JsonProperty("CustomerReferenceNo")
    private String customerReferenceNo;
	
	@JsonProperty("ProductId")
    private Integer productId;

	@JsonProperty("InsuranceId")
    private String companyId;
    	
	@JsonProperty("LeadId")
	private Long LeadId;

	@JsonProperty("QuoteStatus")
	private String quoteStatus;
	
	@JsonProperty("enqStatus")
	private String enqStatus;
	
	@JsonProperty("enqSeqNo")
	private Long enqSeqNo;
	
	@JsonProperty("leadSeqNo")
	private Long leadSeqNo;	
	
	
	public CustomerDetail(String loginId) {
        this.loginId = loginId;
    }
}
