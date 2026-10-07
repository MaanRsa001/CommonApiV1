package com.maan.eway.realpay.model;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "customer_transaction")
@Data
public class CustomerTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "client_number", length = 50)
    private String clientNumber;
    
    @Column(name = "transaction_type", length = 20) // DEBIT/CREDIT
    private String transactionType;
    
    @Column(name = "amount", precision = 17, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "reason", length = 100)
    private String reason;
    
    @Column(name = "transaction_date")
    private Date transactionDate;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "created_date")
    private Date createdDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }
}
