package com.maan.eway.mtpintegration.mtppayment.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "mtp_config_master")
@Data
public class MtpConfigMaster {
   
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "COMPANY_ID")
    private String companyId;
    @Column(name = "MAX_ATTEMPTS")
    private Integer maxAttempts;
    @Column(name = "INTERVAL_MINUTES") 
    private Integer intervalMinutes;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "ENTRY_DATE")
    private Date entryDate;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
}
