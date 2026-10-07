package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Entity
@IdClass(MotorBodyTypeMasterId.class)
@Table(name = "eway_motor_bodytype_master")
public class MotorBodyTypeMaster implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// --- ENTITY PRIMARY KEY

	@Id
	@Column(name = "BODY_ID", nullable = false)
	private Integer bodyId;
	
	@Id
	@Column(name = "SECTION_ID", nullable = false)
	private Integer sectionId;

	@Id
	@Column(name = "AMEND_ID", nullable = false)
	private Integer amendId;

	@Id
	@Column(name = "COMPANY_ID", nullable = false, length = 20)
	private String companyId;

	@Id
	@Column(name = "BRANCH_CODE", nullable = false, length = 20)
	private String branchCode;
	
	// --- ENTITY DATA FIELDS
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EFFECTIVE_DATE_START", nullable = false)
	private Date effectiveDateStart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EFFECTIVE_DATE_END", nullable = false)
	private Date effectiveDateEnd;

	@Column(name = "BODY_NAME_EN", length = 100)
	private String bodyNameEn;

	@Column(name = "STATUS", length = 10)
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "en-IN", timezone = "Asia/Calcutta")
	@Column(name = "ENTRY_DATE")
	private Date entryDate;

	@Column(name = "REMARKS", length = 100)
	private String remarks;

	@Column(name = "SEATING_CAPACITY")
	private Integer seatingCapacity;
	
	@Column(name = "TONNAGE")
	private Integer tonnage;
	
	@Column(name = "CYCLINDERS")
	private Integer cyclinders;

	@Column(name = "UPDATED_BY", length = 100)
	private String updatedBy;
	
	@Column(name = "CREATED_BY", length = 100)
	private String createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;

    @Column(name="REGULATORY_CODE",  length=20)
    private String     regulatoryCode ;
    
    @Column(name="CORE_APP_CODE",  length=20)
    private String     coreAppCode ;
    
    @Column(name="BODY_TYPE",  length=20)
    private String     bodyType ;
    
    @Column(name="BODY_NAME_LOCAL",  length=100)
    private String     bodyNameLocal ;
    
	
}