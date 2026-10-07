package com.maan.eway.bean;

import java.io.Serializable;

//import java.util.Date;

import java.sql.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
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
@IdClass(InsuranceTypeMasterId.class)
@Table(name="Insurance_Type_Master")
public class InsuranceTypeMaster implements Serializable {
	 
	    @Id
	    @Column(name="COMPANY_ID", nullable=false, length=100)
	    private String    companyId ;

         @Id
	    @Column(name="PRODUCT_ID", nullable=false)
	    private  Integer   productId ;
	    
         
         @Id
	    @Column(name="SECTION_ID", nullable=false)
	    private Integer    sectionId ;
	    
	    @Column(name="SECTION_NAME", length=100)
	    private String     sectionName ;
	    
	    @Id
	    @Column(name="INDUSTRY_TYPE_ID", nullable=false)
	    private String   indsutryTypeId ;
	    
	    @Column(name="INDUSTRY_TYPE_DESC")
	    private String   indsutryTypeDesc ;
	    
	    @Column(name="INDUSTRY_TYPE_DESC_LOCAL")
	    private String   indsutryTypeLocalDesc ;

	    @Column(name="STATUS", length=10)
	    private String     status ;
	    
	
	    //@Temporal(TemporalType.TIMESTAMP)
	    @Column(name="ENTRY_DATE")
	    private Date       entryDate ;
	
	    //@Temporal(TemporalType.TIMESTAMP)
	    @Column(name="EFFECTIVE_DATE_END")
	    private Date       effectiveDateEnd ;

	    
	   // @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="EFFECTIVE_DATE_START")
	    private Date       effectiveDateStart ;

	    @Column(name="CREATED_BY", length=100)
	    private String    createdBy;
	    
	    @Column(name="AMEND_ID")
	    private Integer    amendId ;
	    
	    @Column(name="DISPLAY_ORDER")
	    private Integer   displayOrder  ;
	    
	    @Column(name="REMARKS", length=100)
	    private String     remarks;
	    
	    @Column(name="BODY_TYPE")
	    private String  bodyTypeIds;
	    
	    @Column(name="CORE_APP_CODE")
	    private String coreAppCode;  
}
