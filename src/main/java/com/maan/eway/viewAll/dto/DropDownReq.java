package com.maan.eway.viewAll.dto;

import java.math.BigDecimal;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DropDownReq {

	private String companyId;

	private Integer productId;

	private Integer sectionId;

	private Integer coverId;
	
	private String productYn;
}
