package com.maan.eway.excelupload.bean;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

	
@Entity
@Table(name = "template_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name="EXCEL_ID")
		private Long id;
		@Column(name="EXCEL_NAME")
		private String name;
		@Column(name="COMPANY_ID")
		private String companyId;
		@Column(name="PRODUCT_ID")
		private String productId;
		@Column(name="SECTION_ID")
		private String sectionId;
		@Column(name="COVER_ID")
		private String coverId;
		@Column(name="ENTRY_DATE")
		private LocalDate entryDate;
		@Column(name="EFFECTIVE_START_DATE")
		private LocalDate effectiveStartDate;
		@Column(name="EFFECTIVE_END_DATE")
		private LocalDate effectiveEndDate;
		@Column(name="UPDATE_DATE")
		private LocalDate updateDate;
		@Column(name="AMEND_ID")
		private Integer amendId;
		@Lob
		@Column(name="COLUMN_JSON", columnDefinition = "TEXT")
		private String columnsJson; 
		
		@Column(name="STATUS_TF")
		private String statusTf;
		
		@Column(name="RISK_YN")
		private String riskYn;
		
		@Column(name="TABLE_NAME")
		private String tableName;
		
		@Column(name="COLUMN_NAME_RISK")
		private String columnName;
		
		@Column(name="COUNT_YN")
		private String countYn;
		
		@Column(name="COUNT_COLUMN")
		private String countColumn;
		
	}
