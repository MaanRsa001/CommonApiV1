package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REPORT_CONFIG_MASTER")
@Builder
public class ReportJasperConfigMaster {
	
	
	@EmbeddedId
	private ReportJasperConfigMasterId id;
	
	
	@Column(name="REPORT_TYPE")
	private String reportType;
	
	@Column(name ="JASPER_NAME")
	private String jasperName;

	@Column(name ="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name ="EFFECTIVE_DATE_START")
	private Date effectiveDateStart;

	@Column(name ="JASPER_PATH")
	private String jasperPath;

	@Column(name ="EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;

	@Column(name ="STATUS")
	private String status;

	@Column(name ="CREATED_BY")
	private String createdBy;

	@Column(name ="UPDATED_DATE")
	private Date updatedDate;

	@Column(name ="SUB_JASPER_YN")
	private String subJasperYn;
	
	@Column(name ="SUB_JASPER_NAME")
	private String subJasperName;

	
	
}

