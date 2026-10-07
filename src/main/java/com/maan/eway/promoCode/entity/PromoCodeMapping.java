package com.maan.eway.promoCode.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maps to promo_code_mapping.
 *
 * Unique constraint: (PRODUCT_ID, SECTION_ID, TYPE_OF_BUSINESS, AMEND_ID, STATUS)
 *
 * Versioning: previous row STATUS → 'N', new row STATUS = 'Y'.
 */
@Data
@Entity
@Table(
    name = "promo_code_mapping",
    uniqueConstraints = @UniqueConstraint(
        name  = "UK_PROMO_MAPPING",
        columnNames = {"PRODUCT_ID", "SECTION_ID", "TYPE_OF_BUSINESS", "AMEND_ID", "STATUS"}
    )
)
public class PromoCodeMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAPPING_ID")
    private Long mappingId;

    @Column(name = "PROMO_ID", nullable = false)
    private Long promoId;

    @Column(name = "PROMO_CODE", nullable = false, length = 30)
    private String promoCode;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Integer productId;

    @Column(name = "SECTION_ID", nullable = false)
    private Integer sectionId;

    @Column(name = "TYPE_OF_BUSINESS", nullable = false, length = 30)
    private String typeOfBusiness;

    @Column(name = "BUSINESS_DESCRIPTION", nullable = false, length = 50)
    private String businessDescription;

    @Column(name = "CALC_TYPE", nullable = false, length = 20)
    private String calcType;

    @Column(name = "MIN_PREMIUM", nullable = false, precision = 10, scale = 2)
    private BigDecimal minPremium = BigDecimal.ZERO;

    @Column(name = "MAX_PREMIUM", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxPremium = BigDecimal.ZERO;

    @Column(name = "MIN_DISCOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal minDiscount = BigDecimal.ZERO;

    @Column(name = "MAX_DISCOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxDiscount = BigDecimal.ZERO;

    @Column(name = "AMEND_ID", nullable = false)
    private Integer amendId = 0;

    @Column(name = "EFFECTIVE_START_DATE", nullable = false)
    private LocalDateTime effectiveStartDate;

    @Column(name = "EFFECTIVE_END_DATE", nullable = false)
    private LocalDateTime effectiveEndDate;

    /** 'Y' = active (current amendment) | 'N' = superseded */
    @Column(name = "STATUS", nullable = false, length = 10)
    private String status = "Y";

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "CREATED_BY", nullable = false, length = 50)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "ENTRY_DATE", nullable = false, updatable = false)
    private LocalDateTime entryDate;
    
    @Column(name = "COMPANY_ID", nullable = false)
    private Integer companyId;
    
    @Column(name = "CHANNEL_TYPE", length = 100)
    private String channelType;
}