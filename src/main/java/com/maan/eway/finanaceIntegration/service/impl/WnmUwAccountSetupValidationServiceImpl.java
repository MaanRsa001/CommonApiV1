package com.maan.eway.finanaceIntegration.service.impl;

import com.maan.eway.bean.WnmUwAccountSetup;
import com.maan.eway.finanaceIntegration.res.CompanyMasterRes;
import com.maan.eway.finanaceIntegration.res.WnmUwAccountSetupRes;
import com.maan.eway.finanaceIntegration.service.WnmUwAccountSetupValidationService;
import com.maan.eway.repository.WnmUwAccountSetupRepository;
import com.maan.eway.finanaceIntegration.req.CoverReq;
import com.maan.eway.finanaceIntegration.req.SectionReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WnmUwAccountSetupValidationServiceImpl implements WnmUwAccountSetupValidationService {

    @Autowired
    private WnmUwAccountSetupRepository repository;

    /**
     * Validation for Create operation
     */
    @Override
    public List<String> validateCreate(WnmUwAccountSetupRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // Mandatory field validations
            if (StringUtils.isBlank(req.getCompanyId())) {
                errors.add("Company ID is required");
            }

            if (StringUtils.isBlank(req.getAcntType())) {
                errors.add("Account Type is required");
            }

            if (StringUtils.isBlank(req.getAcntSubType())) {
                errors.add("Account Sub Type is required");
            }

            if (StringUtils.isBlank(req.getMainAcntCode())) {
                errors.add("Main Account Code is required");
            }

            if (StringUtils.isBlank(req.getDrcrFlag())) {
                errors.add("Dr/Cr Flag is required");
            }

            if (req.getEffectiveFm() == null) {
                errors.add("Effective From Date is required");
            }

            if (req.getEffectiveTo() == null) {
                errors.add("Effective To Date is required");
            }

            // Date validation
            if (req.getEffectiveFm() != null && req.getEffectiveTo() != null) {
                if (req.getEffectiveFm().after(req.getEffectiveTo())) {
                    errors.add("Effective From Date cannot be after Effective To Date");
                }
            }

            // Duplicate check - Check ALL columns except audit fields
            // Only check if there's an active (non-expired) record with same data
            if (!StringUtils.isBlank(req.getCompanyId()) && !StringUtils.isBlank(req.getAcntType())) {

                List<WnmUwAccountSetup> existingRecords = repository.findByAcntTypeAndAcntSubTypeAndAdCustRvPvYnAndAdAutomatchYnAndCompanyCodeFmAndCompanyCodeToAndDivisionCodeFmAndDivisionCodeToAndDepartmentCodeFmAndDepartmentCodeToAndBussinessTypeFmAndBussinessTypeToAndSrcTypeFmAndSrcTypeToAndProductCodeFmAndProductCodeToAndSectionCodeFmAndSectionCodeToAndCoverCodeFmAndCoverCodeToAndChargeCodeFmAndChargeCodeToAndVatCodeFmAndVatCodeToAndSrcCodeFmAndSrcCodeToAndCustCatgAndInstruCodeFmAndInstruCodeToAndMainAcntCodeAndSubAcntCodeAndAcntCompCodeAndAcntDivnCodeAndAcntDeptCodeAndAcntNarrationAndReplaceText1AndReplaceText2AndReplaceText3AndReplaceText4AndReplaceText5AndReplaceText6AndReplaceText7AndReplaceText8AndReplaceText9AndReplaceText10AndDrcrFlagAndTransactionCodeAndCompEntryYnAndPostOnlineYnAndActiveStatusYnAndCompanyId(
                        req.getAcntType(),
                        req.getAcntSubType(),
                        req.getAdCustRvPvYn(),
                        req.getAdAutomatchYn(),
                        req.getCompanyCodeFm(),
                        req.getCompanyCodeTo(),
                        req.getDivisionCodeFm(),
                        req.getDivisionCodeTo(),
                        req.getDepartmentCodeFm(),
                        req.getDepartmentCodeTo(),
                        req.getBussinessTypeFm(),
                        req.getBussinessTypeTo(),
                        req.getSrcTypeFm(),
                        req.getSrcTypeTo(),
                        req.getProductCodeFm(),
                        req.getProductCodeTo(),
                        req.getSectionCodeFm(),
                        req.getSectionCodeTo(),
                        req.getCoverCodeFm(),
                        req.getCoverCodeTo(),
                        req.getChargeCodeFm(),
                        req.getChargeCodeTo(),
                        req.getVatCodeFm(),
                        req.getVatCodeTo(),
                        req.getSrcCodeFm(),
                        req.getSrcCodeTo(),
                        req.getCustCatg(),
                        req.getInstruCodeFm(),
                        req.getInstruCodeTo(),
                        req.getMainAcntCode(),
                        req.getSubAcntCode(),
                        req.getAcntCompCode(),
                        req.getAcntDivnCode(),
                        req.getAcntDeptCode(),
                        req.getAcntNarration(),
                        req.getReplaceText1(),
                        req.getReplaceText2(),
                        req.getReplaceText3(),
                        req.getReplaceText4(),
                        req.getReplaceText5(),
                        req.getReplaceText6(),
                        req.getReplaceText7(),
                        req.getReplaceText8(),
                        req.getReplaceText9(),
                        req.getReplaceText10(),
                        req.getDrcrFlag(),
                        req.getTransactionCode(),
                        req.getCompEntryYn(),
                        req.getPostOnlineYn(),
                        req.getActiveStatusYn(),
                        req.getCompanyId()
                );

                // Check if any of the existing records are NOT expired
                Date today = new Date();
                boolean hasActiveRecord = existingRecords.stream()
                        .anyMatch(record -> record.getEffectiveTo() != null && !record.getEffectiveTo().before(today));

                if (hasActiveRecord) {
                    errors.add("Duplicate entry found with an active (non-expired) record. Cannot create duplicate while active record exists.");
                }
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateCreate: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    /**
     * Validation for Update operation
     */
    @Override
    public List<String> validateUpdate(WnmUwAccountSetupRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // ID is mandatory for update
            if (req.getPasSysId() == null || req.getPasSysId() == 0) {
                errors.add("System ID (pasSysId) is required for update operation");
                return errors;
            }

            // Check if record exists
            if (!repository.existsById(req.getPasSysId())) {
                errors.add("Record not found with System ID: " + req.getPasSysId());
                return errors;
            }

            // Mandatory field validations
            if (StringUtils.isBlank(req.getCompanyId())) {
                errors.add("Company ID is required");
            }

            if (StringUtils.isBlank(req.getAcntType())) {
                errors.add("Account Type is required");
            }

            if (StringUtils.isBlank(req.getAcntSubType())) {
                errors.add("Account Sub Type is required");
            }

            if (StringUtils.isBlank(req.getMainAcntCode())) {
                errors.add("Main Account Code is required");
            }

            if (StringUtils.isBlank(req.getDrcrFlag())) {
                errors.add("Dr/Cr Flag is required");
            }

            if (req.getEffectiveFm() == null) {
                errors.add("Effective From Date is required");
            }

            if (req.getEffectiveTo() == null) {
                errors.add("Effective To Date is required");
            }

            // Date validation
            if (req.getEffectiveFm() != null && req.getEffectiveTo() != null) {
                if (req.getEffectiveFm().after(req.getEffectiveTo())) {
                    errors.add("Effective From Date cannot be after Effective To Date");
                }
            }

            // Duplicate check excluding current record
            // Only check if there's an active (non-expired) record with same data
            if (!StringUtils.isBlank(req.getCompanyId()) && !StringUtils.isBlank(req.getAcntType())) {

                List<WnmUwAccountSetup> existingRecords = repository.findByAcntTypeAndAcntSubTypeAndAdCustRvPvYnAndAdAutomatchYnAndCompanyCodeFmAndCompanyCodeToAndDivisionCodeFmAndDivisionCodeToAndDepartmentCodeFmAndDepartmentCodeToAndBussinessTypeFmAndBussinessTypeToAndSrcTypeFmAndSrcTypeToAndProductCodeFmAndProductCodeToAndSectionCodeFmAndSectionCodeToAndCoverCodeFmAndCoverCodeToAndChargeCodeFmAndChargeCodeToAndVatCodeFmAndVatCodeToAndSrcCodeFmAndSrcCodeToAndCustCatgAndInstruCodeFmAndInstruCodeToAndMainAcntCodeAndSubAcntCodeAndAcntCompCodeAndAcntDivnCodeAndAcntDeptCodeAndAcntNarrationAndReplaceText1AndReplaceText2AndReplaceText3AndReplaceText4AndReplaceText5AndReplaceText6AndReplaceText7AndReplaceText8AndReplaceText9AndReplaceText10AndDrcrFlagAndTransactionCodeAndCompEntryYnAndPostOnlineYnAndActiveStatusYnAndCompanyIdAndPasSysIdNot(
                        req.getAcntType(),
                        req.getAcntSubType(),
                        req.getAdCustRvPvYn(),
                        req.getAdAutomatchYn(),
                        req.getCompanyCodeFm(),
                        req.getCompanyCodeTo(),
                        req.getDivisionCodeFm(),
                        req.getDivisionCodeTo(),
                        req.getDepartmentCodeFm(),
                        req.getDepartmentCodeTo(),
                        req.getBussinessTypeFm(),
                        req.getBussinessTypeTo(),
                        req.getSrcTypeFm(),
                        req.getSrcTypeTo(),
                        req.getProductCodeFm(),
                        req.getProductCodeTo(),
                        req.getSectionCodeFm(),
                        req.getSectionCodeTo(),
                        req.getCoverCodeFm(),
                        req.getCoverCodeTo(),
                        req.getChargeCodeFm(),
                        req.getChargeCodeTo(),
                        req.getVatCodeFm(),
                        req.getVatCodeTo(),
                        req.getSrcCodeFm(),
                        req.getSrcCodeTo(),
                        req.getCustCatg(),
                        req.getInstruCodeFm(),
                        req.getInstruCodeTo(),
                        req.getMainAcntCode(),
                        req.getSubAcntCode(),
                        req.getAcntCompCode(),
                        req.getAcntDivnCode(),
                        req.getAcntDeptCode(),
                        req.getAcntNarration(),
                        req.getReplaceText1(),
                        req.getReplaceText2(),
                        req.getReplaceText3(),
                        req.getReplaceText4(),
                        req.getReplaceText5(),
                        req.getReplaceText6(),
                        req.getReplaceText7(),
                        req.getReplaceText8(),
                        req.getReplaceText9(),
                        req.getReplaceText10(),
                        req.getDrcrFlag(),
                        req.getTransactionCode(),
                        req.getCompEntryYn(),
                        req.getPostOnlineYn(),
                        req.getActiveStatusYn(),
                        req.getCompanyId(),
                        req.getPasSysId()
                );

                // Check if any of the existing records (excluding current) are NOT expired
                Date today = new Date();
                boolean hasActiveRecord = existingRecords.stream()
                        .anyMatch(record -> record.getEffectiveTo() != null && !record.getEffectiveTo().before(today));

                if (hasActiveRecord) {
                    errors.add("Duplicate entry found with an active (non-expired) record. Cannot update to duplicate while active record exists.");
                }
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateUpdate: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    @Override
    public List<String> validateSectionRequest(SectionReq request) {
        List<String> validationCodes = new ArrayList<>();

        // Company ID is mandatory
        if (request.getCompanyId() == null) {
            validationCodes.add("COMPANY_ID_REQUIRED");
        }

        // Validate minProductId range if provided
        if (request.getMinProductId() != null) {
            if (request.getMinProductId() < 1 || request.getMinProductId() > 9999999) {
                validationCodes.add("MIN_PRODUCT_ID_OUT_OF_RANGE");
            }
        }

        // Validate maxProductId range if provided
        if (request.getMaxProductId() != null) {
            if (request.getMaxProductId() < 1 || request.getMaxProductId() > 9999999) {
                validationCodes.add("MAX_PRODUCT_ID_OUT_OF_RANGE");
            }
        }

        // Validate min <= max
        if (request.getMinProductId() != null && request.getMaxProductId() != null) {
            if (request.getMinProductId() > request.getMaxProductId()) {
                validationCodes.add("MIN_PRODUCT_ID_GREATER_THAN_MAX");
            }
        }

        return validationCodes;
    }

    @Override
    public List<String> validateCoverRequest(CoverReq request) {
        List<String> validationCodes = new ArrayList<>();

        // Company ID is mandatory
        if (request.getCompanyId() == null) {
            validationCodes.add("COMPANY_ID_REQUIRED");
        }

        // Validate minProductId range if provided
        if (request.getMinProductId() != null) {
            if (request.getMinProductId() < 1 || request.getMinProductId() > 9999999) {
                validationCodes.add("MIN_PRODUCT_ID_OUT_OF_RANGE");
            }
        }

        // Validate maxProductId range if provided
        if (request.getMaxProductId() != null) {
            if (request.getMaxProductId() < 1 || request.getMaxProductId() > 9999999) {
                validationCodes.add("MAX_PRODUCT_ID_OUT_OF_RANGE");
            }
        }

        // Validate min <= max
        if (request.getMinProductId() != null && request.getMaxProductId() != null) {
            if (request.getMinProductId() > request.getMaxProductId()) {
                validationCodes.add("MIN_PRODUCT_ID_GREATER_THAN_MAX");
            }
        }

        return validationCodes;
    }

    @Override
    public List<String> validateGetCompany(CompanyMasterRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // Company ID is mandatory
            if (StringUtils.isBlank(req.getCompanyId())) {
                errors.add("Company ID is required");
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateGetCompany: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    public List<String> validateGetBranches(CompanyMasterRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // Company ID is mandatory
            if (StringUtils.isBlank(req.getCompanyId())) {
                errors.add("Company ID is required");
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateGetBranches: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    public List<String> validateGetDepartments(CompanyMasterRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // Company ID is mandatory
            if (StringUtils.isBlank(req.getCompanyId())) {
                errors.add("Company ID is required");
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateGetDepartments: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    /**
     * Validation for Delete operation
     */
    public List<String> validateDelete(Long pasSysId) {
        List<String> errors = new ArrayList<>();

        try {
            // ID is mandatory for delete
            if (pasSysId == null || pasSysId == 0) {
                errors.add("System ID (pasSysId) is required for delete operation");
                return errors;
            }

            // Check if record exists
            if (!repository.existsById(pasSysId)) {
                errors.add("Record not found with System ID: " + pasSysId);
            }

            // Add any dependency checks here if needed
            // Example: Check if this setup is being used in accounting entries
            // if (accountingEntryRepository.existsByAccountSetupId(pasSysId)) {
            //     errors.add("Cannot delete: Account setup is being used in accounting entries");
            // }

        } catch (Exception ex) {
            System.err.println("Exception in validateDelete: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    public List<String> validateGetNarrationDetails(WnmUwAccountSetupRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // Narration Account Type is mandatory
            if (StringUtils.isBlank(req.getAcntType())) {
                errors.add("Narration Account Type is required");
            }

            // Narration Account Sub Type is mandatory
            if (StringUtils.isBlank(req.getAcntSubType())) {
                errors.add("Narration Account Sub Type is required");
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateGetNarrationDetails: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }
}