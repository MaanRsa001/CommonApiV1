package com.maan.eway.bean;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CLAIM_HISTORY_INFO")
@IdClass(ClaimHistoryInfoId.class)
@NoArgsConstructor
@Getter
@Setter
public class ClaimHistoryInfo {
//Entity Primary Key
	@Id
	@Column(name = "COMPANY_ID")
	private Integer companyId;
	
	@Id
	@Column(name = "PRODUCT_ID")
	private Integer productId;

	
	@Column(name = "QUOTE_NO")
	private String quoteNo;
	
	@Id
	@Column(name = "REQUEST_REFERENCE_NO")
	private String requestReferenceNo;
		
	@Id
	@Column(name = "AMEND_ID")
	private Integer amendId;
	
	@Id
	@Column(name = "CLH_SL_NO")
	private Integer clhSlNo;
	
//Other Data Fields	
	@Column(name = "CLH_DATE_OF_LOSS")
	private LocalDate clhDateOfLoss;
	
	@Column(name = "CLH_NATURE_OF_LOSS")
	private String clhNatureOfLoss;
	
	@Column(name = "CLH_CLAIMED_AMOUNT")
	private Double clhClaimedAmount;
	
	@Column(name = "CLH_CLAIM_YEAR")
	private Integer clhClaimYear;
	
	@Column(name = "CLH_ENTRY_DATE")
	private LocalDate clhEntryDate;
	
	@Column(name = "CLH_REMARKS")
	private String clhRemarks;
		
	@Column(name = "EFFECTIVE_DATE_START")
	private LocalDate effectiveDateStart;
	
	@Column(name = "EFFECTIVE_DATE_END")
	private LocalDate effectiveDateEnd;
	
	@Column(name = "STATUS")
	private String status;
	
}
