/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workflow_tracking")
@IdClass(WorkflowTrackingPK.class)
@NoArgsConstructor
@Setter
@Getter
public class WorkflowTracking {
	
	@Id
	@Column(name = "WORKFLOW_ID")
	private Long workflowId;
	
	@Id
	@Column(name = "COMPANY_ID", nullable = false)
	private Integer companyId;
	
	@Id
	@Column(name = "PRODUCT_ID", nullable = false)
	private Integer productId;
	
	@Id
	@Column(name = "PROPOSAL_ID", nullable = false)
	private Long proposalId;

	@Column(name = "LOGIN_ID")
	private String loginId;
	
	@Column(name = "CUSTOMER_REFERENCE_NO", nullable = false)
	private String customerReferenceNo;
	
	@Column(name = "REQUEST_REFERENCE_NO", nullable = false)
	private String requestReferenceNo;
	
	@Column(name = "QUOTE_NO")
	private String quoteNo;
		
	@Column(name = "HIERARCHY_LEVEL")
	private String hierarchyLevel;
	
	@Column(name = "HIERARCHY_VALUE")
	private Integer hierarchyValue;
	
	@Column(name = "ACTION_TAKEN", length = 2)
	private String actionTaken;
			
	@Column(name = "ACTION_TAKEN_ON")
	private LocalDateTime actionTakenOn;
	
	@Column(name = "ACTION_REMARKS")
	private String actionRemarks;	
	
	@Column(name = "TOTAL_PREMIUM")
	private BigDecimal totalPremium;
	
	@Column(name = "COMMISSION_MODIFY_YN", length = 1)
	private String commissionModifyYn;
	
	@Column(name = "COMMISSION_PERCENT")
	private Double commissionPercent;
	
}
