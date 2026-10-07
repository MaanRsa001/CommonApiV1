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
@Table(name = "mtp_payment_tracking")
@Data
public class MtpPaymentTracking {
	
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "QUOTE_NO")          
    private String quoteNo;
    @Column(name = "POLICY_NO")         
    private String policyNo;
    @Column(name = "COMPANY_ID")        
    private String companyId;
    @Column(name = "EMPLOYEE_NAME")     
    private String employeeName;
    @Column(name = "EMPLOYEE_MOBILE")   
    private String employeeMobile;
    @Column(name = "CURRENT_ATTEMPT")   
    private Integer currentAttempt;
    @Column(name = "MAX_ATTEMPTS")      
    private Integer maxAttempts;
    @Column(name = "INTERVAL_MINUTES")  
    private Integer intervalMinutes;
    @Column(name = "NEXT_ATTEMPT_DUE")  
    private Date nextAttemptDue;
    @Column(name = "STATUS")            
    private String status;
    @Column(name = "ENTRY_DATE")        
    private Date entryDate;
    @Column(name = "UPDATE_DATE")      
    private Date updatedDate;
}
