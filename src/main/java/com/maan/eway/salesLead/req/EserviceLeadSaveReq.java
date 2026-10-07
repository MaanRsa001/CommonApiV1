package com.maan.eway.salesLead.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EserviceLeadSaveReq {

	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("Address1")
	private String address1;
	
	@JsonProperty("BusinessType")
	private String businessType;
	
	@JsonProperty("CityCode")
	private String cityCode;
	
	@JsonProperty("CityName")
	private String cityName;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("Clientstatus")
	private String clientStatus;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DobOrRegDate")
	private Date dobOrRegDate;
	
	@JsonProperty("Email1")
	private String email1;
	
	@JsonProperty("Fax")
	private String fax;
	
	@JsonProperty("Gender")
	private String gender;
	
	@JsonProperty("IdNumber")
	private String idNumber;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("IsTaxExempted")
	private String isTaxExempted;
	
	@JsonProperty("MobileNo1")
	private String mobileNo1;
	
	@JsonProperty("Country")
	private String     country ;
	
	@JsonProperty("CountryName")
	private String     countryName ;
	
	@JsonProperty("Occupation")
	private String occupation;
	
	@JsonProperty("OtherOccupation")
	private String otherOccupation;
	
	@JsonProperty("Placeofbirth")
	private String placeOfBirth;

	@JsonProperty("PolicyHolderType")
	private String policyHolderType;
	
	@JsonProperty("PolicyHolderTypeid")
	private String policyHolderTypeid;
	
	@JsonProperty("PreferredNotification")
	private String preferredNotification;
	
	@JsonProperty("RegionCode")
	private String regionCode;
	
	@JsonProperty("MobileCode1")
	private String mobileCode1;
	
	@JsonProperty("MobileCodeDesc1")
	private String mobileCodeDesc1;
	
	@JsonProperty("WhatsappCode")
	private String whatsappCode;
	
	@JsonProperty("WhatsappDesc")
	private String whatsappDesc;
	
	@JsonProperty("StateCode")
	private String stateCode;
	
	@JsonProperty("StateName")
	private String stateName;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Street")
	private String street;
	
	@JsonProperty("TaxExemptedId")
	private String taxExemptedId;
	
	@JsonProperty("PinCode")
	private String pinCode;
	
	@JsonProperty("Title")
	private String title;
	
	@JsonProperty("VrTinNo")
	private String vrTinNo;
	
	@JsonProperty("SaveOrSubmit")
	private String saveOrSubmit;
	
	@JsonProperty("Zone")
	private String zone;
	
	@JsonProperty("CustomerAsInsurer")
	private String customerAsInsurer;
	
	@JsonProperty("GstIdentificationNo")
	private String gstIdentificationNo;
	
	@JsonProperty("LeadCreatedOn")
	private String leadCreatedOn;
	
	@JsonProperty("IntermediateId")
	private String intermediateId;
	
	@JsonProperty("IntermediateName")
	private String intermediateName;
	
	@JsonProperty("ChannelId")
	private String channelId;
	
	@JsonProperty("ChannelDesc")
	private String channelDesc;
	
	@JsonProperty("SectionTypeId")
	private String sectionTypeId;
	
	@JsonProperty("SectionTypeDesc")
	private String sectionTypeDesc;
	
	@JsonProperty("PropobabilityOfSuccessId")
	private String propobabilityOfSuccessId;
	
	@JsonProperty("PropobabilityOfSuccessDesc")
	private String propobabilityOfSuccessDesc;
	
	@JsonProperty("TypeOfBusinessId")
	private String typeOfBusinessId;
	
	@JsonProperty("TypeOfBusinessDesc")
	private String typeOfBusinessDesc;
	
	@JsonProperty("CurrentInsurer")
	private String currentInsurer;
	
	@JsonProperty("LeadContactPerson")
	private List<LeadContactPersonReq> leadContactPersonReq;
	
}
