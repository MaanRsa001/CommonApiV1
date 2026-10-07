package com.maan.eway.bean;

import java.util.Date;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PolicyTypeMasterId.class)
@Table(name="policy_type_master")
public class PolicyTypeMaster {

	@Id
	@Column(name="POLICY_TYPE_ID",nullable=false)
	private Integer policyTypeId;
	
	@Id
	@Column(name="AMEND_ID")
	private Integer amendId;
	
	@Id
    @Column(name="COMPANY_ID", nullable=false, length=100)
    private String     companyId ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false, length=100)
    private Integer     productId;
    
	@Column(name="POLICY_TYPE_NAME",length=100,nullable=false)
	private String policyTypeName;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START", nullable=false)
    private Date       effectiveDateStart ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END", nullable=false)
    private Date       effectiveDateEnd ;

	@Temporal(TemporalType.DATE)
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name="REMARKS",length=100)
	private String remarks;
	
	@Column(name="STATUS",length=1)
	private String status;
	
    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="UPDATED_BY", length=20)
    private String     updatedBy ;
	
    @Column(name="CORE_APP_CODE", length=50)
    private String coreAppCode;
	
}
