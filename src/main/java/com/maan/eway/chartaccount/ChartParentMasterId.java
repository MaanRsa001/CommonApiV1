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
@Builder
@Embeddable
public class ChartParentMasterId implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "CHART_ID")
	private Integer chartId;
	
	@Column(name = "COMPANY_ID")
	private Integer companyId;
	
	@Column(name = "AMEND_ID")
	private Integer amendId;
	
}
