/**
 * @author : Ashok Kumar S 
 * @since  : 09-01-2025
 */
package com.maan.eway.workstream.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WorkflowGetReq {
	
	@JsonProperty("CompanyId")
	private Integer companyId;
	
	@JsonProperty("ProductId")
	private Integer productId;
	
	@JsonProperty("ProposalId")
	@JsonFormat(shape = Shape.STRING)
	private Long proposalId;	

}
