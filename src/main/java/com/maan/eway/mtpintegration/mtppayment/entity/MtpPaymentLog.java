package com.maan.eway.mtpintegration.mtppayment.entity;

import java.util.Date;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "mtp_payment_log")
@Data
public class MtpPaymentLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TRACKING_ID")        
    private Long trackingId;
    @Column(name = "QUOTE_NO")           
    private String quoteNo;
    @Column(name = "ATTEMPT_NO")         
    private Integer attemptNo;
    @Column(name = "SOURCE")             
    private String source;
    @Column(name = "PAYMENT_REQUEST_ID") 
	private String paymentRequestId;
    @Column(name = "PAYMENT_CHANNEL")    
	private String paymentChannel;
    @Column(name = "REQUEST_JSON")       
	private String requestJson;
    @Column(name = "RESPONSE_JSON")      
	private String responseJson;
    @Column(name = "REQUEST_TIME")       
	private Date requestTime;
    @Column(name = "RESPONSE_TIME")      
	private Date responseTime;
    @Column(name = "STATUS")             
	private String status;
    @Column(name = "ENTRY_DATE")         
	private Date entryDate;
}
