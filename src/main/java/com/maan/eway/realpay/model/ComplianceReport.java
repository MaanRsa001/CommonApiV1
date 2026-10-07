package com.maan.eway.realpay.model;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "compliance_report")
@Data
public class ComplianceReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "report_month")
    private Date reportMonth;
    
    @Column(name = "total_instructions")
    private Long totalInstructions;
    
    @Column(name = "unpaid_instructions")
    private Long unpaidInstructions;
    
    @Column(name = "disputed_instructions")
    private Long disputedInstructions;
    
    @Column(name = "unpaid_ratio", precision = 5, scale = 2)
    private BigDecimal unpaidRatio;
    
    @Column(name = "dispute_ratio", precision = 5, scale = 2)
    private BigDecimal disputeRatio;
    
    @Column(name = "unpaid_compliant")
    private Boolean unpaidCompliant;
    
    @Column(name = "dispute_compliant")
    private Boolean disputeCompliant;
    
    @Column(name = "overall_compliant")
    private Boolean overallCompliant;
    
    @Column(name = "created_date")
    private Date createdDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }
}
