package com.maan.eway.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DamagePartsDetailsId {
	
	private Long damageId;
	
	private String uniqueId;

	private Integer documentId;
	
	private String quoteNo;

}
