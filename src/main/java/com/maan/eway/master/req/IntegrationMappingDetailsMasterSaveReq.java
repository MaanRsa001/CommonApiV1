package com.maan.eway.master.req;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class IntegrationMappingDetailsMasterSaveReq {
		
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
		
	@JsonProperty("CoreSectionCode")
	@JsonFormat(shape = Shape.STRING)
	private Integer coreSectionCode;
	
	@JsonProperty("CoreSectionDesc")
	private String coreSectionDesc;
	
	@JsonProperty("CoreProductCode")
	@JsonFormat(shape = Shape.STRING)
	private Integer coreProductCode;
	
	@JsonProperty("CoreProductDesc")
	private String coreProductDesc;
	
	@JsonProperty("EffectiveDateStart")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate effectiveDateStart;
		
	@JsonProperty("CreatedBy")
	private String createdBy;
		
	@JsonProperty("Status")
	private String Status;

}
