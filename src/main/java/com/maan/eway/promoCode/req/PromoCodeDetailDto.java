package com.maan.eway.promoCode.req;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Represents one row in the "promocodedetails" array of the request.
 * Each detail item drives ONE promo_code_mapping record and
 * N tbl_promo_code_agent_mapping records (one per agencyCode).
 */
@Data
public class PromoCodeDetailDto {

    /** Product ID for this detail line (maps to promo_code_mapping.PRODUCT_ID). */
    private Integer products;          // JSON key: "products"

    /**
     * Section ID — required when discountType = 'S';
     * auto-set to 99999 when discountType = 'D'.
     */
    private Integer sectionIds;        // JSON key: "sectionIds"

    /** N = New Business | R = Renewal */
    private String  typeOfBusiness;

    private BigDecimal minPremium;
    private BigDecimal maxPremium;
    private BigDecimal minDiscount;
    private BigDecimal maxDiscount;

    /** One or more agency codes for this product/section/business-type combination. */
    private List<String> agencyCodes;
}