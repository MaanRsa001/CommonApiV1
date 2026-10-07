package com.maan.eway.workstream.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class WorkflowTrackingPK {	
	private Long workflowId;
	private Integer companyId;
	private Integer productId;
	private Long proposalId;
}
