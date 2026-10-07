package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.dto.InstallmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "realpay_installment_reminder")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealpayInstallmentReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "broker_id")
    private String brokerId;

    @Column(name = "broker_name")
    private String brokerName;

    @Column(name = "broker_email")
    private String brokerEmail;


    @Column(name = "instalment_reference_number")
    private String instalmentReferenceNumber;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "quote_number")
    private String quoteNumber;

    @Column(name = "installment_month")
    private String installmentMonth;

    @Column(name = "installment_amount")
    private BigDecimal installmentAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "next_notification_date")
    private LocalDateTime nextNotificationDate;

    @Column(name = "reminder_count")
    private Integer reminderCount;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status;

    @Column(name="template_name")
    private String templateName;
}