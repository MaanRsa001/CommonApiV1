package com.maan.eway.excelupload.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateGetResponse {

	@JsonProperty("TemplateId")
    private Long        templateId;
	@JsonProperty("ExcelName")
    private String      excelName;  
	@JsonProperty("CompanyId")
    private String      companyId;
	@JsonProperty("ProductId")
    private String      productId;
	@JsonProperty("SectionId")
    private String     sectionId;
	@JsonProperty("CoverId")
    private String     coverId;
	@JsonProperty("Status")
    private String    active;
	@JsonProperty("EffectiveStartDate")
	private LocalDate effectiveStartDate;
	
	@JsonProperty("ColumnList")
    private List<ColumnDef> columnList; 
}
