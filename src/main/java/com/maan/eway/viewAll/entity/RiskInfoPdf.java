package com.maan.eway.viewAll.entity;

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
@Builder
@Entity
@DynamicInsert
@DynamicUpdate
@IdClass(RiskInfoPdfId.class)
@Table(name="risk_info_pdf")
public class RiskInfoPdf {
	
    @Id
    @Column(name = "Product_id")
    private Integer productId;
    
    @Id
    @Column(name = "Quote_no")
    private String quoteNo;
    
    @Id
    @Column(name = "Broker_Quotation_Yn")
    private String brokerQuotationyn;
    
    @Column(name = "Pdf_St")
    private String pdfSt;
    
   
    @Column(name = "Company_id")
    private String companyid;
    
    @Column(name = "Product_Desc")
    private String productDesc;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Entry_Date")
    private Date entryDate;
    
    
    
   

}
