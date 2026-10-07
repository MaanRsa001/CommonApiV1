package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@ToString
@IdClass(MultiplePolicyDrCrDetailId.class)
@Table(name = "multiple_policy_drcr_detail")
public class MultiplePolicyDrCrDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "RISK_ID",nullable = false)
	private Integer riskId;
	
	@Id
	@Column(name = "POLICY_NO",nullable = false)
	private String policyNo;
	
	@Id
	@Column(name = "QUOTE_NO",nullable = false)
	private String quoteNo;
	
	@Column(name = "CHARGE_CODE")
	private BigDecimal chargeCode;
	
	@Id
	@Column(name = "CHG_ID",nullable = false)
	private BigDecimal chgId;
	
	@Column(name = "DOC_NO")
	private String docNo;
	
	@Column(name = "DOC_TYPE")
	private String docType;
	
	@Column(name = "DOC_ID")
	private String docId;
	
	@Column(name = "AMOUNT_LC")
	private Double amountLc;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Id
	@Column(name = "COMPANY_ID",nullable = false)
	private String companyId;
	
	@Id
	@Column(name = "PRODUCT_ID",nullable = false)
	private String productId;
	
	@Id
	@Column(name = "BRANCH_CODE",nullable = false)
	private String branchCode;
	
	@Column(name = "DRCR_FLAG")
	private String drcrFlag;
	
	@Column(name = "AMOUNT_FC",nullable = false)
	private BigDecimal amountFc;
	
	@Id
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "CHARGE_ACCOUNT_DESC")
	private String chargeAccountDesc;
	
	@Column(name = "NARRATION")
	private String narration;
	
	@Column(name = "DISPLAY_ORDER")
	private Integer displayOrder;
	
	@Column(name = "ERROR_DESC")
	private String errorDesc;
	
	@Column(name = "TYPE")
	private String type;
	
	

}
