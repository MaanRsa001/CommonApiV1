package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerProductListReq {

	@JsonProperty("ProductId")
	private String productId ;
	@JsonProperty("SuminsuredStart")
	private String  suminsuredStart; 
	
	@JsonProperty("SuminsuredEnd")
	private String  suminsuredEnd; 
	
	
	@JsonProperty("ReferralIds")
	private List<String> referralIds ;
	
	@JsonProperty("EndorsementIds")
	private List<String> endorsementIds ;
	
	@JsonProperty("ColumnName")
	private String  columnName; 

    /**
     * Represents Fields Related To WorkflowTracking
     * @attributes: hierarchy_level, hierarchy_value, can_escalate, can_finalize 
     * @since : 08-01-2025
     */
	@JsonProperty("WorkflowYN")
	private String workflowYn;
	
	@JsonProperty("BackDays")
    private Integer backDays;
	
	@JsonProperty("HierarchyValue")
	@JsonFormat(shape = Shape.STRING)
	private Integer hierarchyValue;
	
	@JsonProperty("HierarchyLevel")
	private String hierarchyLevel;
			
	@JsonProperty("CanFinalize")
	@JsonFormat(shape = Shape.STRING)
	private Boolean canFinalize;
	
	@JsonProperty("CanEscalate")
	@JsonFormat(shape = Shape.STRING)
	private Boolean canEscalate;	
	
}
