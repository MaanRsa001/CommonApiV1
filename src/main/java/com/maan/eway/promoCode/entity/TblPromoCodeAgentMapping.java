package com.maan.eway.promoCode.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maps to tbl_promo_code_agent_mapping.
 *
 * Unique constraint (UK_PROMO_AGENT):
 *   PROMO_CODE, AGENCY_CODE, COMPANY_ID, PRODUCT_ID, SECTION_ID, TYPE_OF_BUSINESS
 *
 * Versioning: AMEND_ID increments on each amendment; previous row STATUS → 'N'.
 */
@Data
@Entity
@Table(
    name = "tbl_promo_code_agent_mapping",
    uniqueConstraints = @UniqueConstraint(
        name  = "UK_PROMO_AGENT",
        columnNames = {
            "PROMO_CODE", "AGENCY_CODE", "COMPANY_ID",
            "PRODUCT_ID", "SECTION_ID",  "TYPE_OF_BUSINESS"
        }
    )
)
public class TblPromoCodeAgentMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AGENT_MAP_ID")
    private Long agentMapId;

    @Column(name = "MAPPING_ID", nullable = false)
    private Long mappingId;

    @Column(name = "PROMO_CODE", nullable = false, length = 30)
    private String promoCode;

    @Column(name = "COMPANY_ID", nullable = false)
    private Integer companyId;

    @Column(name = "USER_NAME", nullable = false, length = 50)
    private String userName;

    @Column(name = "AGENCY_CODE", nullable = false, length = 30)
    private String agencyCode;

    // ── New columns (added in updated DDL) ───────────────────────────────────
    @Column(name = "PRODUCT_ID", nullable = false)
    private Integer productId;

    @Column(name = "SECTION_ID", nullable = false)
    private Integer sectionId;

    @Column(name = "TYPE_OF_BUSINESS", nullable = false, length = 30)
    private String typeOfBusiness;
    // ─────────────────────────────────────────────────────────────────────────

    @Column(name = "OA_CODE", length = 30)
    private String oaCode;

    @Column(name = "USER_TYPE", nullable = false, length = 20)
    private String userType;

    @Column(name = "SUB_USER_TYPE", length = 20)
    private String subUserType;

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
    
    @Column(name = "TYPE", length = 20)
    private String type;
    
    @Column(name = "DISC_TYPE", length = 20)
    private String discType;
}