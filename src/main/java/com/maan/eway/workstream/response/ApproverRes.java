/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ApproverRes {
	
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
	
	@JsonProperty("CanFinalize")
	@JsonFormat(shape = Shape.STRING)
	private Boolean canFinalize;
	
	@JsonProperty("CanEscalate")
	@JsonFormat(shape = Shape.STRING)
	private Boolean canEscalate;
	
	@JsonProperty("SumInsuredStart")
	@JsonFormat(shape = Shape.STRING)
	private BigDecimal sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	@JsonFormat(shape = Shape.STRING)
	private BigDecimal sumInsuredEnd;
	
}
