package com.maan.eway.bean;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import groovy.transform.ToString;
import groovy.transform.builder.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(AgricultureMasterId.class)
@Table(name="agriculture_master")
public class AgricultureMaster {

	//--- ENTITY PRIMARY KEY
	
	@Id
	@Column(name="SNO")
	private Integer sNo;
	
	@Id
	@Column(name="COMPANY_ID")
	private Integer companyId;
	
	@Id
	@Column(name="PRODUCT_ID")
	private Integer productId;
	
	@Id
	@Column(name="AMEND_ID")
	private Integer amendId;
	
	//--- ENTITY DATA FIELDS 
	@Column(name="PROVINCE_ID")
	private Integer provinceId;
	
	@Column(name="PROVINCE_DESC",length=100)
	private String provinceDesc;
	
	@Column(name="DISTRICT_ID")
	private Integer districtId;
	
	@Column(name="DISTRICT_DESC",length=100)
	private String districtDesc;
	
	@Column(name="AEZ")
	private Integer aez;
	
	@Column(name="CROP_ID")
	private Integer cropId;
	
	@Column(name="CROP_DESC",length=100)
	private String cropDesc;
	
	@Column(name="YIELD_PRECENTAGE")
	private Integer yeildPercentage;
	
	@Column(name="PER_HA_COST")
	private BigDecimal perHACost;
	
	@Column(name="SECTION_ID")
	private Integer sectionId;
	
	@Column(name="CORE_APP_CODE",length=100)
	private String coreAppCode;
	
	@Column(name="STATUS",length=5)
	private String status;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date entryDate ;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START")
    private Date effectiveStartDate ;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END")
    private Date effectiveEndDate;
	
	@Column(name="REMARKS",length=200)
	private String remarks;
}
