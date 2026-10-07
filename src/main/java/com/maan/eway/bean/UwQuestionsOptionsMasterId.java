package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UwQuestionsOptionsMasterId implements Serializable {

	private static final long serialVersionUID = 1L;

	// --- ENTITY KEY ATTRIBUTES

	private String companyId;

	private String branchCode;

	private Integer productId;

	private Integer dependentUwQuestionId;

	private Integer uwQuesOptionId;

	//private String sectionId;

}
