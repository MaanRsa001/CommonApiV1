package com.maan.eway.promoCode.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
    name = "tbl_promo_code_header",
    uniqueConstraints = @UniqueConstraint(
        name  = "UK_PROMO_HEADER",
        columnNames = {"COMPANY_ID", "PRODUCT_ID", "PROMO_CODE", "AMEND_ID", "STATUS"}
    )
)
public class TblPromoCodeHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROMO_ID")
    private Long promoId;

    @Column(name = "PROMO_CODE", nullable = false, length = 30)
    private String promoCode;

    @Column(name = "PROMO_DESC", length = 255)
    private String promoDesc;

    @Column(name = "COMPANY_ID", nullable = false)
    private Integer companyId;

    @Column(name = "BRANCH_ID")
    private Integer branchId;

    /**
     * Populated from the first item in promocodedetails.
     * Nullable at JPA level; the service always sets it before saving.
     */
    @Column(name = "PRODUCT_ID", nullable = false)
    private Integer productId;

    @Column(name = "DISCOUNT_TYPE", nullable = false, length = 20)
    private String discountType;

    @Column(name = "DISCOUNT_TYPE_DESCRIPTION", nullable = false, length = 50)
    private String discountTypeDescription;

    @Column(name = "CALC_TYPE", nullable = false, length = 20)
    private String calcType;

    @Column(name = "CORE_APP_CODE", length = 20)
    private String coreAppCode;

    @Column(name = "AMEND_ID", nullable = false)
    private Integer amendId = 0;

    @Column(name = "EFFECTIVE_START_DATE")
    private LocalDateTime effectiveStartDate;

    @Column(name = "EFFECTIVE_END_DATE")
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

    @Column(name = "REMARKS", length = 255)
    private String remarks;
}