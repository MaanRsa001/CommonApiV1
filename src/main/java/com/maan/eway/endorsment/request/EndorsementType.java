package com.maan.eway.endorsment.request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EndorsementType {
	@JsonProperty("EndorsementDesc")	
	private String endorsementDesc;
	@JsonProperty("EndtType") 
	private BigDecimal endtType;
	@JsonProperty("EndorsementCategoryDesc") 
	private String endorsementCategoryDesc;
	@JsonProperty("EndorsementCategory") 
	private BigDecimal endorsementCategory;
	@JsonProperty("FieldsAllowed") 
	private List<String> fieldsAllowed;
	@JsonProperty("SectionModificationYn") 
	private String sectionModificationYn;
	@JsonProperty("SectionModificationType") 
	private String sectionModificationType;
	@JsonProperty("isCoverEndt") 
	private String isCoverEndt;
	
	@JsonProperty("EndtShortCode") 
	private String endtShortCode;
	
	@JsonProperty("EndtShortDesc") 
	private String endtShortDesc;
}
