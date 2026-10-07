package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="MARINE_PERSONAL_INFO")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfoMarine {

	@Id
	@Column(name = "CUSTOMER_ID")
	private Long customerid;
	@Column(name = "APPLICATION_ID")
	private String applicationid;
	private String title;
	@Column(name = "first_name")
	private String firstname;
	private String last_name;
	private String nationality;
	private Date dob;
	private String gender;
	private String telephone;
	private String mobile;
	private String fax;
	private String email;
	private String address1;
	private String address2;
	private String occupation;
	private String pobox;
	private String country;
	private String emirate;
	private String emirate_name;
	@Column(name = "AMEND_ID")
	private Long amendid;
	private Date inception_date;
	private Date expiry_date;
	private Date effective_date;
	private Date entry_date;
	private String remarks;
	private String status;
	@Column(name = "LOGIN_ID")
	private String loginid;
	private Long ac_executive_id;
	@Column(name = "agency_code")
	private String agencyCode;
	private String oa_code;
	@Column(name = "company_name")
	private String companyname;
	@Column(name = "missippi_customer_code")
	private String missippicustomercode;
	//private String city;
	private String freight_forward_user;
	private String customer_login_id;
	private String customer_source;
	private String fd_code;
	private String client_customer_id;
	private String cust_ar_no;
	private String cust_name;
	private String passport_number;
	private String nrc;
	private String customer_type;
	private String company_reg_no;
	private String cust_name_arabic;
	private String dob_ar;
	private String alternate_mobile;
	private String ins_pocode;
	private String building_no;
	private String additional_no;
	private String sales_userid;
	private String ins_city;
	//private String zipcode;
	private String neighborhood;
	private String unitno;
	private String street;
	private String address_identifier;
	private String ins_pobox;
	private String waseel_buildingno;
	private String waseel_additional_no;
	private String waseel_zipcode;
	private String waseel_city;
	private String waseel_neighborhood;
	private Long insuredid_ver_type;
	private String policy_max_liability;
	private String driver_age_limit;
	private String expire_gredate;
	private String effective_gredate;
	private String neworrenew;
	private String insured_id;
	private String usertype;
	private String issue_gredate;
	private String quote_type;
	private String webdatayn;
	private String district;
	@Column(name="VAT_REG_NO")
	private String vatregno;
	private String cust_vat_yn;
	private String middle_name;
	private String cust_mid_name;
	private String cust_last_name;
	private String cust_mid_name_arabic;
	private String cust_last_name_arabic;
	private String education;
	@Column(name="BRANCH_CODE")
	private String branchcode;
	private String region_code;
	private String city;
	//private String wrsc_yn;
	
	private String cust_flag;
	private String trnNo;
	private String contactNo;
	private String civilId;
	private String passportNo;
	private String emirateId;
	private String custIdentityType;

}
