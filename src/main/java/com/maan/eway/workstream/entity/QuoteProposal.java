/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workflow_quote_proposals")
@IdClass(QuoteProposalPK.class)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class QuoteProposal {
	
	@Id
	@Column(name = "COMPANY_ID", nullable = false)
	private Integer companyId;
	
	@Id
	@Column(name = "PRODUCT_ID", nullable = false)
	private Integer productId;
	
	@Id
	@Column(name = "PROPOSAL_ID", nullable = false)
	private Long proposalId;
	
	@Column(name = "CUSTOMER_REFERENCE_NO", nullable = false)
	private String customerReferenceNo;
	
	@Column(name = "REQUEST_REFERENCE_NO", nullable = false)
	private String requestReferenceNo;
	
	@Column(name = "QUOTE_NO")
	private String quoteNo;
	
	@Column(name = "CLIENT_NAME")
	private String clientName;
	
	@Column(name = "POLICY_START_DATE")
	private LocalDate policyStartDate;
	
	@Column(name = "POLICY_END_DATE")
	private LocalDate policyEndDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
		
	@Column(name = "SUM_INSURED")
	private BigDecimal sumInsured;
	
	@Column(name = "PROPOSAL_STATUS", length = 2)
	private String proposalStatus;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;			
	
	@Column(name = "FINALIZED_ON")
	private LocalDateTime finalizedOn;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "LAST_ACTION_BY")
	private String lastActionBy;
	
	@Column(name = "LAST_ACTION_ON")
	private LocalDateTime lastActionOn;
	
	@OneToMany
	@JoinColumns({
		@JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID"),
		@JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"),
		@JoinColumn(name = "PROPOSAL_ID", referencedColumnName = "PROPOSAL_ID")
	})
	private List<WorkflowTracking> workflowRef;
	
}
