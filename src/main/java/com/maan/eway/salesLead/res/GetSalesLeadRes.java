package com.maan.eway.salesLead.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.salesLead.req.LeadContactPersonReq;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetSalesLeadRes {
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("LeadId")
	private String leadId;
	
	@JsonProperty("PolicyHolderTypeid")
	private String policyHolderTypeid;
	
	@JsonProperty("PolicyHolderTypeDesc")
	private String policyHolderTypeDesc;
	
	@JsonProperty("Title")
	private String title;
	
	@JsonProperty("TitleDesc")
	private String titleDesc;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("Gender")
	private String gender;
	
	@JsonProperty("GenderDesc")
	private String genderDesc;
	
	@JsonProperty("Occupation")
	private String occupation;
	
	@JsonProperty("OccupationDesc")
	private String occupationDesc;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("MobileCode")
	private String mobileCode;
	
	@JsonProperty("MobileNumber")
	private String mobileNumber;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("IdTypeDesc")
	private String idTypeDesc;
	
	@JsonProperty("IdNumber")
	private String idNumber;
	
	@JsonProperty("GstIdentificationNo")
	private String gstIdentificationNo;
	
	@JsonProperty("PreferredNotification")
	private String preferredNotification;
	
	@JsonProperty("IsTaxExempted")
	private String isTaxExempted;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("StatusDesc")
	private String statusDesc;
	
	@JsonProperty("Street")
	private String street;
	
	@JsonProperty("CountryCode")
	private String countryCode;
	
	@JsonProperty("CountryCodeDesc")
	private String countryCodeDesc;
	
	@JsonProperty("RegionCode")
	private String regionCode;
	
	@JsonProperty("RegionCodeDesc")
	private String regionCodeDesc;
	
	@JsonProperty("StateCode")
	private String stateCode;
	
	@JsonProperty("StateCodeDesc")
	private String stateCodeDesc;
	
	@JsonProperty("PoBox")
	private String poBox;
	
	@JsonProperty("IntermediateId")
	private String intermediateId;
	
	@JsonProperty("IntermediateName")
	private String intermediateName;
	
	@JsonProperty("LeadCreatedOn")
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private String leadCreatedOn;
	
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
	
	@JsonProperty("CurrentInsurerDesc")
	private String currentInsurerDesc;
	
	@JsonProperty("EnquiryCount")
	private String enquiryCount;
	
	@JsonProperty("EntryDate")
	private String entryDate;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("UpdatedDate")
	private String updatedDate;
	
	@JsonProperty("LeadContactPerson")
	private List<LeadContactPersonReq> leadContactPersonReq;
}
