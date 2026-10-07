package com.maan.eway.admin.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.workstream.response.HierarchyRes;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class IssuerProductGetRes {


	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("ProductDesc")
	private String productDesc;

	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("EndorsementIds")
	private List<String> endorsementIds;
	
	@JsonProperty("ReferralIds")
	private List<String> referralIds;

	@JsonProperty("IsOptedYn")
	private String isOptedYn;	

	@JsonProperty("ColumnName")
	private String columnName;	

	@JsonProperty("TableName")
	private String tableName;	

	/**
     * Represents Fields Related To WorkflowTracking
     * @attributes: hierarchy_level, hierarchy_value, can_escalate, can_finalize 
     * @since : 09-01-2025
     */
	@JsonProperty("WorkflowYN")
	private String workflowYn;
	
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
	
	@JsonProperty("BackDays")
    private Integer backDays;
    
	@JsonProperty("AllAvailableHierarchy")
	private List<HierarchyRes> allAvailableHierarchy;

}
