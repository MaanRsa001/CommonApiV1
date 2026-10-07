package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultiplePolicyDrCrDetailId implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer riskId;

	private String policyNo;

	private String quoteNo;

	private BigDecimal chgId;

	private String productId;

	private String branchCode;
	
	private String status;
	private String companyId;

}
