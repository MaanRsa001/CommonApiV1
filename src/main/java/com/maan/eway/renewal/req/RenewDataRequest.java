package com.maan.eway.renewal.req;

import java.util.Date;

import lombok.Data;

@Data
public class RenewDataRequest {

	private String customerName;
	private String policyNo;
	private String transId;
	private String email;
	private String mobileCode;
	private String mobileno;
	private String companyId;
	private String companyName;
	private String registrationNo;
	private Date expiryDate;
	private String lastNotifyYN;

}
