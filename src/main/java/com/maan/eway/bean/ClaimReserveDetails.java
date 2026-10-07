package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_reserve_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimReserveDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reserve_id")
	private Integer reserveId;

	@Column(name = "claim_no")
	private String claimNo;

	@Column(name = "policy_no")
	private String policyNo;

	@Column(name = "product_id")
	private String productId;

	@Column(name = "location_name")
	private String locationName;

	@Column(name = "section_name")
	private String sectionName;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "location_id")
	private String locationId;
	
	@Column(name = "risk_id")
	private String riskId;

	@Column(name = "section_id")
	private String sectionId;

	@Column(name = "cover_id")
	private String coverId;

	@Column(name = "reserve_amount")
	private String reserveAmount;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "reserve_type_id")
	private String reserveTypeId;

	@Column(name = "reserve_type")
	private String reserveType;

	@Column(name = "status")
	private String status;

	@Column(name = "cover_name")
	private String coverName;

	@Column(name = "reserve_refno")
	private String reserveRefno;

	//@Temporal(TemporalType.DATE)
	@Column(name = "entry_date")
	private LocalDateTime entryDate;

	//@Temporal(TemporalType.DATE)
	@Column(name = "reserve_Date")
	private LocalDateTime reserveDate;
	
	@Column(name = "reserve_master_name")
	private String reserveMasterName;
	
	@Column(name = "reserve_master_id")
	private String reserveMasterId;
	
	@Column(name = "payment_status")
	private String paymentStatus;
	
	@Column(name = "payment_amount")
	private BigDecimal paymentAmount;
	
	@Column(name = "count")
	private Integer count;
	
	@Column(name = "prev_reserve_id")
	private Integer prevReserveId;
	
	@Column(name = "closed_yn")
	private String closedYN;
	
	//@Temporal(TemporalType.DATE)
	@Column(name = "closed_date")
	private LocalDateTime closedDate;
	
	@Column(name = "revised_reserve_amount")
	private String revisedAmount;
	
	@Column(name = "revised_reserve_amount_lc")
	private String revisedAmountLc;

	@Column(name = "reserve_indicator_id")
	private String reserveIndicatorId;

	@Column(name = "balance_amount")
	private BigDecimal balanceAmount;
	
	@Column(name = "updatedby")
	private String updatedby;
	
	@Column(name = "update_date")
	private LocalDateTime updateDate;
	
	@Column(name = "closedby")
	private String closedby;
	
	@Column(name = "createdby")
	private String createdby;
	
	@Column(name = "reserve_amount_lc")
	private String reserveAmountLc;
	
	@Column(name = "currency_code")
	private String currencyCode;
	
	@Column(name = "closed_reason")
	private String closedReason;

	@Column(name = "reserve_cust_code")
	private String reserveCustCode;

	@Column(name = "reserve_cust_name")
	private String reserveCustName;
}
