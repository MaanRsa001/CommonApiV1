package com.maan.eway.crm.bean;

import lombok.Data;

@Data
public class CompanyProductRes {
	
	private String companyId;
	private Integer productId;
	private String productName;
	private String status;
	
	private String sectionId;
	private String sectionName;

}
