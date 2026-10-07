package com.maan.eway.excelupload.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateCreateRequest {
	
	@NotBlank(message = "ExcelName is mandatory")
	@JsonProperty("ExcelName")
	private String name;
	
	@NotBlank(message = "CompanyId is mandatory")
	@JsonProperty("CompanyId")
	private String companyId;
	
	@NotBlank(message = "ProductId is mandatory")
	@JsonProperty("ProductId")
	private String productId;
	
	@NotNull(message = "SectionId is mandatory")
	@JsonProperty("SectionId")
	private String sectionId;
	
	@NotNull(message = "CoverId is mandatory")
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("EffectiveStartDate")
	private LocalDate effectiveStartDate;
	
	@NotNull(message = "RiskYN is mandatory")
    @JsonProperty("RiskYN")
    private String riskYn;          
    
    @JsonProperty("TableName")
    private String tableName;      

    @JsonProperty("ColumnName")
    private String columnName;     
	
	@NotNull(message = "ColumnList must not be null")
    @Size(min = 1, message = "ColumnList must have at least one column")
    @Valid
	@JsonProperty("ColumnList")
	private List<ColumnDef> columns;
}
