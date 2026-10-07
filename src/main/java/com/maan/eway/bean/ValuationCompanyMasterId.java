package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ValuationCompanyMasterId implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	Long sNo;
	
	String companyId;
	
	String valCompanyCode;
	
	String branchCode;
	
     Integer amendId;
}
