package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerGetallCriteriaRes {

	@JsonProperty("CustomerId")
    private Integer     customerId;
	@JsonProperty("GstNo")
    private String    gstNo  ;
	
	@JsonProperty("InsuranceId")
    private String     companyId;
	@JsonProperty("BranchCode")
    private String     branchCode;
	@JsonProperty("ClientName")
    private String     clientName;
	@JsonProperty("DateOfBirth")
    private Date       dateOfBirth  ;

	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("MobileNo1")
    private String     mobileNo1    ;
	@JsonProperty("Email1")
    private String     email1    ;

	@JsonProperty("ProductCount")
	private Long productCount;
	
	@JsonProperty("UpdatedDate")
    private Date       updatedDate;
	@JsonProperty("UpdatedBy")
    private String       updatedBy;
	@JsonProperty("EntryDate")
    private Date       entryDate;


}
