/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.response;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class QuoteProposalRes {
	
	@JsonProperty("CompanyId")
	@JsonFormat(shape = Shape.STRING)
	private Integer companyId;
	
	@JsonProperty("ProductId")
	@JsonFormat(shape = Shape.STRING)
	private Integer productId;
	
	@JsonProperty("ProposalId")
	@JsonFormat(shape = Shape.STRING)
	private Long proposalId;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("PolicyStartDate")
	@JsonFormat(pattern = "dd/MM/yyyy", shape = Shape.STRING)
	private LocalDate policyStartDate;
	
	@JsonProperty("PolicyEndDate")
	@JsonFormat(pattern = "dd/MM/yyyy", shape = Shape.STRING)
	private LocalDate policyEndDate;
		
	@JsonProperty("SumInsured")
	@JsonFormat(shape = Shape.STRING, pattern = "0")
	private BigDecimal sumInsured;
	
	@JsonProperty("ProposalStatus")
	private String proposalStatus;

	@JsonProperty("CreatedOn")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDateTime createdOn;			
				
	@JsonProperty("FinalizedOn")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDateTime finalizedOn;
		
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("LastActionBy")
	private String lastActionBy;
	
	@JsonProperty("LastActionOn")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDateTime lastActionOn;
	
	//This is not in Proposal Entity is Taken from WorkflowTracking Entity
	@JsonProperty("ActionTakenOn")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDateTime actionTakenOn;
	
}
