package com.maan.eway.ticket;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(CustomerJobTrackerId.class)
@Table(name="customer_job_tracker")
public class CustomerJobTracker implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//--- ENTITY PRIMARY KEY 
	
	@Id
    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=20)
    private String     requestReferenceNo ;
	
	@Id
	@Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;
	
	//--- ENTITY DATA FIELDS 
	
	@Column(name="QUOTE_NO", length=20)
    private String     quoteNo ;
	
	@Column(name = "CUSTOMER_NAME", length = 100)
	private String clientName;
	
	@Column(name="BRANCH_CODE", nullable=false, length=20)
    private String     branchCode ;
	
	@Column(name="BRANCH_NAME")
    private String     branchName ;
	
	@Column(name="PRODUCT_ID", nullable=false, length=20)
    private String     productId ;
    
    @Column(name="PRODUCT_NAME", length=100)
    private String     productName ;
    
    @Column(name="SECTION_ID", nullable=false, length=20)
    private String     sectionId ;
    
    @Column(name="SECTION_NAME", length=100)
    private String     sectionName ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;
    
    @Column(name = "EMAIL", length = 100)
	private String email1;
    
    @Column(name = "MOBILE_NO", length = 20)
	private String mobileNo;
    
    @Column(name = "CURRENT_STATUS", length = 20)
	private String currentStatus;
    
    @Column(name = "REMARKS", length = 500)
	private String remarks;
    
    @Column(name = "SOURCE_TYPE", length = 50)
	private String sourceType;
    
    @Column(name="SUB_USER_TYPE", length=20)
    private String     subUserType ;
    
    @Column(name="LOGIN_ID", length=100)
    private String     loginId ;
    
    @Column(name="ADMIN_REMARKS")
    private String     adminRemarks ;
    
    @Column(name="ADMIN_STATUS")
    private String     adminStatus ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATE_DATE")
    private Date       updateDate ;
    
    @Column(name="UPDATED_BY")
    private String     updatedBy ;

}
