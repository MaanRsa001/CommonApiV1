package com.maan.eway.chartaccount;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
public class ChartAccountChildMasterId implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="COMPANY_ID")
	private Integer companyId;
	
	@Column(name="PRODUCT_ID")
	private Integer productId;
	
	@Column(name="SECTION_ID")
	private Integer sectionId;
	
	@Column(name="CHART_ID")
	private Integer chartId;
	
	@Column(name="COVER_ID")
	private Integer coverId;
	
	@Column(name="AMEND_ID")
	private Integer amendId;

}
