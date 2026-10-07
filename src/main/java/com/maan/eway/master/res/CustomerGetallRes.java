package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerGetallRes {
	

	@JsonProperty("CustomerId")
    private String     customerId;
	@JsonProperty("GstNo")
    private String    gstNo  ;
	
	@JsonProperty("InsuranceId")
    private String     companyId;
	@JsonProperty("BranchCode")
    private String     branchCode;
	@JsonProperty("ClientName")
    private String     clientName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DateOfBirth")
    private Date       dateOfBirth  ;

	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("MobileNo1")
    private String     mobileNo1    ;

	@JsonProperty("ProductCount")
	private String productCount;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
    private Date       updatedDate;
	@JsonProperty("UpdatedBy")
    private String       updatedBy;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date       entryDate;

}
