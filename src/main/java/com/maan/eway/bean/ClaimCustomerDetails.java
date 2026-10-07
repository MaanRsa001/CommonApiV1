package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(ClaimCustomerDetailsId.class)
@Table(name = "wnm_claim_customer_details")
public class ClaimCustomerDetails implements Serializable{
    private static final long serialVersionUID = 1L;

    // --- ENTITY PRIMARY KEY
    @Id
    @Column(name = "CUSTOMER_REFERENCE_NO", nullable = false, length = 20)
    private String customerReferenceNo;

    @Id
    @Column(name = "COMPANY_ID", nullable = false, length = 20)
    private String companyId;

    // --- ENTITY DATA FIELDS
    @Column(name = "CLIENT_NAME", length = 250)
    private String clientName;

    @Column(name = "ADDRESS_1", length = 100)
    private String address1;

    @Column(name = "ADDRESS_2", length = 100)
    private String address2;

    @Column(name = "ADDRESS_3", length = 100)
    private String address3;

    @Column(name = "TITLE", length = 20)
    private String title;

    @Column(name = "TITLE_DESC", length = 20)
    private String titleDesc;

    @Column(name = "TITLE_DESC_LOCAL", length = 50)
    private String titleDescLocal;

    @Column(name = "VR_TIN_NO", length = 20)
    private String vrTinNo;

    @Column(name = "CLIENT_STATUS", length = 20)
    private String clientStatus;

    @Column(name = "CLIENT_STATUS_DESC", length = 100)
    private String clientStatusDesc;

    @Column(name = "POLICY_HOLDER_TYPE", length = 2)
    private String policyHolderType;

    @Column(name = "POLICY_HOLDER_TYPEID", nullable = false)
    private String policyHolderTypeid = "1";


    @Column(name = "POLICY_HOLDER_TYPE_DESC", length = 100)
    private String policyHolderTypeDesc;

    @Column(name = "POLICY_HOLDER_TYPE_ID_DESC", length = 100)
    private String policyHolderTypeIdDesc;

    @Column(name = "POLICY_HOLDER_TYPE_DESC_LOCAL", length = 50)
    private String policyHolderTypeDescLocal;

    @Column(name = "POLICY_HOLDER_TYPE_ID_DESC_LOCAL", length = 50)
    private String policyHolderTypeIdDescLocal;

    @Column(name = "POLICY_HODER_TYPE_DESC", length = 50)
    private String polHoderTypeDesc;

    @Column(name = "POLICY_HODER_TYPE_ID_DESC", length = 50)
    private String polHoderTypeIdDesc;

    @Column(name = "ID_TYPE", length = 100)
    private String idType;

    @Column(name = "ID_TYPE_DESC", length = 100)
    private String idTypeDesc;

    @Column(name = "ID_TYPE_DESC_LOCAL", length = 50)
    private String idTypeDescLocal;

    @Column(name = "ID_NUMBER", nullable = false, length = 100)
    private String idNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DOB_OR_REG_DATE")
    private Date dobOrRegDate;

    @Column(name = "AGE", nullable = false)
    private Integer age;

    @Column(name = "NATIONALITY", length = 20)
    private String nationality;

    @Column(name = "NATIONALITY_NAME", length = 100)
    private String nationalityName;

    @Column(name = "PLACE_OF_BIRTH", length = 100)
    private String placeOfBirth;

    @Column(name = "GENDER", length = 2)
    private String gender;

    @Column(name = "GENDER_DESC", length = 20)
    private String genderDesc;

    @Column(name = "GENDER_DESC_LOCAL", length = 50)
    private String genderDescLocal;

    @Column(name = "OCCUPATION", nullable = false, length = 10)
    private String occupation;

    @Column(name = "OCCUPATION_DESC", length = 1500)
    private String occupationDesc;

    @Column(name = "OCCUPATION_DESC_LOCAL", length = 50)
    private String occupationDescLocal;

    @Column(name = "OTHER_OCCUPATION", length = 200)
    private String otherOccupation;

    @Column(name = "BUSINESS_TYPE", length = 100)
    private String businessType;

    @Column(name = "BUSINESS_TYPE_DESC", length = 20)
    private String businessTypeDesc;

    @Column(name = "BUSINESS_TYPE_DESC_LOCAL", length = 50)
    private String businessTypeDescLocal;

    @Column(name = "VRN_GST", length = 20)
    private String vrnGst;

    @Column(name = "REGION_CODE", length = 20)
    private String regionCode;

    @Column(name = "STATE_CODE", length = 20)
    private String stateCode;

    @Column(name = "STATE_NAME", length = 100)
    private String stateName;

    @Column(name = "STATE_NAME_LOCAL", length = 50)
    private String stateNameLocal;

    @Column(name = "CITY_CODE", length = 20)
    private String cityCode;

    @Column(name = "CITY_NAME", length = 100)
    private String cityName;

    @Column(name = "city_name_local", length = 200)
    private String cityNameLocal;

    @Column(name = "STREET", length = 100)
    private String street;

    @Column(name = "FAX", length = 20)
    private String fax;

    @Column(name = "TELEPHONE_NO_1", length = 20)
    private String telephoneNo1;

    @Column(name = "TELEPHONE_NO_2", length = 20)
    private String telephoneNo2;

    @Column(name = "TELEPHONE_NO_3", length = 20)
    private String telephoneNo3;

    @Column(name = "MOBILE_CODE_1", length = 20)
    private String mobileCode1;

    @Column(name = "MOBILE_CODE_DESC_1", length = 20)
    private String mobileCodeDesc1;

    @Column(name = "MOBILE_CODE_DESC_1_LOCAL", length = 50)
    private String mobileCodeDesc1Local;

    @Column(name = "MOBILE_NO_1", length = 20)
    private String mobileNo1;

    @Column(name = "MOBILE_CODE_2", length = 20)
    private String mobileCode2;

    @Column(name = "MOBILE_CODE_DESC_2", length = 20)
    private String mobileCodeDesc2;

    @Column(name = "MOBILE_CODE_DESC_2_LOCAL", length = 50)
    private String mobileCodeDesc2Local;

    @Column(name = "MOBILE_NO_2", length = 20)
    private String mobileNo2;

    @Column(name = "MOBILE_CODE_3", length = 20)
    private String mobileCode3;

    @Column(name = "MOBILE_CODE_DESC_3", length = 20)
    private String mobileCodeDesc3;

    @Column(name = "MOBILE_CODE_DESC_3_LOCAL", length = 50)
    private String mobileCodeDesc3Local;

    @Column(name = "MOBILE_NO_3", length = 20)
    private String mobileNo3;

    @Column(name = "EMAIL_1", length = 100)
    private String email1;

    @Column(name = "EMAIL_2", length = 20)
    private String email2;

    @Column(name = "EMAIL_3", length = 20)
    private String email3;

    @Column(name = "WHATSAPP_CODE", length = 20)
    private String whatsappCode;

    @Column(name = "WHATSAPP_CODE_DESC", length = 20)
    private String whatsappCodeDesc;

    @Column(name = "WHATSAPP_CODE_DESC_LOCAL", length = 50)
    private String whatsappCodeDescLocal;

    @Column(name = "WHATSAPP_NO", length = 20)
    private String whatsappNo;

    @Column(name = "LANGUAGE", length = 1)
    private String language;

    @Column(name = "LANGUAGE_DESC", length = 20)
    private String languageDesc;

    @Column(name = "LANGUAGE_DESC_LOCAL", length = 50)
    private String languageDescLocal;

    @Column(name = "IS_TAX_EXEMPTED", nullable = false, length = 1)
    private String isTaxExempted;

    @Column(name = "TAX_EXEMPTED_ID", length = 20)
    private String taxExemptedId;

    @Column(name = "BRANCH_CODE", length = 20)
    private String branchCode;

    @Column(name = "BROKER_BRANCH_CODE", length = 20)
    private String brokerBranchCode;

    @Column(name = "PRODUCT_ID")
    private Integer productId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENTRY_DATE")
    private Date entryDate;

    @Column(name = "STATUS", length = 1)
    private String status;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPOINTMENT_DATE")
    private Date appointmentDate;

    @Column(name = "PREFERRED_NOTIFICATION", length = 100)
    private String preferredNotification;

    @Column(name = "PIN_CODE", length = 20)
    private String pinCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LICENSE_ISSUED_DATE")
    private Date licenseIssuedDate;

    @Column(name = "LICESENSE_DURATION")
    private Integer licesenseDuration;

    @Column(name = "LICENSE_DURATION")
    private Integer licenseDuration;

    @Column(name = "AREA_GROUP", length = 100)
    private String areaGroup;

    @Column(name = "AREA_CLARIFICATION", length = 100)
    private String areaClarification;

    @Column(name = "AREA_CLASIFICATION", length = 10)
    private String areaClasification;

    @Column(name = "MARITAL_STATUS", length = 100)
    private String maritalStatus;

    @Column(name = "MARITAL_STATUS_DESC", length = 100)
    private String maritalStatusDesc;

    @Column(name = "MARITAL_STATUS_DESC_LOCAL", length = 50)
    private String maritalStatusDescLocal;

    @Column(name = "POL_CUST_CODE", length = 50)
    private String polCustCode;

    @Column(name = "SUB_URB_ID")
    private Integer subUrbId;

    @Column(name = "FIRST_NAME", length = 200)
    private String firstName;

    @Column(name = "MIDDLE_NAME", length = 200)
    private String middleName;

    @Column(name = "LAST_NAME", length = 200)
    private String lastName;

    @Column(name = "CUSTOMER_CODE", length = 100)
    private String customerCode;

    @Column(name = "ZONE")
    private Integer zone;

    @Column(name = "Socio_Professional_Category", length = 200)
    private String socioProfessionalCategory;

    @Column(name = "Company_Name", length = 200)
    private String companyName;

    @Column(name = "Activities", length = 200)
    private String activities;

    @Column(name = "CUSTOMER_AS_INSURER", length = 200)
    private String customerAsInsurer;

    @Column(name = "COUNTRY", length = 100)
    private String country;

    @Column(name = "COUNTRY_NAME", length = 100)
    private String countryName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXPIRY_DATE")
    private Date expiryDate;

    @Column(name = "KRA_PIN", length = 100)
    private String kraPin;

    @Column(name = "FATHER_NAME", length = 50)
    private String fatherName;

    @Column(name = "MOTHER_NAME", length = 50)
    private String motherName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RISK_ASSESSMENT_DATE")
    private Date riskAssessmentDate;

    @Column(name = "VIPFLAG", length = 100)
    private String vipFlag;

    @Column(name = "PHONENO_CODE", length = 100)
    private String phoneNoCode;

    @Column(name = "LEAD_SEQNO")
    private Long leadSeqNo;

    @Column(name = "QUOTE_STATUS")
    private String quoteStatus;

    @Column(name = "cc_code", length = 100)
    private String ccCode;

    @Column(name = "cc_cust_type", length = 100)
    private String ccCustType;

    @Column(name = "cc_cust_catg", length = 50)
    private String ccCustCatg;

    @Column(name = "cc_cust_catg_code", length = 50)
    private String ccCustCatgCode;

    @Column(name = "CUSTOMER_TYPE", length = 100)
    private String customerType;

    @Column(name = "compliance_status_id", length = 20)
    private String complianceStatusId;

    @Column(name = "compliance_status")
    private String complianceStatus;

    @Column(name = "industry_type_id", length = 20)
    private String industryTypeId;

    @Column(name = "industry_type")
    private String industryType;

    @Column(name = "wealth_source_id", length = 20)
    private String wealthSourceId;

    @Column(name = "wealth_source")
    private String wealthSource;

    @Column(name = "legal_structure_id", length = 20)
    private String legalStructureId;

    @Column(name = "legal_structure")
    private String legalStructure;

    @Column(name = "owners_id", length = 20)
    private String ownersId;

    @Column(name = "owners")
    private String owners;

    @Column(name = "bank_id", length = 100)
    private String bankId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_no", length = 100)
    private String bankAccountNo;

    @Column(name = "bank_ifsc_code", length = 100)
    private String bankIfscCode;

    @Column(name = "bank_branch")
    private String bankBranch;
}
