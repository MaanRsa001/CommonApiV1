package com.maan.eway.promoCode.res;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * Response returned by POST /api/promo-code/save.
 *
 * The header snapshot is always present.
 * {@code mappingResults} contains one entry per item in
 * {@code promocodedetails}, each with its own agent-mapping list.
 */
@Data
@Builder
public class PromoCodeResponseDto {

    private String    status;       // SUCCESS | ERROR
    private String    message;

    // ── Header snapshot ───────────────────────────────────────────────────────
    private Long      promoId;
    private String    promoCode;
    private Integer   companyId;
    private Integer   amendId;
    private String    discountType;
    private String    discountTypeDescription;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private String    recordStatus;

    // ── Per-detail results (one per promocodedetails entry) ───────────────────
    private List<MappingResultDto> mappingResults;

    // ─────────────────────────────────────────────────────────────────────────

    @Data
    @Builder
    public static class MappingResultDto {
        private Long    mappingId;
        private Integer productId;
        private Integer sectionId;
        private String  typeOfBusiness;
        private String  businessDescription;
        private Integer amendId;

        private List<AgentMappingDto> agentMappings;
    }

    @Data
    @Builder
    public static class AgentMappingDto {
        private Long    agentMapId;
        private String  agencyCode;
        private String  userName;
        private String  userType;
        private String  oaCode;
        private String  subUserType;
        private Integer amendId;
    }
}