package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NonMotorHPMres {

 	// Customer Info
	//private Integer   idsCount ;
    private String   customerReferenceNo ;
    private String    clientName;
	private String     companyId ;
	private Integer     productId ;
	private String     productName ;
	
	private String     branchCode ;
	
	private String   requestReferenceNo ;
	private String quoteNo;
	private String customerId;
	private Timestamp  policyStartDate;
	private Timestamp  policyEndDate;
	private BigDecimal overallPremiumLc;
	private BigDecimal overallPremiumFc;
	private String currency;
	private String savedFrom;
	
}
