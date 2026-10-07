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
@Builder
@DynamicInsert
@DynamicUpdate
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eway_deposit_cbc_master")
public class DepositcbcMaster implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="CBC_NO")
	private String cbcNo;
	
	@Column(name="BROKER_ID")
	private String brokerId;
	
	@Column(name="PRODUCT_ID")
	private String productId;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="DEPOSIT_AMOUNT")
	private Double depositAmount;
	
	@Column(name="DEPOSIT_UTILISED")
	private Double depositUtilized;
	
	@Column(name="REFUND_AMOUNT")
	private Double refundAmount;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="POLICY_REFUND_AMOUNT")
	private Double policyrefundamount;
	
	@Column(name = "BROKER_NAME")
	private String brokerName;
	
	@Column(name = "PRODUCT_NAME")
	private String productName;
	
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name="CUSTOMER_ID")
	private String customerId;
	
	@Column(name="COMPANY_ID")
	private String companyId;
}
