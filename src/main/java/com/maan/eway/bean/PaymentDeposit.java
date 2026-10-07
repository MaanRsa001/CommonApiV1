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
@ToString
@DynamicInsert
@DynamicUpdate
@Table(name = "eway_payment_deposit")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDeposit implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "DEPOSIT_NO")
	private Long depositNo;
	
	@Column(name = "CBC_NO")
	private String cbcNo;
	
	@Column(name = "QUOTE_NO")
	private String quoteNo;
	
	@Column(name = "PAYMENT_TYPE")
	private String paymentType;
	
	@Column(name = "PAYMENT_TYPEDESC")
	private String paymentTypeDesc;
	
	@Column(name = "PREMIUM")
	private Double premium;
	
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CHEQUE_NO")
	private String chequeNo;
	
	@Column(name = "CHEQUE_DATE")
	private Date chequeDate;
	
	@Column(name = "ACCOUNT_NUMBER")
	private String AccountNo;
	
	@Column(name = "IBAN_NUMBER")
	private String ibanNumber;
	
	@Column(name = "MICRNO")
	private String micrno;
	
	@Column(name = "PAYEENAME")
	private String payeeName;
	
	@Column(name = "REFERENCE_NO")
	private String referenceNo;
	
}
