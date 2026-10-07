package com.maan.eway.common.req;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EserviceCustomerSaveReq {

	
	@JsonProperty("SaveOrSubmit")
	private String saveOrSubmit;
	
	@JsonProperty("InsuredReferenceNo")
	private String insuredReferenceNo;

	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PolicyHolderTypeid")
	private String policyHolderTypeid;

	@JsonProperty("IdNumber")
	private String idNumber;

	
	@JsonProperty("Gender")
	private String gender;

	@JsonProperty("Occupation")
	private String occupation;

	@JsonProperty("BusinessType")
	private String businessType;

	@JsonProperty("RegionCode")
	private String regionCode;

	@JsonProperty("IsTaxExempted")
	private String isTaxExempted;

	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("MiddleName")
	private String middleName;
	
	@JsonProperty("LastName")
	private String lastName;

	@JsonProperty("Address1")
	private String address1;

	@JsonProperty("Address2")
	private String address2;

	@JsonProperty("Title")
	private String title;

	
	@JsonProperty("Clientstatus")
	private String clientStatus;


	@JsonProperty("PolicyHolderType")
	private String policyHolderType;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DobOrRegDate")
	private Date dobOrRegDate;

	@JsonProperty("Nationality")
	private String nationality;

	@JsonProperty("Placeofbirth")
	private String placeOfBirth;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("AppointmentDate")
	private Date appointmentDate;
	
	@JsonProperty("PreferredNotification")
	private String preferredNotification;
	
	@JsonProperty("MaritalStatus")
	private String maritalStatus;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("LicenseIssuedDate")
	private Date licenseIssuedDate;

	@JsonProperty("IdType")
	private String idType;
	
/*
	@JsonProperty("IdTypeDesc")
	private String idTypeDesc;



	@JsonProperty("Age")
	private String age;


	@JsonProperty("TitleDesc")
	private String titleDesc;

	
	@JsonProperty("ClientStatusDesc")
	private String clientStatusDesc;

	@JsonProperty("GenderDesc")
	private String genderDesc;

	@JsonProperty("OccupationDesc")
	private String occupationDesc;

	@JsonProperty("BusinessTypeDesc")
	private String businessTypeDesc;

	@JsonProperty("Vrngst")
	private String vrnGst;
*/
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

	@JsonProperty("Fax")
	private String fax;

	@JsonProperty("TelephoneNo1")
	private String telephoneNo1;
	@JsonProperty("TelephoneNo2")
	private String telephoneNo2;
	@JsonProperty("TelephoneNo3")
	private String telephoneNo3;
	@JsonProperty("MobileNo1")
	private String mobileNo1;
	@JsonProperty("MobileNo2")
	private String mobileNo2;
	@JsonProperty("MobileNo3")
	private String mobileNo3;
	@JsonProperty("MobileCode1")
	private String mobileCode1;
	@JsonProperty("MobileCode2")
	private String mobileCode2;
	@JsonProperty("MobileCode3")
	private String mobileCode3;

	@JsonProperty("WhatsappCode")
	private String whatsappCode;
	@JsonProperty("WhatsappNo")
	private String whatsappNo;

	@JsonProperty("Email1")
	private String email1;
	@JsonProperty("Email2")
	private String email2;
	@JsonProperty("Email3")
	private String email3;
	@JsonProperty("Language")
	private String language;
	@JsonProperty("LanguageDesc")
	private String languageDesc;

	@JsonProperty("TaxExemptedId")
	private String taxExemptedId;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("VrTinNo")
	private String vrTinNo;
	@JsonProperty("EndorsementDate") // EndorsementDate
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date endorsementDate;
	@JsonProperty("EndorsementRemarks") // EndorsementRemarks
	private String endorsementRemarks;
	@JsonProperty("EndorsementEffectiveDate") // EndorsementEffectiveDate
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date endorsementEffdate;
	@JsonProperty("OrginalPolicyNo") // OrginalPolicyNo
	private String originalPolicyNo;
	@JsonProperty("EndtPrevPolicyNo") // EndtPrevPolicyNo
	private String endtPrevPolicyNo;
	@JsonProperty("EndtPrevQuoteNo") // EndtPrevQuoteNo
	private String endtPrevQuoteNo;
	@JsonProperty("EndtCount") // EndtCount
	private BigDecimal endtCount;
	@JsonProperty("EndtStatus") // EndtStatus
	private String endtStatus;
	@JsonProperty("IsFinanceEndt") // IsFinanceEndt
	private String isFinaceYn;
	@JsonProperty("EndtCategoryDesc") // EndtCategoryDesc
	private String endtCategDesc;
	@JsonProperty("EndorsementType") // EndorsementType
	private Integer endorsementType;

	@JsonProperty("EndorsementTypeDesc") // EndorsementTypeDesc
	private String endorsementTypeDesc;
	
	@JsonProperty("PinCode") 
	private String pinCode;
	
	@JsonProperty("Type") 
	private String type;
	
	@JsonProperty("OtherOccupation")
	private String otherOccupation;
	
	@JsonProperty("FirstName")
	private String firstName;

	@JsonProperty("CustomerCode")
	private String customerCode;

	@JsonProperty("Address3")
	private String address3;
	
	@JsonProperty("ZONE")
	private String zone;
	
	@JsonProperty("SocioProfessionalCategory")
	private String socioProfessionalCategory;
		
	@JsonProperty("Activities")
	private String activities;
	
	@JsonProperty("CustomerAsInsurer")
	private String customerAsInsurer;
	
	@JsonProperty("Country")
	private String     country ;
	
	@JsonProperty("CountryName")
	private String     countryName ;
	
	@JsonProperty("NationalityName")
	private String     nationalityName ;
	
	@JsonProperty("ExpiryDate")
	@JsonFormat(pattern ="dd/MM/yyyy")
	private Date expiryDate;
	
	@JsonProperty("KraPin")
	private String kraPin;
	

	@JsonProperty("VipFlag")
	private String vipFlag;
	
	@JsonProperty("PhoneNoCode")
	private String phoneNoCode;
	
	@JsonProperty("RiskAssessmentDate") 
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date riskAssessmentDate;

	@JsonProperty("FatherName")
	private String father_name;
	    
	@JsonProperty("MotherName")
	private String mother_name;
	
	@JsonProperty("LeadSeqNo")
	private Long leadSeqNo;
	
	@JsonProperty("CustomerType")
	private String customerType;
	
	  @JsonProperty("ComplianceStatusId")
	    private String complianceStatusId;

	    @JsonProperty("ComplianceStatus")
	    private String complianceStatus;

	    @JsonProperty("IndustryTypeId")
	    private String industryTypeId;

	    @JsonProperty("IndustryType")
	    private String industryType;

	    @JsonProperty("WealthSourceId")
	    private String wealthSourceId;

	    @JsonProperty("WealthSource")
	    private String wealthSource;


	    @JsonProperty("LegalStructureId")
	    private String legalStructureId;

	    @JsonProperty("LegalStructure")
	    private String legalStructure;

	    @JsonProperty("OwnersId")
	    private String ownersId;

	    @JsonProperty("Owners")
	    private String owners;
	    
	    @JsonProperty("HouseNo")
	    private String houseNo;
	    
	    @JsonProperty("Code")
	    private String code;
	    
	    @JsonProperty("AreaLocality")
	    private String  areaLocality;
	    
	    
}
