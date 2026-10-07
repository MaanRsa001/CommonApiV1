package com.maan.eway.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ClaimHistoryInfoId {

	private Integer companyId;	
	
	private Integer productId;	
	
	//private String quoteNo;	
	
	private String requestReferenceNo;	
	
	private Integer clhSlNo;
	
	private Integer amendId;
	
}
