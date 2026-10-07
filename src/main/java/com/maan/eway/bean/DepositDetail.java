package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@ToString
@Table(name = "eway_deposit_detail")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositDetail implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "DEPOSIT_NO")
	private Long depositNo;
	
	@Column(name = "QUOTE_NO")
	private String quoteNo;
	
	@Column(name="CBC_NO")
	private String cbcNo;
	
	@Column(name="PRODUCT_ID")
	private String productId;
	
	@Column(name="PREMIUM_AMOUNT")
	private Double premiumAmount;
	
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name="STATUS")
	private String status;

	
	@Column(name="BALANCE_AMOUNT")
	private Double balanceAmount;
	
	@Column(name="RECEIPT_NO")
	private String receiptNo;
	
	@Column(name="BROKER_ID")
	private Long brokerId;
	
	@Column(name="PREMIUM")
	private Double premium;
	
	@Column(name="POLICY_INSURANCE_FEE")
	private Double policyInsuranceFee;
	
	@Column(name="VAT_AMOUNT")
	private Double vatAmount;
	
	@Column(name="CHARGABLE_TYPE")
	private String chargableType;
	
	@Column(name = "BROKER_NAME")
	private String brokerName;
	
	@Column(name = "PRODUCT_NAME")
	private String productName;
	
	@Column(name = "DEPOSIT_TYPE")
	private String depositType;
	
	@Column(name="CUSTOMER_ID")
	private String customerId;

}
