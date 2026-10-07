package com.maan.eway.bean;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import groovy.transform.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
//@Builder   PremiaApiDropdownMaster
@IdClass(PremiaApiDropdownMasterId.class)
@Table(name = "premia_api_dropdown_master")
public class PremiaApiDropdownMaster {

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
	@Column(name = "AMEND_ID", nullable = false)
	private Integer amendId;

	@Id
	@Column(name = "ITEM_TYPE", nullable = false,length = 100)
	private String itemType;

	@Id
	@Column(name = "ITEM_ID", nullable = false)
	private Integer itemId;

	@Column(name = "ITEM_VALUE",length = 500)
	private String itemValue;

	@Column(name = "FROM_VALUE",length = 100)
	private String fromValue;

	@Column(name = "TO_VALUE",length=100)
	private String toValue;

	@Column(name = "CORE_APP_CODE",length=100)
	private String coreAppCode;

	@Column(name = "REGULATORY_CODE",length=100)
	private String regulatoryCode;

	@Column(name = "STATUS",length=2)
	private String status;

	@Column(name = "EFFECTIVE_DATE_START")
	private Date effectiveDateStart;

	@Column(name = "EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;

	@Column(name = "REMARKS",length=100)
	private String remarks;

	@Column(name = "CREATED_BY",length=100)
	private String createdBy;

	@Column(name = "ENTRY_DATE")
	private Date entry_date;

}
