package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class IntegrationMappingDetailsMasterGetReq {
	
	@JsonProperty("CompanyId")
	@JsonFormat(shape = Shape.STRING)
	private Integer companyId;
	
	@JsonProperty("SectionId")
	@JsonFormat(shape = Shape.STRING)
	private Integer sectionId;
	
	@JsonProperty("ProductId")
	@JsonFormat(shape = Shape.STRING)
	private Integer productId;
	
	@JsonProperty("PolicyTypeId")
	@JsonFormat(shape = Shape.STRING)
	private Integer policyTypeId;
	
	@JsonProperty("IntegrationId")
	@JsonFormat(shape = Shape.STRING)
	private Long integrationId;
		
}
