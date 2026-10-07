package com.maan.eway.chartaccount;

import java.util.Date;

import groovy.transform.ToString;
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
@ToString
@Builder
@Entity
@Table(name = "chart_parent_master")
public class ChartParentMaster {

	@EmbeddedId
	private ChartParentMasterId chatParentId;
	
	@Column(name = "CHART_ACCOUNT_CODE")
	private Integer chartAccountCode;
	
	@Column(name = "CHART_ACCOUNT_DESC")
	private String chartAccountDesc;
	
	@Column(name = "NARATION_DESC")
	private String narationDesc;
	
	@Column(name = "ACCOUNT_TYPE")
	private String accountType;
	
	@Column(name = "CHARACTERSTIC_TYPE")
	private String charactersticType;
	
	@Column(name = "DISPLAY_ORDER")
	private Integer displayOrder;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "EFFECTIVE_START_DATE")
	private Date effectiveStartDate;
	
	@Column(name = "EFFECTIVE_END_DATE")
	private Date effectiveEndDate;
	
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	
	@Column(name = "TYPE")
	private String type;
	
}
