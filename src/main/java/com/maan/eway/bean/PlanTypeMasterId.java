package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanTypeMasterId  implements Serializable{

	private static final long serialVersionUID=1L;
	
	private Integer planTypeId;
	private String branchCode;
	private String companyId;
	private Integer amendId;
	private String sectionId;
	private String productId;
   
}
