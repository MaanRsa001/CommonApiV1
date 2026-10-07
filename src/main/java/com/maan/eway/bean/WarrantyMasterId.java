package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WarrantyMasterId implements Serializable {

	private static final long serialVersionUId = 1L;
	private Integer warrantyId;
	private String branchCode;
	private String companyId;
	private Integer amendId;
	private String productId;
	private String sectionId;
	private Integer coverId;
	//private String policyType;
	

}
