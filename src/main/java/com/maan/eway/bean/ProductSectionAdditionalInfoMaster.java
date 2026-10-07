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
@IdClass(ProductSectionAdditionalInfoMasterId.class)
@Table(name="PRODUCT_SECTION_ADDITIONAL_INFO")
public class ProductSectionAdditionalInfoMaster implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	 
	//--- ENTITY PRIMARY KEY 
	@Id
	@Column(name="SECTION_ID", nullable=false)
	private Integer    sectionId ;
	
	@Id
	@Column(name="PRODUCT_ID", nullable=false)
	private Integer    productId ;
	
	@Id
	@Column(name="COMPANY_ID", nullable=false, length=100)
	private String     companyId ;
	
	@Id
	@Column(name="AMEND_ID", nullable=false)
	private Integer    amendId ;
	
	
	@Column(name="ADD_DETAIL_YN", nullable=false)
	private String    addDetailYn ;

    //--- ENTITY DATA FIELDS 
    @Column(name="SECTION_NAME", length=100)
    private String     sectionName ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="STATUS", length=1)
    private String     status ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START", nullable=false)
    private Date       effectiveDateStart ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END", nullable=false)
    private Date       effectiveDateEnd ;

    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="CREATED_BY", nullable=false, length=20)
    private String     createdBy ;
  
    @Column(name="UPDATED_BY", length=20)
    private String     updatedBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="JSON_PATH")
    private String     jsonPath ; //json file path  (req, res)
    
    @Column(name="SAVE_URL")
    private String     saveUrl ;
    
    @Column(name="GET_URL")
    private String     getUrl ;
    
    @Column(name="GETALL_URL")	
    private String     getallUrl ;
    
    @Column(name="FILE_NAME")
    private String     fileName ;
    
}



