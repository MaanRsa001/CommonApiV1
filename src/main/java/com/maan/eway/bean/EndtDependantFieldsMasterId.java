package com.maan.eway.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class EndtDependantFieldsMasterId implements Serializable {

	private static final long serialVersionUID =1L;
	
	private Integer dependantFieldId;
	
	private String companyId;
	
	private String productId;
}
