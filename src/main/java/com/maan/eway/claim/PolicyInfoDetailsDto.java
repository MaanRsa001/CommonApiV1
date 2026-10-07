package com.maan.eway.claim;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyInfoDetailsDto {

	@JsonProperty("PolicyNo")
	private String policyNo;

	@JsonProperty("PolicySysId")
	private String policySysId;

	@JsonProperty("PolicyEndSrNo")
	private String policyEndSrNo;

	@JsonProperty("Branch")
	private String branch;

	@JsonProperty("Broker")
	private String broker;

	@JsonProperty("Producer")
	private String producer;

	@JsonProperty("Bank")
	private String bank;

	@JsonProperty("PolicyFrom")
	private Date policyFrom;

	@JsonProperty("PolicyTo")
	private Date policyTo;

	@JsonProperty("BusinessType")
	private String businessType;

	@JsonProperty("SourceOfBusiness")
	private String sourceOfBusiness;

	@JsonProperty("FleetPolicy")
	private String fleetPolicy;

	@JsonProperty("Uwyear")
	private String uwyear;

	@JsonProperty("AgencyRepair")
	private String agencyRepair;
	
	@JsonProperty("ProductDesc")
	private String productDesc;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductCode")
	private String productcode;
	
	@JsonProperty("PolicyTypeId")
	private String policytypeId;

	@JsonProperty("Endtno")
	private String endtNo;
	
	@JsonProperty("Customer")
	private String customer;
	
	@JsonProperty("Contactpername")
	private String contactPerName;
	
	@JsonProperty("Civilid")
	private String civilId;
	
	@JsonProperty("Divisioncode")
	private String divisionCode;
	
	@JsonProperty("Department")
	private String department;
	
	@JsonProperty("Insured")
	private String insured;
	
	@JsonProperty("Product")
	private String product;
	
	@JsonProperty("BrokerCode")
	private String brokerCode;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("OutstandingAmt")
	private String outstandingAmt;
	
	@JsonProperty("CurrencyCode")
	private String currencyCode;
	
	@JsonProperty("CurrencyName")
	private String currencyName;

	@JsonProperty("PolhApprSts")
	private String polhApprSts;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("RegionCode")
	private String regionCode;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("SectionCode")
	private String sectionCode;

	@JsonProperty("Email")
	private String email;

	@JsonProperty("Address")
	private String address;

	@JsonProperty("MobileNumber")
	private String mobileNumber;

	@JsonProperty("Occupation")
	private String occupation;

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("CompanyName")
	private String companyName;

	@JsonProperty("ProductType")
	private String productType;

	@JsonProperty("Nationality")
	private String nationality;

	@JsonProperty("MobileCode")
	private String mobileCode;

	@JsonProperty("UwSysId")
	private String uwSysId;

	@JsonProperty("Polidx")
	private String polidx;

	@JsonProperty("Uwlob")
	private String uwlob;

	@JsonProperty("EndNo")
	private String endNo;

	@JsonProperty("CompanyCode")
	private String companyCode;

	@JsonProperty("BrokerName")
	private String brokerName;

	@JsonProperty("CustCode")
	private String custCode;

	@JsonProperty("CustName")
	private String custName;

	@JsonProperty("DeptCode")
	private String deptCode;

	@JsonProperty("PhoneNo")
	private String phoneNo;

	@JsonProperty("GenderDesc")
	private String genderDesc;

	@JsonProperty("District")
	private String district;

	@JsonProperty("StateName")
	private String stateName;

	@JsonProperty("StateCode")
	private String stateCode;

	@JsonProperty("Street")
	private String street;

	@JsonProperty("CityName")
	private String cityName;

	@JsonProperty("CityCode")
	private String cityCode;

	@JsonProperty("PostalNo")
	private String postalNo;

	@JsonProperty("IdType")
	private String idType;

	@JsonProperty("IdTypeDesc")
	private String idTypeDesc;

	@JsonProperty("IdNumber")
	private String idNumber;

	@JsonProperty("PolicyHolderTypeId")
	private String policyHolderTypeId;

	@JsonProperty("PolicyHolderTypeDesc")
	private String policyHolderTypeDesc;
	
	@JsonProperty("Age")
	private String age;
	
	@JsonProperty("DOB")
	private Date dob;
	
	@JsonProperty("SourceType")
	private String sourceType;
	
	@JsonProperty("SourceTypeId")
	private String sourceTypeId;

	@JsonProperty("MotorList")
	private List<MotorRes> motorList;


}
