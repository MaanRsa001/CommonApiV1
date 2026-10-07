package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateCustomerDetailsReq {
	
	@JsonProperty("Nationality")
	private String nationality;
	
	@JsonProperty("StateCode")
	private String stateCode;

	@JsonProperty("StateName")
	private String stateName;

	@JsonProperty("CityCode")
	private String cityCode;

	@JsonProperty("CityName")
	private String cityName;

	@JsonProperty("Street")
	private String street;
	
	@JsonProperty("Email1")
	private String email1;
	
	@JsonProperty("Gender")
	private String gender;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DobOrRegDate")
	private Date dobOrRegDate;
	
	@JsonProperty("Fax")
	private String fax;
	
	@JsonProperty("MobileNo1")
	private String mobileNo1;
	
	@JsonProperty("MobileCode1")
	private String mobileCode1;
	
	@JsonProperty("PolicyHolderTypeid")
	private String policyHolderTypeid;

	@JsonProperty("IdNumber")
	private String idNumber;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("Address1")
	private String address1;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	
	@JsonProperty("SailPointCode")
	private String sailPointCode;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("IdType")
	private String idType;
	
//	@JsonProperty("RegistrationNo")
//	private String registrationNo;
	
//	@JsonProperty("ChassisNo")
//	private String chassisNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("GenderDesc")
	private String genderDesc;
	
	@JsonProperty("IdTypeDesc")
	private String idTypeDesc;
	
	@JsonProperty("PolicyHoderTypeIdDesc")
	private String policyHoderTypeIdDesc;
	
	@JsonProperty("PolicyHolderType")
	private String policyHolderType;
	
	@JsonProperty("PolicyHolderTypeDesc")
	private String policyHolderTypeDesc;
	
	

}
