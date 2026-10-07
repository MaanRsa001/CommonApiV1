package com.maan.eway.promoCode.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.promoCode.exception.PromoCodeValidation;
import com.maan.eway.promoCode.req.PromoCodeAgentMappingListReq;
import com.maan.eway.promoCode.req.PromoCodeHeaderListReq;
import com.maan.eway.promoCode.req.PromoCodeMappingListReq;
import com.maan.eway.promoCode.req.PromoCodeReq;
import com.maan.eway.promoCode.service.PromoCodeService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/promo-code")
@Log4j2
public class PromoCodeController {

    @Autowired
    private PromoCodeService promoCodeService;

    @Autowired
    private PromoCodeValidation promoCodeValidation;

    // =========================================================================
    // SINGLE API – handles INSERT and AMENDMENT across all three tables
    // =========================================================================

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
    @PostMapping("/saveOrUpdateAmend")
    @ApiOperation(value = "Save or Amend Promo Code – handles INSERT and UPDATE across "
            + "tbl_promo_code_header, promo_code_mapping, and tbl_promo_code_agent_mapping")
    public ResponseEntity<CommonRes> saveOrAmendPromoCode(@RequestBody PromoCodeReq req) {

        log.info("PromoCodeController.saveOrAmendPromoCode -> promoCode: {}, companyId: {}",
                req.getPromoCode(), req.getCompanyId());

        // ------------------------------------------------------------------
        // Validate request (mirrors existing project pattern)
        // ------------------------------------------------------------------
        List<Error> errors = new ArrayList<>();
        errors = promoCodeValidation.validateReq(req);

        if (errors != null && !errors.isEmpty()) {
            CommonRes res = new CommonRes();
            res.setCommonResponse(null);
            res.setIsError(true);
            res.setErrorMessage(errors);
            res.setMessage("Validation failed.");
            res.setErroCode(400);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }

        // ------------------------------------------------------------------
        // Delegate to service (transactional INSERT / AMENDMENT)
        // ------------------------------------------------------------------
        CommonRes res = promoCodeService.saveOrAmendPromoCode(req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
    @PostMapping("/getHeaderList")
    @ApiOperation(value = "Promo Code Header – Grid List. "
            + "Filter by companyId (mandatory) and optionally by amendId.")
    public ResponseEntity<CommonRes> getPromoCodeHeaderList(
            @RequestBody PromoCodeHeaderListReq req) { 
        CommonRes res = promoCodeService.getPromoCodeHeaderList(req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
 

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
    @PostMapping("/getMappingList")
    @ApiOperation(value = "Promo Code Mapping – List. "
            + "Filter by promoCode + companyId (mandatory) and optionally by amendId.")
    public ResponseEntity<CommonRes> getPromoCodeMappingList(
            @RequestBody PromoCodeMappingListReq req) {
        CommonRes res = promoCodeService.getPromoCodeMappingList(req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
 
   
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
    @PostMapping("/getAgentMappingList")
    @ApiOperation(value = "Promo Code Agent Mapping – List. "
            + "Filter by productId + companyId + sectionId + agencyCode (mandatory) "
            + "and optionally by amendId.")
    public ResponseEntity<CommonRes> getPromoCodeAgentMappingList(
            @RequestBody PromoCodeAgentMappingListReq req) {
 
        CommonRes res = promoCodeService.getPromoCodeAgentMappingList(req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}