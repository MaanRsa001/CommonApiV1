package com.maan.eway.viewAll.entity;

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
public class RiskInfoPdfId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer productId;

	private String quoteNo;
	
	 private String brokerQuotationyn;

}
