package com.maan.eway.promoCode.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.maan.eway.error.Error;
import com.maan.eway.promoCode.req.PromoCodeDetailReq;
import com.maan.eway.promoCode.req.PromoCodeReq;

/**
 * Promo Code validation.
 *
 * Rules applied per detail line:
 *   - calcType is AUTO-DERIVED from header discountType (D→P, S→F), so it is
 *     NEVER expected from the UI in the detail rows. PROMO_027/028 are removed.
 *   - When discountType = 'S' (Schema), the UI does NOT send
 *     minPremium / maxPremium / minDiscount / maxDiscount.
 *     Those four fields are therefore skipped entirely for discountType = 'S'.
 *   - When discountType = 'D' (Discount), all four range fields are validated.
 */
@Component
public class PromoCodeValidation {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // =========================================================================
    // PUBLIC ENTRY POINT
    // =========================================================================
    public List<Error> validateReq(PromoCodeReq req) {
        List<Error> list = new ArrayList<>();
        try {
            validateRequiredFields(req, list);

            if (list.isEmpty()) {
                validateDiscountType(req.getDiscountType(), list);
                validateEffectiveDates(req.getEffectiveStartDate(), req.getEffectiveEndDate(), list);
                validateDetails(req, list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            list.add(new Error("PROMO_001", "System", "Unexpected validation error occurred."));
        }
        return list;
    }

    // =========================================================================
    // HEADER-LEVEL REQUIRED FIELDS
    // =========================================================================
    private void validateRequiredFields(PromoCodeReq req, List<Error> list) {

        if (req.getCompanyId() == null) {
            list.add(new Error("PROMO_002", "companyId", "Company ID is required."));
        }
        if (StringUtils.isBlank(req.getPromoCode())) {
            list.add(new Error("PROMO_003", "promoCode", "Promo Code is required."));
        }
        if (StringUtils.isBlank(req.getDiscountType())) {
            list.add(new Error("PROMO_004", "discountType", "Discount Type is required."));
        }
        // calcType is NOT validated at header level — it is auto-derived in the service
        // from discountType (D→P, S→F) and must not be sent per detail row either.
        if (StringUtils.isBlank(req.getCreatedBy())) {
            list.add(new Error("PROMO_006", "createdBy", "Created By is required."));
        }
        if (StringUtils.isBlank(req.getEffectiveStartDate())) {
            list.add(new Error("PROMO_007", "effectiveStartDate", "Effective Start Date is required."));
        }
        if (StringUtils.isBlank(req.getEffectiveEndDate())) {
            list.add(new Error("PROMO_008", "effectiveEndDate", "Effective End Date is required."));
        }
        if (req.getPromocodedetails() == null || req.getPromocodedetails().isEmpty()) {
            list.add(new Error("PROMO_009", "promocodedetails",
                    "At least one promo code detail line is required."));
        }
    }

    // =========================================================================
    // HEADER-LEVEL DISCOUNT TYPE  (D / S)
    // =========================================================================
    private void validateDiscountType(String discountType, List<Error> list) {
        if (!("D".equalsIgnoreCase(discountType) || "S".equalsIgnoreCase(discountType))) {
            list.add(new Error("PROMO_010", "discountType",
                    "Discount Type must be 'D' (Discount) or 'S' (Schema)."));
        }
    }

    // =========================================================================
    // HEADER-LEVEL EFFECTIVE DATES
    // =========================================================================
    private void validateEffectiveDates(String startStr, String endStr, List<Error> list) {
        LocalDate today     = LocalDate.now();
        LocalDate startDate = null;
        LocalDate endDate   = null;

        try {
            startDate = LocalDate.parse(startStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            list.add(new Error("PROMO_011", "effectiveStartDate",
                    "Effective Start Date format must be yyyy-MM-dd."));
        }
        try {
            endDate = LocalDate.parse(endStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            list.add(new Error("PROMO_012", "effectiveEndDate",
                    "Effective End Date format must be yyyy-MM-dd."));
        }
        if (startDate != null && startDate.isBefore(today)) {
            list.add(new Error("PROMO_013", "effectiveStartDate",
                    "Effective Start Date must not be a back date."));
        }
        if (endDate != null && endDate.isBefore(today)) {
            list.add(new Error("PROMO_014", "effectiveEndDate",
                    "Effective End Date must not be a back date."));
        }
        if (startDate != null && endDate != null && !endDate.isAfter(startDate)) {
            list.add(new Error("PROMO_015", "effectiveEndDate",
                    "Effective End Date must be greater than Effective Start Date."));
        }
    }

    // =========================================================================
    // DETAIL-LINE LOOP
    // =========================================================================
    private void validateDetails(PromoCodeReq req, List<Error> list) {
        if (req.getPromocodedetails() == null) return;

        boolean isDiscountType = "D".equalsIgnoreCase(req.getDiscountType());
        // isDiscountType = true  → DISCOUNT_TYPE = 'D' → validate premium & discount ranges
        // isDiscountType = false → DISCOUNT_TYPE = 'S' → skip premium & discount range validation

        for (int i = 0; i < req.getPromocodedetails().size(); i++) {
            PromoCodeDetailReq detail = req.getPromocodedetails().get(i);
            String idx   = "[" + i + "]";
            String label = "Row " + (i + 1);

            // ── Required: product ──────────────────────────────────────────
            if (StringUtils.isBlank(detail.getProducts())) {
                list.add(new Error("PROMO_016",
                        "promocodedetails" + idx + ".products",
                        "Product is required in " + label + "."));
            }

            // ── Required & allowed values: typeOfBusiness ──────────────────
            validateTypeOfBusiness(detail.getTypeOfBusiness(), idx, label, list);

            // ── calcType is NOT expected from UI — auto-derived by service ──
            // (no PROMO_027 / PROMO_028 validation here)

            // ── Section ID (only required when discountType = S) ───────────
            validateSectionId(req.getDiscountType(), detail, idx, label, list);

            // ── Premium & Discount range — ONLY when discountType = 'D' ────
            // When discountType = 'S', the UI does not send these values;
            // skipping avoids false validation errors.
            if (isDiscountType) {
                validatePremiumRange(detail, idx, label, list);
                validateDiscountRange(detail, idx, label, list);
            }

            // ── Agency codes required ──────────────────────────────────────
            if (detail.getAgencyCodes() == null || detail.getAgencyCodes().isEmpty()) {
                list.add(new Error("PROMO_024",
                        "promocodedetails" + idx + ".agencyCodes",
                        "At least one Agency Code is required in " + label + "."));
            }
        }
    }

    // =========================================================================
    // TYPE OF BUSINESS  (N / R)
    // =========================================================================
    private void validateTypeOfBusiness(String typeOfBusiness, String idx, String label, List<Error> list) {
        if (StringUtils.isBlank(typeOfBusiness)) {
            list.add(new Error("PROMO_017",
                    "promocodedetails" + idx + ".typeOfBusiness",
                    "Business Type is required in " + label + "."));
            return;
        }
        if (!("N".equalsIgnoreCase(typeOfBusiness) || "R".equalsIgnoreCase(typeOfBusiness))) {
            list.add(new Error("PROMO_018",
                    "promocodedetails" + idx + ".typeOfBusiness",
                    "Business Type must be 'N' (New Business) or 'R' (Renewal) in " + label + "."));
        }
    }

    // =========================================================================
    // SECTION ID  (only mandatory when discountType = S)
    // =========================================================================
    private void validateSectionId(String discountType, PromoCodeDetailReq detail,
                                   String idx, String label, List<Error> list) {
        if ("S".equalsIgnoreCase(discountType) && detail.getSectionIds() == null) {
            list.add(new Error("PROMO_019",
                    "promocodedetails" + idx + ".sectionIds",
                    "Section ID is required when Discount Type is 'S' (Schema) in " + label + "."));
        }
        // discountType = D → section forced to 99999 in service; no UI value needed
    }

    // =========================================================================
    // PREMIUM RANGE  — called ONLY when discountType = 'D'
    // =========================================================================
    private void validatePremiumRange(PromoCodeDetailReq detail, String idx, String label, List<Error> list) {

        Double minP = detail.getMinPremium();
        Double maxP = detail.getMaxPremium();

        if (minP != null && minP < 0) {
            list.add(new Error("PROMO_020",
                    "promocodedetails" + idx + ".minPremium",
                    "Min Premium must not be negative in " + label + "."));
        }
        if (maxP != null && maxP < 0) {
            list.add(new Error("PROMO_021",
                    "promocodedetails" + idx + ".maxPremium",
                    "Max Premium must not be negative in " + label + "."));
        }
        if (minP != null && maxP != null && minP > maxP) {
            list.add(new Error("PROMO_022",
                    "promocodedetails" + idx + ".minPremium",
                    "Min Premium must not be greater than Max Premium in " + label + "."));
        }
    }

    // =========================================================================
    // DISCOUNT RANGE  — called ONLY when discountType = 'D'
    //
    // calcType is auto-derived (D → 'P' = Percentage).
    // For discountType = 'D', calcType will always be 'P', so the 0–100 cap
    // always applies here. The branch is kept explicit for future-proofing.
    // =========================================================================
    private void validateDiscountRange(
        PromoCodeDetailReq detail,
        String idx,
        String label,
			List<Error> list) {

		Double minD = detail.getMinDiscount();
		Double maxD = detail.getMaxDiscount();

		String calcType = detail.getCalcType();

		// P = Percentage, A = Amount
		boolean isPercentage = "P".equalsIgnoreCase(calcType);

		if (minD != null) {

			if (minD < 0) {
				list.add(new Error("PROMO_023A", "promocodedetails" + idx + ".minDiscount",
						"Min Discount must not be negative in " + label + "."));
			}

			// Only Percentage should have 100% validation
			if (isPercentage && minD > 100) {
				list.add(new Error("PROMO_023B", "promocodedetails" + idx + ".minDiscount",
						"Min Discount must not exceed 100% in " + label + "."));
			}
		}

		if (maxD != null) {

			if (maxD < 0) {
				list.add(new Error("PROMO_023C", "promocodedetails" + idx + ".maxDiscount",
						"Max Discount must not be negative in " + label + "."));
			}

			// Only Percentage should have 100% validation
			if (isPercentage && maxD > 100) {
				list.add(new Error("PROMO_023D", "promocodedetails" + idx + ".maxDiscount",
						"Max Discount must not exceed 100% in " + label + "."));
			}
		}

		// Applies to both Amount and Percentage
		if (minD != null && maxD != null && minD > maxD) {
			list.add(new Error("PROMO_023E", "promocodedetails" + idx + ".minDiscount",
					"Min Discount must not be greater than Max Discount in " + label + "."));
		}
	}
}