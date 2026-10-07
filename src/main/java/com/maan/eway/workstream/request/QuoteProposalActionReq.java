/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuoteProposalActionReq {
//Common Info
	@JsonProperty("CompanyId")
	@JsonFormat(shape = Shape.STRING)
	private Integer companyId;
	
	@JsonProperty("ProductId")
	@JsonFormat(shape = Shape.STRING)
	private Integer productId;

//Proposal Info
	@JsonProperty("ProposalId")
	@JsonFormat(shape = Shape.STRING)
	private Long proposalId;
	
//Approver Info
	@JsonProperty("LoginId")
	private String loginId;

//Action Info
	@JsonProperty("ActionTaken")
	private String actionTaken;	
	
	@JsonProperty("ActionRemarks")
	private String actionRemarks;	
	
	@JsonProperty("TotalPremium")
	@JsonFormat(shape = Shape.STRING)
	private BigDecimal totalPremium;
	
	@JsonProperty("CommissionModifyYN")
	private String commissionModifyYn;
	
	@JsonProperty("CommissionPercent")
	@JsonFormat(shape = Shape.STRING)
	private Double commissionPercent;
}
