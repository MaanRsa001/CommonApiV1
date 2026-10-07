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
public class ProductBenefitMasterId implements Serializable {

	private static final long serialVersionUId = 1L;
	
	private Integer benefitId ;
	private Integer typeId;
	private String companyId;
	private String productId;
	private String sectionId;
	private Integer amendId;
	
}
