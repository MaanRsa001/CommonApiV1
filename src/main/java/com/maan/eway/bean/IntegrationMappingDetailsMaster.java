package com.maan.eway.bean;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "INTEGRATION_MAPPING_DETAIL")
@IdClass(IntegrationMappingDetailsMasterId.class)
@NoArgsConstructor
@Getter
@Setter
public class IntegrationMappingDetailsMaster {
	
	//--Entity Primary Key
	@Id
	@Column(name = "COMPANY_ID", nullable = false)
	private Integer companyId;
	
	@Id
	@Column(name = "SECTION_ID", nullable = false)
	private Integer sectionId;
	
	@Id
	@Column(name = "PRODUCT_ID", nullable = false)
	private Integer productId;
	
	@Id
	@Column(name = "POLICY_TYPE_ID", nullable = false)
	private Integer policyTypeId;
	
	@Id
	@Column(name = "AMEND_ID", nullable = false)
	private Integer amendId;
	
	@Id
	@Column(name = "INTEGRATION_ID", nullable = false)
	private Long integrationId;
	
	//--Other Data Fields
	@Column(name = "SECTION_NAME")
	private String sectionName;
	
	@Column(name = "PRODUCT_NAME")
	private String productName;
	
	@Column(name = "POLICY_TYPE")
	private String policyType;
	
	@Column(name = "CORE_SECTION_CODE")
	private Integer coreSectionCode;
	
	@Column(name = "CORE_SECTION_DESC")
	private String coreSectionDesc;
	
	@Column(name = "CORE_PRODUCT_CODE")
	private Integer coreProductCode;
	
	@Column(name = "CORE_PRODUCT_DESC")
	private String coreProductDesc;
		
	@Column(name = "EFFECTIVE_DATE_START")
	private LocalDate effectiveDateStart;
	
	@Column(name = "EFFECTIVE_DATE_END")
	private LocalDate effectiveDateEnd;
	
	@Column(name = "ENTRY_DATE")
	private LocalDate entryDate;
	
	@Column(name = "UPDATED_DATE")
	private LocalDate updatedDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "STATUS")
	private String status;
	
}
