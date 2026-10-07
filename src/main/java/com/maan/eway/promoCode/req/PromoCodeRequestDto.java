package com.maan.eway.promoCode.req;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PromoCodeRequestDto {

    // ── Header-level fields (tbl_promo_code_header) ───────────────────────────
    private Integer   companyId;
    private String    promoCode;
    private String    promoDesc;
    private Integer   branchId;
    private String    status;          // Y / N  (defaults to Y if absent)
    private String    discountType;    // D = Discount | S = Schema
    private String    calcType;
    private String    coreAppCode;
    private String    createdBy;
    private String    remarks;

    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;

    // ── Per-product / per-section / per-business detail lines ─────────────────
    /** Must contain at least one entry. */
    private List<PromoCodeDetailDto> promocodedetails;
}