package com.maan.eway.renewal.req;

import java.util.Date;

import lombok.Data;

@Data
public class EmiDataRequest {

	private String customerName;
	private String quoteNo;
	private String title;
	private String email;
	private String mobileCode;
	private String mobileno;
	private String companyId;
	private String productCode;
	private String sectionCode;
	private String branchCode;
	private String companyName;
	private String instalment;
	private Date dueDate;
	private String dueAmount;

}
