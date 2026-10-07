/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class WorkflowTrackingRes {
	
	@JsonProperty("WorkflowId")
	@JsonFormat(shape = Shape.STRING)
	private Long workflowId;
	
	@JsonProperty("CompanyId")
	@JsonFormat(shape = Shape.STRING)
	private Integer companyId;
	
	@JsonProperty("ProductId")
	@JsonFormat(shape = Shape.STRING)
	private Integer productId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("HierarchyLevel")
	private String hierarchyLevel;
	
	@JsonProperty("HierarchyValue")
	@JsonFormat(shape = Shape.STRING)
	private Integer hierarchyValue;
	
	@JsonProperty("ProposalId")
	@JsonFormat(shape = Shape.STRING)
	private Long proposalId;
		
	@JsonProperty("ActionTaken")
	private String actionTaken;
			
	@JsonProperty("ActionDateTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDateTime actionDateTime;
	
	@JsonProperty("ActionRemarks")
	private String actionRemarks;	

	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("TotalPremium")
	@JsonFormat(shape = Shape.STRING)
	private BigDecimal totalPremium;
	
	@JsonProperty("CommissionModifyYN")
	private String commissionModifyYn;
	
	@JsonProperty("CommissionPercent")
	@JsonFormat(shape = Shape.STRING)
	private Double commissionPercent;
	
}
