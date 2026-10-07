package com.maan.eway.viewAll.entity;



import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(EmployeeInfoId.class)
public class EmployeeInfo {

	@Id
	@Column(name = "EMP_ID")
	private Long empId;

	@Column(name = "REQUEST_REFERENCE_NO", length = 20)
	private String requestReferenceNo;

	@Column(name = "EMPLOYEE_NAME")
	private String employeeName;

	@Column(name = "VISA_NO")
	private String visaNo;

	@Column(name = "VISA_EXPIRY_DATE")
	@Temporal(TemporalType.DATE)
	private Date visaExpiryDate;

	@Id
	@Column(name = "PASSPORT_NO")
	private String passportNo;

	@Column(name = "PASSPORT_EXPIRY_DATE")
	@Temporal(TemporalType.DATE)
	private Date passportExpiryDate;

	@Column(name = "NATIONALITY")
	private String nationality;

	@Column(name = "DATE_OF_BIRTH")
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@Column(name = "GENDER")
	private String gender;

	@Column(name = "VISA_ISSUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date visaIssueDate;

	@Column(name = "COUNTRY_ENTRY_DATE")
	@Temporal(TemporalType.DATE)
	private Date countryEntryDate;

	@Column(name = "MOBILE_NO")
	private String mobileNo;

	@Column(name = "CURRENT_HOUSE_LOCATION")
	private String currentHouseLocation;

	@Column(name = "HOUSE_NO")
	private Integer houseNo;

	@Column(name = "GOVERNORATE")
	private String governorate;

	@Column(name = "CITY")
	private String city;

	@Column(name = "COUNTRY")
	private String country;

	@Id
	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@Id
	@Column(name = "CUSTOMER_REFERENCE_NO")
	private String customerReferenceNo;

	@Id
	@Column(name = "LOGIN_ID")
	private String loginId;

	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "ENTRY_DATE")
	@Temporal(TemporalType.DATE)
	private Date entryDate;

	@Column(name = "COMPANY_ID")
	private String companyId;

	@Column(name = "ID_OR_VISA")
	private String idOrVisa;

	@Column(name = "OCCUPATION")
	private String occupation;

	@Column(name = "EMAIL_ID")
	private String emailId;
	

	@Column(name= "TYPE_OF_CONTRACTOR")
	private String typeOfContractor;
	
	@Column(name = "OCCUPATION_DESC")
	private String occupationDesc;
	
	@Column(name = "NATIONALITY_DESC")
	private String nationalityDesc;

}
