package com.maan.eway.salesLead.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InsertSalesReq {
	
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
	
	@JsonProperty("Title")
	private String title;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("Gender")
	private String gender;
	
	@JsonProperty("Occupation")
	private String occupation;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("MobileCode")
	private String mobileCode;
	
	@JsonProperty("MobileNumber")
	private String mobileNumber;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("IdNumber")
	private String idNumber;
	
	@JsonProperty("GstIdentificationNo")
	private String gstIdentificationNo;
	
	@JsonProperty("PreferredNotification")
	private String preferredNotification;
	
	@JsonProperty("IsTaxExempted")
	private String IsTaxExempted;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Street")
	private String street;
	
	@JsonProperty("CountryCode")
	private String countryCode;
	
	@JsonProperty("RegionCode")
	private String regionCode;
	
	@JsonProperty("StateCode")
	private String stateCode;
	
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
	
	@JsonProperty("SectionTypeId")
	private String sectionTypeId;
	
	@JsonProperty("PropobabilityOfSuccessId")
	private String propobabilityOfSuccessId;
	
	@JsonProperty("TypeOfBusinessId")
	private String typeOfBusinessId;
	
	@JsonProperty("CurrentInsurer")
	private String currentInsurer;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("LeadContactPerson")
	private List<LeadContactPersonReq> leadContactPersonReq;
}
