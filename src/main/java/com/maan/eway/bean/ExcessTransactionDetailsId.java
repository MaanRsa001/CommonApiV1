package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ExcessTransactionDetailsId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer excessId;
	private String requestReferenceNo;
	private String productId;
	private String sectionId;
	private String locationId;
	private Integer riskId;

}
