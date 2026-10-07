package com.maan.eway.bean;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ReportJasperConfigMasterId implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="COMPANY_ID")
	private String companyId;
	
	
	@Column(name="PRODUCT_ID")
	private Integer productId;
	
	
	@Column(name="AMEND_ID")
	private Integer amendId;
	
	
	@Column(name="REPORT_ID")
	private Integer reportId;
	


}
