package com.maan.eway.excelupload.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "additional_information")
public class AdditionalInformation {

    @Id
    @Column(name="CONTENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId; 

    @Column(name="QUOTE_NO", length=20)
    private String quoteNo;
    
    @Column(name = "RISK_ID" )
    private String riskId;
    
    @Column(name="REQUEST_REFERENCE_NO")
    private String requestReferenceNo;
    
    @Column(name="COMPANY_ID",   length=20)
    private String companyId;
    
    @Column(name="PRODUCT_ID",   length=20)
    private String productId;
    
    @Column(name="LOCATION_ID",   length=20)
    private String locationId;
    
    @Column(name="SECTION_ID",   length=20)
    private String sectionId;
    
    @Column(name="COVER_ID",   length=20)
    private String coverId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENTRY_DATE")
    private Date entryDate;
    
    @Column(name="PARAM_1",   length=200)
    private String param1;
    
    @Column(name="PARAM_2",   length=200)
    private String param2;
    
    @Column(name="VALUE",   length=200)
    private String value;
    
    @Column(name="PARAM_3",   length=200)
    private String param3;
    
    @Column(name="PARAM_4",   length=200)
    private String param4;
    
    @Column(name="PARAM_5",   length=200)
    private String param5;
    
    @Column(name="PARAM_6",   length=200)
    private String param6;
    
    @Column(name="PARAM_7",   length=200)
    private String param7;
    
    @Column(name="PARAM_8",   length=200)
    private String param8;
    
    @Column(name="PARAM_9",   length=200)
    private String param9;
    
    @Column(name="PARAM_10",   length=200)
    private String param10;
    
    @Column(name="STATUS")
    private String status;
    
    @Column(name="ENDT_TYPE_ID")
    private String endtTypeId;
    
    @Column(name="ENDT_COUNT")
    private String endtCount;
    
    @Column(name="ENDT_STATUS")
    private String endtStatus;
    
    @Column(name="ENDT_PREV_QUOTE")
    private String endtPrevQuote;
    
    @Column(name="ENDT_REQ_REF_NO")
    private String endtReqRefNo;
    
    @Column(name="ENDT_CONTENT_ID")
    private String endtAdd;
    
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INCEPTION_DATE")
	private Date inceptionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXPIRY_DATE")
	private Date expiryDate;
}
