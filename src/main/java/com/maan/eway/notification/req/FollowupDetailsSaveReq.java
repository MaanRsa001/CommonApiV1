package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FollowupDetailsSaveReq {

	@JsonProperty("FollowupId")
	private String followupId;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("FollowupDesc")
	private String followupDesc;

	@JsonProperty("Status")
    private String status;
    
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("StartDate")
    private String startDate;
	
	@JsonProperty("StartTime")
    private String startTime;
    
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EndDate")
    private String endDate;
	
	@JsonProperty("EndTime")
    private String endTime;
    
	
	@JsonProperty("Remarks")
    private String remarks;
    
    
    
    }
