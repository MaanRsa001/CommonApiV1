package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InsuredDetailsGetRes {

	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("InsuredReferenceNo")
    private String insuredReferenceNo ;

	@JsonProperty("PolicyHolderTypeid")
	private String policyHolderTypeid;

	
	@JsonProperty("IdType")
	private String idType;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("AppointmentDate")
	private Date appointmentDate;
	
	@JsonProperty("PreferredNotification")
	private String preferredNotification;
	
	@JsonProperty("IdNumber")
	private String idNumber;

	@JsonProperty("Age")
	private String age;

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

	@JsonProperty("Address1")
	private String address1;

	@JsonProperty("Address2")
	private String address2;

	@JsonProperty("Title")
	private String title;

	@JsonProperty("TitleDesc")
	private String titleDesc;

	@JsonProperty("Clientstatus")
	private String clientStatus;

	@JsonProperty("ClientStatusDesc")
	private String clientStatusDesc;

	@JsonProperty("PolicyHolderType")
	private String policyHolderType;
	
	@JsonProperty("PolicyHolderTypeDesc")
	private String policyHolderTypeDesc;

	@JsonProperty("IdTypeDesc")
	private String idTypeDesc;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DobOrRegDate")
	private Date dobOrRegDate;

	@JsonProperty("Nationality")
	private String nationality;

	@JsonProperty("Placeofbirth")
	private String placeOfBirth;

	@JsonProperty("GenderDesc")
	private String genderDesc;

	@JsonProperty("OccupationDesc")
	private String occupationDesc;

	@JsonProperty("BusinessTypeDesc")
	private String businessTypeDesc;

	@JsonProperty("Vrngst")
	private String vrnGst;

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
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("UpdatedBy")
	private String updatedBy;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("VrTinNo")
	private String vrTinNo;

	@JsonProperty("MobileCode1")
	private String mobileCode1;
	@JsonProperty("MobileCode2")
	private String mobileCode2;
	@JsonProperty("MobileCode3")
	private String mobileCode3;

	@JsonProperty("MobileCodeDesc1")
	private String mobileCodeDesc1;
	@JsonProperty("MobileCodeDesc2")
	private String mobileCodeDesc2;
	@JsonProperty("MobileCodeDesc3")
	private String mobileCodeDesc3;

	@JsonProperty("WhatsappCode")
	private String whatsappCode;
	@JsonProperty("WhatsappDesc")
	private String whatsappDesc;
	@JsonProperty("WhatsappNo")
	private String whatsappNo;
	
	 @JsonProperty("EndorsementDate") //EndorsementDate
	    @JsonFormat(pattern = "dd/MM/yyyy")
	    private Date       endorsementDate ;
	    @JsonProperty("EndorsementRemarks") // EndorsementRemarks
	    private String     endorsementRemarks ;    
	    @JsonProperty("EndorsementEffectiveDate") // EndorsementEffectiveDate
	    @JsonFormat(pattern = "dd/MM/yyyy")
	    private Date       endorsementEffdate ;
	    @JsonProperty("OrginalPolicyNo") // OrginalPolicyNo
	    private String     originalPolicyNo ;
	    @JsonProperty("EndtPrevPolicyNo") // EndtPrevPolicyNo
	    private String     endtPrevPolicyNo ;
	    @JsonProperty("EndtPrevQuoteNo") // EndtPrevQuoteNo
	    private String     endtPrevQuoteNo ;
	    @JsonProperty("EndtCount")  // EndtCount
	    private BigDecimal endtCount ;
	    @JsonProperty("EndtStatus") //EndtStatus
	    private String     endtStatus ;   
	    @JsonProperty("IsFinanceEndt") //IsFinanceEndt
	    private String     isFinaceYn ;  
	    @JsonProperty("EndtCategoryDesc") //EndtCategoryDesc
	    private String     endtCategDesc ;
	    @JsonProperty("EndorsementType") //EndorsementType
	    private Integer    endorsementType ;

	    @JsonProperty("EndorsementTypeDesc") // EndorsementTypeDesc
	    private String     endorsementTypeDesc ;
	    
	    @JsonProperty("PinCode") 
	    private String     pinCode; 
	    
	    @JsonProperty("OtherOccupation")
	    private String otherOccupation;

		@JsonProperty("FirstName")
		private String firstName;

		@JsonProperty("MiddleName")
		private String middleName;

		@JsonProperty("LastName")
		private String lastName;

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
		
		@JsonProperty("MaritalStatus")
		private String maritalStatus;

		@JsonFormat(pattern = "dd/MM/yyyy")
		@JsonProperty("ExpiryDate")
		private Date expiryDate;
}
