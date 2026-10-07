package com.maan.eway.master.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.master.req.RiskListRes;

import lombok.Data;

@Data
public class RiskDetailsListGetRes {

	@JsonProperty("ProductId")
    private String     productId ;
	
	@JsonProperty("ProductName")
    private String     productName ;
	
	@JsonProperty("CustomerId")
    private String     customerId ;
	
	@JsonProperty("RequestReferenceNo")
    private String     requestReferenceNo ;
	
	@JsonProperty("InsuranceId")
    private String     insuranceId ;
	
	@JsonProperty("BranchCode")
    private String     branchCode;
	
	@JsonProperty("CreatedBy")
    private String     createdBy;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date entryDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
    private Date updatedDate;
	
	@JsonProperty("UpdatedBy")
    private String updatedBy;
	
	@JsonProperty("RiskList")
    private List<RiskListRes>     riskList ;
}
