package com.maan.eway.bean;

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
@IdClass(EserviceNomineeDetailsId.class)
@Table(name="ESERVICE_NOMINEE_DETAILS")

public class EserviceNomineeDetails implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	 
	    //--- ENTITY PRIMARY KEY 
	    @Id
	    @Column(name="CUSTOMER_REFERENCE_NO", nullable=false)
	    private String    customerReferenceNo ;

	    @Id
	    @Column(name="REQUEST_REFERENCE_NO", nullable=false)
	    private String    requestReferenceNo ;

	    @Id
	    @Column(name="RISK_ID", nullable=false)
	    private Integer    riskId ;

	    @Id
	    @Column(name="NOMINEE_ID", nullable=false)
	    private Integer     nomineeId ;

	    //--- ENTITY DATA FIELDS 
	    @Column(name="QUOTE_NO")
	    private String     quoteNo ;

	    @Column(name="POLICY_NO")
	    private String     policyNo ;

	    @Column(name="BENEFICIARY_NAME")
	    private String     beneficiaryName ;

	    @Column(name="RELATION_WITH_ASSURED")
	    private String    relationWithAssured ;

	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="DOB")
	    private Date dob;

	    @Column(name="AGE")
	    private Integer age;

	    @Column(name="SHARE_PERCENTAGE")
	    private Double sharePercentage;

	    @Column(name="MOBILE_NO")
	    private String mobileNo;
	    
	    @Column(name="EMAIL_ID")
	    private String emailId;
	    
	    @Column(name="GUARDIAN")
	    private String guardian;
	    
	    @Column(name="COMPANY_ID")
	    private String companyId;
	    
	    @Column(name="PRODUCT_ID")
	    private String productId;
	    
	    @Column(name="SECTION_ID")
	    private String sectionId;
	    
	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="ENTRY_DATE")
	    private Date entryDate;
	    
	    @Column(name="CREATED_BY", length=100)
	    private String createdBy;

	    @Column(name="UPDATED_BY", length=100)
	    private String updatedBy;
	    
	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="UPDATED_DATE")
	    private Date updatedDate;
	    
	    @Column(name="STATUS")
	    private String     status ;
	    
	    @Column(name="REJECT_REASON", length=100)
	    private String     rejectReason ;

	}



