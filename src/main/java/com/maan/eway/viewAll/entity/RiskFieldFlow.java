package com.maan.eway.viewAll.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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




/**
* Domain class for entity "FlowFieldDetails"
*
* @author Telosys Tools Generator
*
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(RiskFieldFlowId.class)
@Table(name="risk_field_flow")


public class RiskFieldFlow implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="COMPANY_ID", nullable=false)
    private Integer companyId ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false)
    private Integer productId ;

    @Id
    @Column(name="KEY_ID", nullable=false)
    private Integer keyId ;
    
    @Id
    @Column(name="SECTION_ID")
    private Integer sectionId ;
    
    @Id
    @Column(name="COVER_ID")
    private Integer coverId ;
    
    @Column(name="COMPANY_NAME")
    private String companyName ;
    
    @Column(name="PRODUCT_NAME")
    private String productName ;
    
    @Column(name="SECTION_NAME")
    private String sectionName ;
    
    @Column(name="COVER_NAME")
    private String coverName ;

    //--- ENTITY DATA FIELDS 
    @Column(name="JSON_KEY", length=50)
    private String     jsonKey ;

    @Column(name="IS_HEADER", length=5)
    private String     isHeader ;

    @Column(name="HEADER_KEYID", length=50)
    private String     headerKeyid ;

    @Column(name="ISARRAY", length=5)
    private String     isarray ;

    @Column(name="DATATYPE", length=15)
    private String     datatype ;

    @Column(name="PATTERN", length=30)
    private String     pattern ;

    @Column(name="DEFAULT_YN", length=5)
    private String     defaultYn ;

    @Column(name="DEFAULT_VALUE", length=50)
    private String     defaultValue ;

    @Column(name="STATUS", length=5)
    private String     status ;

    @Column(name="QUERY_ID")
    private BigDecimal queryId ;
    
    @Column(name="QUERY_COL")
    private String queryCol ;
    
    @Column(name="QUERY_ALIAS")
    private String queryAlias;
    
    @Column(name="INTEG_TYPE")
    private String integType; 
    
    @Column(name= "ORDER_BY")
    private Integer orderBy; 
    
    @Column(name= "AMOUNT")
    private String amount;
    
    @Column(name= "ENTRY_DATE")
    private Date entryDate;
    
    @Column(name= "UPDATE_DATE")
    private Date updateDate;
//
    @Column(name= "SECTION_ORDER")
    private Integer sectionOrder;
}



