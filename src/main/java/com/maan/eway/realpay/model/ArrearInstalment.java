package com.maan.eway.realpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "arrear_instalment")
@Data
public class ArrearInstalment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "original_instalment_ref", length = 100)
    private String originalInstalmentRef;
    
    @Column(name = "contract_number", length = 50)
    private String contractNumber;
    
    @Column(name = "client_number", length = 50)
    private String clientNumber;
    
    @Column(name = "arrear_amount", precision = 17, scale = 2)
    private BigDecimal arrearAmount;
    
    @Column(name = "reason", length = 100)
    private String reason;
    
    @Column(name = "mandate_type", length = 50)
    private String mandateType;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "recovery_contract_ref", length = 50)
    private String recoveryContractRef;
    
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
