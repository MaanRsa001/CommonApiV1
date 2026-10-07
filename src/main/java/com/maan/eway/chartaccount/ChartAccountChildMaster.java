package com.maan.eway.chartaccount;

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
@Table(name="chartaccount_child_master")
@Builder
public class ChartAccountChildMaster {
	
	@EmbeddedId
	private ChartAccountChildMasterId id;
	
	@Column(name ="ACCOUNT_TYPE")
	private String accountType;
	
	@Column(name ="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name ="EFFECTIVE_START_DATE")
	private Date effectiveStartDate;
	
	@Column(name ="EFFECTIVE_END_DATE")
	private Date effectiveEndDate;
	
	@Column(name ="STATUS")
	private String status;
	
	@Column(name ="UPDATED_BY")
	private String updatedBy;
	
	@Column(name ="UPDATED_DATE")
	private Date updatedDate;
	
}
