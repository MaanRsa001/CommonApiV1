package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverSubCoverChangeStatusReq {

	@JsonProperty("InsuranceId")
	 private String companyId;
	 
	 @JsonProperty("ProductId")
	 private String productId;
	 
	 @JsonProperty("SectionId")
	 private String sectionId;
	 
	 @JsonProperty("CoverId")
	 private String coverId;
	 
	 @JsonProperty("SubCoverId")
	 private String subCoverId;
	 
	 @JsonProperty("Status")
	 private String status;
	 
	 @JsonFormat(pattern ="dd/MM/yyyy")
	 @JsonProperty("EffectiveDateStart")
	 private Date effectiveDateStart;
}
