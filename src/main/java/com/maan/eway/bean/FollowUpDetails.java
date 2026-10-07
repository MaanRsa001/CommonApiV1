package com.maan.eway.bean;

import java.io.Serializable;
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


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(FollowUpDetailsId.class)
@Table( name ="EWAY_FOLLOWUP_DETAILS")
public class FollowUpDetails implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//--- ENTITY PRIMARY KEY 
  
    
	@Id
    @Column(name="FOLLOWUP_ID",nullable=false,length=100)
    private String followupId;

	@Id
    @Column(name="REQUEST_REFERENCE_NO",nullable=false,length=100)
    private String requestReferenceNo;

	@Id
    @Column(name="COMPANY_ID",nullable=false,length=100)
    private String companyId;
	
	@Id
    @Column(name="LOGIN_ID",nullable=false,length=100)
    private String loginId;

	@Id
    @Column(name="BRANCH_CODE",nullable=false,length=100)
    private String branchCode;

	@Id
    @Column(name="PRODUCT_ID",nullable=false,length=100)
    private String productId;


    @Column(name="FOLLOWUP_DESC",length=1000)
    private String followupDesc;

    @Column(name="STATUS",length=10)
    private String status;

    @Column(name="STATUS_DESC",length=100)
    private String statusDesc;
    
    @Temporal(TemporalType.DATE)
    @Column(name="ENTRY_DATE")
    private Date      entryDate;
    

    @Temporal(TemporalType.DATE)
    @Column(name="START_DATE")
    private Date      startDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name="END_DATE")
    private Date      endDate;
    
    @Column(name="START_TIME",length=100)
    private String startTime;
    
    @Column(name="END_TIME",length=100)
    private String endTime;
    
    @Column(name="REMARKS",length=100)
    private String remarks;
    
    @Temporal(TemporalType.DATE)
    @Column(name="UPDATED_DATE")
    private Date      updatedDate;
    
}
