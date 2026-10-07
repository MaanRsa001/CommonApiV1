package com.maan.eway.realpay.model;

import java.time.LocalDateTime;
import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "instalment_retry_log")
@Data
public class InstalmentRetryLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "instalment_reference_number", length = 100)
    private String instalmentReferenceNumber;
    
    @Column(name = "client_number", length = 50)
    private String clientNumber;
    
    @Column(name = "retry_attempt")
    private Integer retryAttempt;
    
    @Column(name = "response_code", length = 10)
    private String responseCode;
    
    @Column(name = "retry_date")
    private Date retryDate;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "created_date")
    private Date createdDate;
    
    @Column(name = "updated_date")
    private Date updatedDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }
}
