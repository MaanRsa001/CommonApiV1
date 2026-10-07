package com.maan.eway.salesLead.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.salesLead.req.LeadContactPersonReq;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
public class GetLeadDetailsRes {

	@Column(name = "CustomerReferenceNo")
	private String customerReferenceNo ;
	
	@Column(name = "CompanyId")
	private String companyId ;
	
	@Column(name = "ProductId")
	private Integer productId ;
	
	@Column(name = "ClientName")
	private String clientName ;
	
	@Column(name = "Address1")
	private String address1 ;
	
	@Column(name = "Address2")
	private String address2 ;
	
	@Column(name = "Title")
	private String title ;
	
	@Column(name = "RegionCode")
	private String regionCode ;
	
	@Column(name = "TitleDesc")
	private String titleDesc ;
	
	@Column(name = "ClientStatus")
	private String clientStatus ;
	
	@Column(name = "StateCode")
	private Integer stateCode ;
	
	@Column(name = "VrTinNo")
	private String vrTinNo ;
	
	@Column(name = "StateName")
	private String stateName ;
	
	@Column(name = "BrokerBranchCode")
	private String brokerBranchCode ;
	
	@Column(name = "ClientStatusDesc")
	private String clientStatusDesc ;
	
	@Column(name = "PolicyHolderType")
	private String policyHolderType ;
	
	@Column(name = "CityCode")
	private Integer cityCode ;
	
	@Column(name = "CityName")
	private String cityName ;
	
	@Column(name = "PolicyHolderTypeid")
	private String policyHolderTypeid ;
	
	@Column(name = "IdType")
	private String idType ;
	
	@Column(name = "IdTypeDesc")
	private String idTypeDesc ;
	
	@Column(name = "IdNumber")
	private String idNumber ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name = "DobOrRegDate")
	private Date dobOrRegDate ;
	
	@Column(name = "CreatedBy")
	private String createdBy ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name = "EntryDate")
	private Date entryDate ;
	
	@Column(name = "Age")
	private Integer age ;
	
	@Column(name = "Nationality")
	private String nationality ;
	
	@Column(name = "Status")
	private String status ;
	
	@Column(name = "UpdatedBy")
	private String updatedBy ;
	
	@Column(name = "PlaceOfBirth")
	private String placeOfBirth ;

	@Column(name = "Gender")
	private String gender ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name = "UpdatedDate")
	private Date updatedDate ;
	
	@Column(name = "GenderDesc")
	private String genderDesc ;
	
	@Column(name = "Occupation")
	private String occupation ;
	
	@Column(name = "OccupationDesc")
	private String occupationDesc ;
	
	@Column(name = "BusinessType")
	private String businessType ;
	
	@Column(name = "BusinessTypeDesc")
	private String businessTypeDesc ;
	
	@Column(name = "VrnGst")
	private String vrnGst ;
	
	@Column(name = "Fax")
	private String fax ;
	
	@Column(name = "TelephoneNo1")
	private String telephoneNo1 ;
	
	@Column(name = "Language")
	private String language ;
	
	@Column(name = "LanguageDesc")
	private String languageDesc ;
	
	@Column(name = "IsTaxExempted")
	private String isTaxExempted ;
	
	@Column(name = "TaxExemptedId")
	private String taxExemptedId ;
	
	@Column(name = "BranchCode")
	private String branchCode ;
	
	@Column(name = "PolicyHolderTypeDesc")
	private String policyHolderTypeDesc ;
	
	@Column(name = "PolicyHolderTypeIdDesc")
	private String policyHolderTypeIdDesc ;
	
	@Column(name = "Street")
	private String street ;
	
	@Column(name = "MobileCode1")
	private String mobileCode1 ;
	
	@Column(name = "MobileCodeDesc1")
	private String mobileCodeDesc1 ;
	
	@Column(name = "MobileNo1")
	private String mobileNo1 ;
	
	@Column(name = "WhatsappCode")
	private String whatsappCode ;
	
	@Column(name = "WhatsappCodeDesc")
	private String whatsappCodeDesc ;
	
	@Column(name = "WhatsappNo")
	private String whatsappNo ;
	
	@Column(name = "Email1")
	private String email1 ;
	
	@Column(name = "PreferredNotification")
	private String preferredNotification ;
	
	@Column(name = "PinCode")
	private String pinCode ;
	
	@Column(name = "OtherOccupation")
	private String otherOccupation;
	
	@Column(name = "LicenseDuration")
	private Integer licenseDuration ;
	
	@Column(name = "AreaGroup")
	private String areaGroup ;
	
	@Column(name = "AreaClasification")
	private String areaClasification ;
	
	@Column(name = "MaritalStatus")
	private String maritalStatus ;
	
	@Column(name = "PolCustCode")
	private String polCustCode ;
	
	@Column(name = "FirstName")
	private String firstName ;
	
	@Column(name = "MiddleName")
	private String middleName ;
	
	@Column(name = "LastName")
	private String lastName ;
	
	@Column(name = "CustomerCode")
	private String customerCode ;
	
	@Column(name = "Address3")
	private String address3 ;
	
	@Column(name = "Zone")
	private Integer zone;
	
	@Column(name = "GstIdentificationNo")
	private String gstIdentificationNo;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name = "LeadCreatedDate")
	private Date leadCreatedDate;
	
	@Column(name = "IntermediateId")
	private String intermediateId;
	
	@Column(name = "IntermediateName")
	private String intermediateName;
	
	@Column(name = "ChannelId")
	private String channelId;
	
	@Column(name = "ChannelName")
	private String channelName;
	
	@Column(name = "SectionTypeId")
	private String sectionTypeId;

	@Column(name = "SectionTypeDesc")
	private String sectionTypeDesc;
	
	@Column(name = "PropobabilityOfSuccessId")
	private String propobabilityOfSuccessId;
	
	@Column(name = "PropobabilityOfSuccessDesc")
	private String propobabilityOfSuccessDesc;
	
	@Column(name = "TypeOfBussinessId")
	private String typeOfBussinessId;
	
	@Column(name = "TypeOfBussinessDesc")
	private String typeOfBussinessDesc;
	
	@Column(name = "CurrentInsurer")
	private String currentInsurer;
	
	@JsonProperty("LeadContactPerson")
	private List<LeadContactPersonReq> leadContactPersonReq;
	
}
