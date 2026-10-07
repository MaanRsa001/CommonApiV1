package com.maan.eway.realpay.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dispute_log")
@Data
public class DisputeLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "instalment_reference_number", length = 100)
    private String instalmentReferenceNumber;
    
    @Column(name = "client_number", length = 50)
    private String clientNumber;
    
    @Column(name = "dispute_date")
    private Date disputeDate;
    
    @Column(name = "response_code", length = 10)
    private String responseCode;
    
    @Column(name = "mandate_deadline")
    private Date mandateDeadline;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "created_date")
    private Date createdDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }
}
