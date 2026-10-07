package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="payment_master")
@IdClass(PaymentMasterId.class)
public class PaymentMaster {

	@Id
	@Column(name="PAYMENT_MASTER_ID",nullable=false)
	private Integer paymentMasterId;
	
	@Id
	@Column(name="BRANCH_CODE",length=20, nullable=false)
	private String branchCode;
	
	@Id
	@Column(name="COMPANY_ID",length=20, nullable=false)
	private String companyId;

	@Id
	@Column(name="PRODUCT_ID",length=20, nullable=false)
	private Integer productId;
	
	@Id
	@Column(name="AMEND_ID",nullable=false)
	private Integer amendId;

	@Id
	@Column(name="USER_TYPE",length=20)
	private String userType;
	
	@Id
	@Column(name="SUB_USER_TYPE",length=20)
	private String subUserType;
	
	@Id
	@Column(name="AGENCY_CODE",length=100)
	private String agencyCode;
	
	
//	@Id
	@Column(name="OA_CODE",length=100)
	private String oaCode;
	
	@Column(name="CASH_YN",length=20)
	private String cashYn;
	
	@Column(name="CREDIT_YN",length=20)
	private String creditYn;
	
	
	@Column(name="CHEQUE_YN",length=20)
	private String chequeYn;
	
	@Column(name="ONLINE_YN",length=20)
	private String onlineYn;
	
	@Column(name="MOBILE_PAYMENT_YN",length=20)
	private String mobilePaymentYn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_START",nullable=false)
	private Date effectiveDateStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_END",nullable=false)
	private Date effectiveDateEnd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	
	@Column(name="STATUS",length=1)
	private String status;
	
	@Column(name="CREATED_BY",length=100)
	private String createdBy;
	
	@Column(name="UPDATED_BY",length=100)
	private String updatedBy;
	
	@Column(name="REALPAY_YN",length=20)
	private String realpayYn;

	@Column(name="EFT_YN",length=20)
	private String eftYn;
}
