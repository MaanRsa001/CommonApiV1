package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UwQuestionsMasterGetAllReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("QuestionCategory")
	private String questionCategory;

	@JsonProperty("questionCategoryDesc")
	private String questionCategoryDesc;
	
	 @JsonFormat(pattern ="dd/MM/yyyy")
	 @JsonProperty("EffectiveDateStart")
	 private Date effectiveDateStart;

}
