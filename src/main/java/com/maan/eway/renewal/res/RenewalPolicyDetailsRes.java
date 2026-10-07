package com.maan.eway.renewal.res;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenewalPolicyDetailsRes {

	private String     oldrequestreferenceNo ;
	private String     oldpolicyNo ;
    private String     serviceType ;
    private String     oldquoteNo ;
    private Date     oldstartDate ;
    private Date       oldendDate ;
    private String    title ;
    private String    customerName ;
    private String    gender ;
    private String    occupation ;
    private String    mobileCode ;
    private String    mobileNo ;
    private String    emailId ;
    private String    identityType ;
    private String    identityNumber ;
    private String    preferedNotification ;
    private String    taxExcemption ;
    private String    street ;
    private String    country ;
    private String    region ;
    private String    district ;
    private String    pobox ;
    private Integer    noofVehicle ;
    private Date    newstartDate ;
    private Date    newendDate ;
    private String    loginId ;
    private String    applicationId ;
    private String    companyId ;
    private Integer    productCode ;
    private Integer    sectionCode ;
    private String    branchCode ;
    private String    branchName ;
    private String    policyHolderType ;
    private String    policyHolderTypeid ;
    private BigDecimal previousPolicyPremium;
    private Integer daysLeft;
    private String customerReferenceNo;
   
}

