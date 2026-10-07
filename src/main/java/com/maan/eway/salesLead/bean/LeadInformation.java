package com.maan.eway.salesLead.bean;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@DynamicUpdate
@Data
@Table(name = "lead_information")
public class LeadInformation  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LEAD_ID", length = 20, nullable = false)
	private String leadId;
	
	@Column(name = "COMPANY_ID")
	private String companyId;
	
	@Column(name = "PRODUCT_ID")
	private String productId;
	
	@Column(name = "BRANCH_CODE")
	private String branchCode;
	
	@Column(name = "POLICY_HOLDER_TYPE")
	private String policyHolderType;
	
	@Column(name = "POLICY_HOLDER_TYPE_DESC")
	private String policyHolderTypeDesc;
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "TITLE_DESC")
	private String titleDesc;
	
	@Column(name = "CLIENT_NAME")
	private String clientName;
	
	@Column(name = "GENDER")
	private String gender;
	
	@Column(name = "GENDER_DESC")
	private String genderDesc;
	
	@Column(name = "OCCUPATION")
	private String occupation;
	
	@Column(name = "OCCUPATION_DESC")
	private String occupationDesc;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "MOBILE_CODE")
	private String mobileCode;
	
	@Column(name = "MOBILE_NUMBER")
	private String mobileNumber;
	
	@Column(name = "IDENTITY_TYPE")
	private String identityType;
	
	@Column(name = "IDENTITY_TYPE_DESC")
	private String identityTypeDesc;
	
	@Column(name = "ID_NUMBER")
	private String idNumber;
	
	@Column(name = "GST_IDENTIFICATION_NO")
	private String gstIdentificationNo;
	
	@Column(name = "PREFERRED_NOTIFICATION")
	private String preferredNotification;
	
	@Column(name = "TAX_EXCEPTED")
	private String taxExcepted;
	
	@Column(name = "STATUS")
	private String status;

	@Column(name = "STATUS_DESC")
	private String statusDesc;
	
	@Column(name = "STREET")
	private String street;
	
	@Column(name = "COUNTRY")
	private String country;
	
	@Column(name = "COUNTRY_DESC")
	private String countryDesc;
	
	@Column(name = "REGION")
	private String region;
	
	@Column(name = "REGION_DESC")
	private String regionDesc;
	
	@Column(name = "DISTRICT")
	private String district;
	
	@Column(name = "DISTRICT_DESC")
	private String districtDesc;
	
	@Column(name = "POBOX")
	private String pobox;
	
	@Column(name = "INTERME_CODE")
	private String intermeCode;
	
	@Column(name = "INTERME_NAME")
	private String intermeName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LEAD_CREATED_DATE")
	private Date leadCreatedDate;
	
	@Column(name = "CHANNEL_ID")
	private String channelId;
	
	@Column(name = "CHANNEL_DESC")
	private String channelDesc;
	
	@Column(name = "SECTION_TYPE_ID")
	private String sectionTypeId;
	
	@Column(name = "SECTION_TYPE_DESC")
	private String sectionTypeDesc;
	
	@Column(name = "PROPOBABILITY_OF_SUCCESS_ID")
	private String propobabilityOfSuccessId;
	
	@Column(name = "PROPOBABILITY_OF_SUCCESS_DESC")
	private String propobabilityOfSuccessDesc;
	
	@Column(name = "TYPE_OF_BUSINESS_ID")
	private String typeOfBusinessId;
	
	@Column(name = "TYPE_OF_BUSINESS_DESC")
	private String typeOfBusinessDesc;

	@Column(name = "CURRENT_INSURER")
	private String currentInsurer;
	
	@Column(name = "CURRENT_INSURER_DESC")
	private String currentInsurerDesc;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	
	
}
