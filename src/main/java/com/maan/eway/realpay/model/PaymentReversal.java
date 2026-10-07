package com.maan.eway.realpay.model;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payment_reversal")
@Data
public class PaymentReversal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "original_instalment_ref", length = 100)
    private String originalInstalmentRef;
    
    @Column(name = "client_number", length = 50)
    private String clientNumber;
    
    @Column(name = "reversal_amount", precision = 17, scale = 2)
    private BigDecimal reversalAmount;
    
    @Column(name = "reversal_date")
    private Date reversalDate;
    
    @Column(name = "reversal_reason", length = 100)
    private String reversalReason;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "created_date")
    private Date createdDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }
    
    
}
