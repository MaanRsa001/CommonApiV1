package com.maan.eway.promoCode.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.LoginMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.promoCode.entity.PromoCodeMapping;
import com.maan.eway.promoCode.entity.TblPromoCodeAgentMapping;
import com.maan.eway.promoCode.entity.TblPromoCodeHeader;
import com.maan.eway.promoCode.exception.PromoCodeValidation;
import com.maan.eway.promoCode.repository.PromoCodeAgentMappingRepository;
import com.maan.eway.promoCode.repository.PromoCodeHeaderRepository;
import com.maan.eway.promoCode.repository.PromoCodeMappingRepository;
import com.maan.eway.promoCode.req.PromoCodeAgentMappingListReq;
import com.maan.eway.promoCode.req.PromoCodeDetailReq;
import com.maan.eway.promoCode.req.PromoCodeHeaderListReq;
import com.maan.eway.promoCode.req.PromoCodeMappingListReq;
import com.maan.eway.promoCode.req.PromoCodeReq;
import com.maan.eway.promoCode.res.PromoCodeAgentMappingRes;
import com.maan.eway.promoCode.res.PromoCodeHeaderRes;
import com.maan.eway.promoCode.res.PromoCodeMappingRes;
import com.maan.eway.promoCode.service.PromoCodeService;
import com.maan.eway.repository.LoginMasterRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PromoCodeServiceImpl implements PromoCodeService {

	// ── Repositories ──────────────────────────────────────────────────────────
	@Autowired
	private PromoCodeHeaderRepository headerRepo;

	@Autowired
	private PromoCodeMappingRepository mappingRepo;

	@Autowired
	private PromoCodeAgentMappingRepository agentMappingRepo;

	@Autowired
	private LoginMasterRepository loginMasterRepo;

	// ── Validation ────────────────────────────────────────────────────────────
	@Autowired
	private PromoCodeValidation promoCodeValidation;

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// =========================================================================
	// PUBLIC API
	// =========================================================================

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CommonRes saveOrAmendPromoCode(PromoCodeReq req) {

		CommonRes res = new CommonRes();

		try {
			// ------------------------------------------------------------------
			// STEP 1 – Static / format validations (mirrors existing pattern)
			// ------------------------------------------------------------------
			List<Error> errors = promoCodeValidation.validateReq(req);
			if (errors != null && !errors.isEmpty()) {
				res.setIsError(true);
				res.setErrorMessage(errors);
				res.setMessage("Validation failed.");
				res.setErroCode(400);
				return res;
			}

			// ------------------------------------------------------------------
			// STEP 2 – Parse common dates once.
			// Timestamp is included: start date uses start-of-day (00:00),
			// end date uses end-of-day (23:59:59) so coverage is inclusive.
			// ------------------------------------------------------------------
			LocalDate effectiveStartDateOnly = LocalDate.parse(req.getEffectiveStartDate(), DATE_FMT);
			LocalDate effectiveEndDateOnly = LocalDate.parse(req.getEffectiveEndDate(), DATE_FMT);
			LocalDateTime effectiveStartDate = effectiveStartDateOnly.atStartOfDay(); // yyyy-MM-dd 00:00:00
			LocalDateTime effectiveEndDate = effectiveEndDateOnly.atTime(23, 59, 59); // yyyy-MM-dd 23:59:59
			LocalDateTime now = LocalDateTime.now();

			// ------------------------------------------------------------------
			// STEP 3 – Derived values that apply to every detail row.
			//
			// discountType → discountTypeDescription
			// discountType → calcType (auto-derived; NOT taken from the UI)
			// D (Discount) → calcType = 'P'
			// S (Schema) → calcType = 'F'
			// ------------------------------------------------------------------
			String discountTypeDescription = resolveDiscountTypeDescription(req.getDiscountType());
			String derivedCalcType = resolveCalcType(req.getDiscountType());

			// ------------------------------------------------------------------
			// STEP 4 – Loop over each detail (product / section / business line)
			// ------------------------------------------------------------------
			List<Object> savedResults = new ArrayList<>();

			for (PromoCodeDetailReq detail : req.getPromocodedetails()) {

				Integer productId = Integer.parseInt(detail.getProducts().trim());
				Integer sectionId = resolveSectionId(req.getDiscountType(), detail.getSectionIds());
				String tobCode = detail.getTypeOfBusiness().toUpperCase();
				String tobDesc = resolveBusinessDescription(tobCode);
				String calcType = detail.getCalcType();

				// --------------------------------------------------------------
				// 4a. Agency validation – validate ALL agencies BEFORE any write
				// --------------------------------------------------------------
				List<Error> agencyErrors = validateAgencies(req, detail, sectionId, tobCode);
				if (!agencyErrors.isEmpty()) {
					res.setIsError(true);
					res.setErrorMessage(agencyErrors);
					res.setMessage("Agency validation failed.");
					res.setErroCode(400);
					return res;
				}

				// --------------------------------------------------------------
				// 4b. HEADER – INSERT or AMEND
				// --------------------------------------------------------------
				TblPromoCodeHeader header = processHeader(req, productId, discountTypeDescription, derivedCalcType,
						effectiveStartDate, effectiveEndDate, now);

				// --------------------------------------------------------------
				// 4c. MAPPING – INSERT or AMEND
				// --------------------------------------------------------------
				PromoCodeMapping mapping = processMapping(req, detail, header.getPromoId(), productId, sectionId,
						tobCode, tobDesc, derivedCalcType, effectiveStartDate, effectiveEndDate, now);

				// --------------------------------------------------------------
				// 4d. AGENT MAPPING – loop per agency code
				// --------------------------------------------------------------
				for (String agencyCode : detail.getAgencyCodes()) {

					LoginMaster loginMaster = loginMasterRepo
							.findByCompanyIdAndAgencyCode(String.valueOf(req.getCompanyId()), agencyCode.trim())
							.orElseThrow(() -> new RuntimeException(
									"Agency code " + agencyCode + " not found in eway_login_master."));

					processAgentMapping(req, header, mapping, productId, sectionId, tobCode, agencyCode.trim(),
							loginMaster, effectiveStartDate, effectiveEndDate, now, calcType);
				}

				savedResults.add(buildDetailResult(header, mapping));
			}

			// ------------------------------------------------------------------
			// STEP 5 – Success response
			// ------------------------------------------------------------------
			res.setIsError(false);
			res.setMessage("Success");
			res.setErroCode(200);
			res.setCommonResponse(savedResults);

		} catch (Exception e) {
			log.error("PromoCodeServiceImpl.saveOrAmendPromoCode -> {}", e.getMessage(), e);
			List<Error> errList = new ArrayList<>();
			errList.add(new Error("PROMO_500", "System", "An unexpected error occurred: " + e.getMessage()));
			res.setIsError(true);
			res.setErrorMessage(errList);
			res.setMessage("Failed");
			res.setErroCode(500);
		}

		return res;
	}

	// =========================================================================
	// HEADER PROCESSING
	// =========================================================================

	/**
	 * For each (companyId + productId + promoCode) combination: - No record exists
	 * → INSERT with AMEND_ID = 0 - Active record exists → deactivate (STATUS='N',
	 * EFFECTIVE_END_DATE=now), INSERT new with MAX_AMEND_ID + 1
	 *
	 * effectiveStartDate / effectiveEndDate are LocalDateTime (with time
	 * component).
	 */
	private TblPromoCodeHeader processHeader(PromoCodeReq req, Integer productId, String discountTypeDescription,
			String derivedCalcType, LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate,
			LocalDateTime now) {

		Integer companyId = Integer.valueOf(req.getCompanyId());
		String promoCode = req.getPromoCode().trim();

		Integer maxAmendId = headerRepo.findMaxAmendId(companyId, productId, promoCode);
		boolean isFirstInsert = (maxAmendId == null || maxAmendId < 0);

		if (!isFirstInsert) {
			// Deactivate existing active record — sets STATUS='N' and
			// EFFECTIVE_END_DATE=now
			headerRepo.deactivateActiveHeader(companyId, productId, promoCode);
		}

		int newAmendId = isFirstInsert ? 0 : (maxAmendId + 1);

		TblPromoCodeHeader header = new TblPromoCodeHeader();
		header.setPromoCode(promoCode);
		header.setPromoDesc(req.getPromoDesc());
		header.setCompanyId(companyId);
		header.setBranchId(req.getBranchId());
		header.setProductId(productId);
		header.setDiscountType(req.getDiscountType().toUpperCase());
		header.setDiscountTypeDescription(discountTypeDescription);
		// calcType is auto-derived from discountType (D→P, S→F) — never from UI
		header.setCalcType(derivedCalcType);
		header.setCoreAppCode(req.getCoreAppCode());
		header.setAmendId(newAmendId);
		// Store as LocalDateTime (date + time)
		header.setEffectiveStartDate(effectiveStartDate);
		header.setEffectiveEndDate(effectiveEndDate);
		header.setStatus("Y");
		header.setCreatedBy(req.getCreatedBy());
		header.setEntryDate(now);
		header.setRemarks(req.getRemarks());

		return headerRepo.save(header);
	}

	private PromoCodeMapping processMapping(PromoCodeReq req, PromoCodeDetailReq detail, Long promoId,
			Integer productId, Integer sectionId, String tobCode, String tobDesc, String derivedCalcType,
			LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate, LocalDateTime now) {
		Integer companyId = req.getCompanyId();
		Integer maxAmendId = mappingRepo.findMaxAmendId(productId, sectionId, tobCode, companyId);
		boolean isFirstInsert = (maxAmendId == null || maxAmendId < 0);

		if (!isFirstInsert) {
			mappingRepo.deactivateActiveMapping(productId, sectionId, tobCode, companyId);
		}

		int newAmendId = isFirstInsert ? 0 : (maxAmendId + 1);

		PromoCodeMapping mapping = new PromoCodeMapping();
		mapping.setPromoId(promoId);
		mapping.setPromoCode(req.getPromoCode().trim());
		mapping.setProductId(productId);
		mapping.setSectionId(sectionId);
		mapping.setTypeOfBusiness(tobCode);
		mapping.setBusinessDescription(tobDesc);
		// calcType is auto-derived from discountType (D→P, S→F)
		mapping.setCalcType(derivedCalcType);
		// For discountType='S', UI does not send these — toBigDecimal() handles null →
		// ZERO
		mapping.setMinPremium(toBigDecimal(detail.getMinPremium()));
		mapping.setMaxPremium(toBigDecimal(detail.getMaxPremium()));
		mapping.setMinDiscount(toBigDecimal(detail.getMinDiscount()));
		mapping.setMaxDiscount(toBigDecimal(detail.getMaxDiscount()));
		mapping.setAmendId(newAmendId);
		// Store as LocalDateTime (date + time)
		mapping.setEffectiveStartDate(effectiveStartDate);
		mapping.setEffectiveEndDate(effectiveEndDate);
		mapping.setStatus("Y");
		mapping.setCreatedBy(req.getCreatedBy());
		mapping.setEntryDate(now);
		mapping.setCompanyId(companyId);
		mapping.setChannelType(detail.getChannelType());

		return mappingRepo.save(mapping);
	}

	private void processAgentMapping(PromoCodeReq req, TblPromoCodeHeader header, PromoCodeMapping mapping,
			Integer productId, Integer sectionId, String tobCode, String agencyCode, LoginMaster loginMaster,
			LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate, LocalDateTime now, String calcType) {

		String promoCode = req.getPromoCode().trim();
		Integer companyId = req.getCompanyId();

		Integer maxAmendId = agentMappingRepo.findMaxAmendId(promoCode, agencyCode, companyId, productId, sectionId,
				tobCode);
		boolean isFirstInsert = (maxAmendId == null || maxAmendId < 0);

		if (!isFirstInsert) {
			agentMappingRepo.deactivateActiveAgentMapping(promoCode, agencyCode, companyId, productId, sectionId,
					tobCode);
		}

		int newAmendId = isFirstInsert ? 0 : (maxAmendId + 1);

		TblPromoCodeAgentMapping agentMapping = new TblPromoCodeAgentMapping();
		agentMapping.setMappingId(mapping.getMappingId());
		agentMapping.setPromoCode(promoCode);
		agentMapping.setCompanyId(companyId);
		agentMapping.setUserName(loginMaster.getLoginId());
		agentMapping.setAgencyCode(agencyCode);
		agentMapping.setProductId(productId);
		agentMapping.setSectionId(sectionId);
		agentMapping.setTypeOfBusiness(tobCode);
		agentMapping.setOaCode(String.valueOf(loginMaster.getOaCode()));
		agentMapping.setUserType(loginMaster.getUserType());
		agentMapping.setSubUserType(loginMaster.getSubUserType());
		agentMapping.setAmendId(newAmendId);
		// Store as LocalDateTime (date + time)
		agentMapping.setEffectiveStartDate(effectiveStartDate);
		agentMapping.setEffectiveEndDate(effectiveEndDate);
		agentMapping.setStatus("Y");
		agentMapping.setCreatedBy(req.getCreatedBy());
		agentMapping.setEntryDate(now);
		agentMapping.setType(req.getDiscountType().toUpperCase());
		agentMapping.setDiscType(calcType);

		agentMappingRepo.save(agentMapping);
	}

	// =========================================================================
	// AGENCY PRE-VALIDATION (before any write)
	// =========================================================================

	/**
	 * Validates all agency codes in a detail record against eway_login_master
	 * before any database write. Returns a non-empty list if any code is invalid.
	 */
	private List<Error> validateAgencies(PromoCodeReq req, PromoCodeDetailReq detail, Integer sectionId,
			String tobCode) {

		List<Error> errors = new ArrayList<>();

		if (detail.getAgencyCodes() == null || detail.getAgencyCodes().isEmpty()) {
			return errors; // already caught by static validation
		}

		for (String agencyCode : detail.getAgencyCodes()) {
			if (StringUtils.isBlank(agencyCode)) {
				errors.add(new Error("PROMO_025", "agencyCodes", "Agency Code must not be blank."));
				continue;
			}
			boolean exists = loginMasterRepo.existsByCompanyIdAndAgencyCode(
			        String.valueOf(req.getCompanyId()),
			        agencyCode.trim()
			);
			if (!exists) {
				errors.add(new Error("PROMO_026", "agencyCodes", "Agency Code '" + agencyCode.trim()
						+ "' does not exist for Company ID " + req.getCompanyId() + " in eway_login_master."));
			}
		}
		return errors;
	}

	// =========================================================================
	// HELPERS
	// =========================================================================

	/**
	 * Maps discountType → human-readable description. D → "Discount" S → "Schema"
	 */
	private String resolveDiscountTypeDescription(String discountType) {
		if ("D".equalsIgnoreCase(discountType))
			return "Discount";
		if ("S".equalsIgnoreCase(discountType))
			return "Schema";
		return discountType;
	}

	/**
	 * Auto-derives calcType from discountType. This value is never taken from the
	 * UI request. D (Discount) → P (Percentage) S (Schema) → F (Fixed /
	 * schema-based)
	 */
	private String resolveCalcType(String discountType) {
		if ("D".equalsIgnoreCase(discountType))
			return "P";
		if ("S".equalsIgnoreCase(discountType))
			return "F";
		return "P"; // safe default (validated upstream)
	}

	/**
	 * Maps typeOfBusiness code → description. N → "New Business" R → "Renewal"
	 */
	private String resolveBusinessDescription(String tobCode) {
		if ("N".equalsIgnoreCase(tobCode))
			return "New Business";
		if ("R".equalsIgnoreCase(tobCode))
			return "Renewal";
		return tobCode;
	}

	/**
	 * Resolves SECTION_ID per the discount type rule: discountType = D → always
	 * 99999 discountType = S → taken from UI (sectionIds)
	 */
	private Integer resolveSectionId(String discountType, Integer uiSectionId) {
		if ("D".equalsIgnoreCase(discountType))
			return 99999;
		return uiSectionId;
	}

	/** Null-safe Double → BigDecimal. Null values become ZERO. */
	private BigDecimal toBigDecimal(Double value) {
		return (value == null) ? BigDecimal.ZERO : BigDecimal.valueOf(value);
	}

	/**
	 * Builds the per-row result object included in the API success response.
	 * effectiveStartDate / effectiveEndDate are returned as LocalDateTime strings.
	 */
	private Object buildDetailResult(TblPromoCodeHeader header, PromoCodeMapping mapping) {
		java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
		result.put("PromoId", header.getPromoId());
		result.put("PromoCode", header.getPromoCode());
		result.put("ProductId", header.getProductId());
		result.put("CalcType", header.getCalcType());
		result.put("AmendId", header.getAmendId());
		result.put("EffectiveStartDate", header.getEffectiveStartDate()); // LocalDateTime
		result.put("EffectiveEndDate", header.getEffectiveEndDate()); // LocalDateTime
		result.put("Status", header.getStatus());
		result.put("MappingId", mapping.getMappingId());
		result.put("SectionId", mapping.getSectionId());
		result.put("TypeOfBusiness", mapping.getTypeOfBusiness());
		result.put("MappingAmendId", mapping.getAmendId());
		return result;
	}

	@Override
	public CommonRes getPromoCodeHeaderList(PromoCodeHeaderListReq req) {

		CommonRes res = new CommonRes();

		try {
			// ── Validation ────────────────────────────────────────────────
			List<Error> errors = validateHeaderListReq(req);
			if (!errors.isEmpty()) {
				res.setIsError(true);
				res.setErrorMessage(errors);
				res.setMessage("Validation failed.");
				res.setErroCode(400);
				return res;
			}

			// ── Fetch ──────────────────────────────────────────────────────
			List<TblPromoCodeHeader> entityList;

			// Retrieve only the latest amendment record(s) for the given CompanyId
			entityList = headerRepo.findLatestByCompanyId(req.getCompanyId());

			// ── Empty result ───────────────────────────────────────────────
			if (entityList == null || entityList.isEmpty()) {
				res.setIsError(false);
				res.setMessage("No records found.");
				res.setErroCode(200);
				res.setCommonResponse(new ArrayList<>());
				return res;
			}

			// ── Map entity → DTO ───────────────────────────────────────────
			List<PromoCodeHeaderRes> responseList = entityList.stream().map(this::mapHeaderToRes)
					.collect(Collectors.toList());

			res.setIsError(false);
			res.setMessage("Success");
			res.setErroCode(200);
			res.setCommonResponse(responseList);

		} catch (Exception e) {
			log.error("PromoCodeGetServiceImpl.getPromoCodeHeaderList -> {}", e.getMessage(), e);
			List<Error> errList = new ArrayList<>();
			errList.add(new Error("PROMO_GET_500", "System", "An unexpected error occurred: " + e.getMessage()));
			res.setIsError(true);
			res.setErrorMessage(errList);
			res.setMessage("Failed");
			res.setErroCode(500);
		}

		return res;
	}

	// =========================================================================
	// API 2 – Promo Code Mapping List
	// =========================================================================

	@Override
	public CommonRes getPromoCodeMappingList(PromoCodeMappingListReq req) {

		CommonRes res = new CommonRes();

		try {
			// ── Validation ────────────────────────────────────────────────
			List<Error> errors = validateMappingListReq(req);
			if (!errors.isEmpty()) {
				res.setIsError(true);
				res.setErrorMessage(errors);
				res.setMessage("Validation failed.");
				res.setErroCode(400);
				return res;
			}

			// ── Fetch ──────────────────────────────────────────────────────
			List<PromoCodeMapping> entityList;

			entityList = mappingRepo.findLatestByPromoCodeAndCompanyIdAndChannelType(req.getPromoCode().trim(),
					req.getCompanyId(), req.getChannelType());

			// ── Empty result ───────────────────────────────────────────────
			if (entityList == null || entityList.isEmpty()) {
				res.setIsError(false);
				res.setMessage("No records found.");
				res.setErroCode(200);
				res.setCommonResponse(new ArrayList<>());
				return res;
			}

			// ── Map entity → DTO ───────────────────────────────────────────
			List<PromoCodeMappingRes> responseList = entityList.stream().map(this::mapMappingToRes)
					.collect(Collectors.toList());

			res.setIsError(false);
			res.setMessage("Success");
			res.setErroCode(200);
			res.setCommonResponse(responseList);

		} catch (Exception e) {
			log.error("PromoCodeGetServiceImpl.getPromoCodeMappingList -> {}", e.getMessage(), e);
			List<Error> errList = new ArrayList<>();
			errList.add(new Error("PROMO_GET_500", "System", "An unexpected error occurred: " + e.getMessage()));
			res.setIsError(true);
			res.setErrorMessage(errList);
			res.setMessage("Failed");
			res.setErroCode(500);
		}

		return res;
	}

	// =========================================================================
	// API 3 – Promo Code Agent Mapping List
	// =========================================================================

	@Override
	public CommonRes getPromoCodeAgentMappingList(PromoCodeAgentMappingListReq req) {

		CommonRes res = new CommonRes();

		try {
			// ── Validation ────────────────────────────────────────────────
			List<Error> errors = validateAgentMappingListReq(req);
			if (!errors.isEmpty()) {
				res.setIsError(true);
				res.setErrorMessage(errors);
				res.setMessage("Validation failed.");
				res.setErroCode(400);
				return res;
			}

			// ── Fetch ──────────────────────────────────────────────────────
			List<TblPromoCodeAgentMapping> entityList;

			entityList = agentMappingRepo.findLatestByProductIdAndCompanyIdAndSectionIdAndPromoCodeAndTypeAndDiscTypeAndTypeOfBusinessAndUserType(req.getProductId(),
					req.getCompanyId(), req.getSectionId(), req.getPromoCode(), req.getType(), req.getDiscType(),req.getBusinessType(),
					req.getChannelType());

			// ── Empty result ───────────────────────────────────────────────
			if (entityList == null || entityList.isEmpty()) {
				res.setIsError(false);
				res.setMessage("No records found.");
				res.setErroCode(200);
				res.setCommonResponse(new ArrayList<>());
				return res;
			}

			// ── Map entity → DTO ───────────────────────────────────────────
			List<PromoCodeAgentMappingRes> responseList = entityList.stream().map(this::mapAgentMappingToRes)
					.collect(Collectors.toList());

			res.setIsError(false);
			res.setMessage("Success");
			res.setErroCode(200);
			res.setCommonResponse(responseList);

		} catch (Exception e) {
			log.error("PromoCodeGetServiceImpl.getPromoCodeAgentMappingList -> {}", e.getMessage(), e);
			List<Error> errList = new ArrayList<>();
			errList.add(new Error("PROMO_GET_500", "System", "An unexpected error occurred: " + e.getMessage()));
			res.setIsError(true);
			res.setErrorMessage(errList);
			res.setMessage("Failed");
			res.setErroCode(500);
		}

		return res;
	}

	// =========================================================================
	// VALIDATION HELPERS
	// =========================================================================

	private List<Error> validateHeaderListReq(PromoCodeHeaderListReq req) {
		List<Error> list = new ArrayList<>();
		if (req == null) {
			list.add(new Error("PROMO_GET_001", "request", "Request body must not be null."));
			return list;
		}
		if (req.getCompanyId() == null) {
			list.add(new Error("PROMO_GET_002", "companyId", "Company ID is required."));
		}
		return list;
	}

	private List<Error> validateMappingListReq(PromoCodeMappingListReq req) {
		List<Error> list = new ArrayList<>();
		if (req == null) {
			list.add(new Error("PROMO_GET_001", "request", "Request body must not be null."));
			return list;
		}
		if (StringUtils.isBlank(req.getPromoCode())) {
			list.add(new Error("PROMO_GET_003", "promoCode", "Promo Code is required."));
		}
		if (req.getCompanyId() == null) {
			list.add(new Error("PROMO_GET_002", "companyId", "Company ID is required."));
		}
		return list;
	}

	private List<Error> validateAgentMappingListReq(PromoCodeAgentMappingListReq req) {
		List<Error> list = new ArrayList<>();
		if (req.getProductId() == null) {
			list.add(new Error("PROMO_GET_004", "productId", "Product ID is required."));
		}
		if (req.getCompanyId() == null) {
			list.add(new Error("PROMO_GET_002", "companyId", "Company ID is required."));
		}

		return list;
	}

	// =========================================================================
	// ENTITY → DTO MAPPERS
	// =========================================================================

	/**
	 * Maps a TblPromoCodeHeader entity to its response DTO. Every column is mapped
	 * — no field is silently dropped.
	 */
	private PromoCodeHeaderRes mapHeaderToRes(TblPromoCodeHeader entity) {
		PromoCodeHeaderRes res = new PromoCodeHeaderRes();
		res.setPromoId(entity.getPromoId());
		res.setPromoCode(entity.getPromoCode());
		res.setPromoDesc(entity.getPromoDesc());
		res.setCompanyId(entity.getCompanyId());
		res.setBranchId(entity.getBranchId());
		res.setProductId(entity.getProductId());
		res.setDiscountType(entity.getDiscountType());
		res.setDiscountTypeDescription(entity.getDiscountTypeDescription());
		res.setCalcType(entity.getCalcType());
		res.setCoreAppCode(entity.getCoreAppCode());
		res.setAmendId(entity.getAmendId());
		res.setEffectiveStartDate(entity.getEffectiveStartDate());
		res.setEffectiveEndDate(entity.getEffectiveEndDate());
		res.setStatus(entity.getStatus());
		res.setUpdatedDate(entity.getUpdatedDate());
		res.setCreatedBy(entity.getCreatedBy());
		res.setEntryDate(entity.getEntryDate());
		res.setRemarks(entity.getRemarks());
		return res;
	}

	/**
	 * Maps a PromoCodeMapping entity to its response DTO.
	 */
	private PromoCodeMappingRes mapMappingToRes(PromoCodeMapping entity) {
		PromoCodeMappingRes res = new PromoCodeMappingRes();
		res.setMappingId(entity.getMappingId());
		res.setPromoId(entity.getPromoId());
		res.setPromoCode(entity.getPromoCode());
		res.setProductId(entity.getProductId());
		res.setSectionId(entity.getSectionId());
		res.setTypeOfBusiness(entity.getTypeOfBusiness());
		res.setBusinessDescription(entity.getBusinessDescription());
		res.setCalcType(entity.getCalcType());
		res.setMinPremium(entity.getMinPremium());
		res.setMaxPremium(entity.getMaxPremium());
		res.setMinDiscount(entity.getMinDiscount());
		res.setMaxDiscount(entity.getMaxDiscount());
		res.setAmendId(entity.getAmendId());
		res.setEffectiveStartDate(entity.getEffectiveStartDate());
		res.setEffectiveEndDate(entity.getEffectiveEndDate());
		res.setStatus(entity.getStatus());
		res.setUpdatedDate(entity.getUpdatedDate());
		res.setCreatedBy(entity.getCreatedBy());
		res.setEntryDate(entity.getEntryDate());
		return res;
	}

	/**
	 * Maps a TblPromoCodeAgentMapping entity to its response DTO.
	 */
	private PromoCodeAgentMappingRes mapAgentMappingToRes(TblPromoCodeAgentMapping entity) {
		PromoCodeAgentMappingRes res = new PromoCodeAgentMappingRes();
		res.setAgentMapId(entity.getAgentMapId());
		res.setMappingId(entity.getMappingId());
		res.setPromoCode(entity.getPromoCode());
		res.setCompanyId(entity.getCompanyId());
		res.setUserName(entity.getUserName());
		res.setAgencyCode(entity.getAgencyCode());
		res.setProductId(entity.getProductId());
		res.setSectionId(entity.getSectionId());
		res.setTypeOfBusiness(entity.getTypeOfBusiness());
		res.setOaCode(entity.getOaCode());
		res.setUserType(entity.getUserType());
		res.setSubUserType(entity.getSubUserType());
		res.setAmendId(entity.getAmendId());
		res.setEffectiveStartDate(entity.getEffectiveStartDate());
		res.setEffectiveEndDate(entity.getEffectiveEndDate());
		res.setStatus(entity.getStatus());
		res.setUpdatedDate(entity.getUpdatedDate());
		res.setCreatedBy(entity.getCreatedBy());
		res.setEntryDate(entity.getEntryDate());
		return res;
	}
}