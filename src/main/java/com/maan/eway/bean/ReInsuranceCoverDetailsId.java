package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReInsuranceCoverDetailsId implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String sectionId;
	private String quoteno;
	private Integer riskId;
	private String coverId;
	private String productId;
	private String companyId;

}