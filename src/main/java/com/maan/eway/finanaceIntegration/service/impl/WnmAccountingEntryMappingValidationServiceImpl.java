package com.maan.eway.finanaceIntegration.service.impl;

import com.maan.eway.finanaceIntegration.res.WnmAccountingEntryMappingRes;
import com.maan.eway.finanaceIntegration.service.WnmAccountingEntryMappingValidationService;
import com.maan.eway.repository.WnmAccountingEntryMappingRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WnmAccountingEntryMappingValidationServiceImpl implements WnmAccountingEntryMappingValidationService {

    @Autowired
    private WnmAccountingEntryMappingRepository repository;

    /**
     * Validation for Create operation
     */
    @Override
    public List<String> validateCreate(WnmAccountingEntryMappingRes req) {
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

            if (StringUtils.isBlank(req.getAcntTable())) {
                errors.add("Account Table is required");
            }

            if (StringUtils.isBlank(req.getAcntColumn())) {
                errors.add("Account Column is required");
            }

            if (StringUtils.isBlank(req.getAcntColumnType())) {
                errors.add("Account Column Type is required");
            }

            if (StringUtils.isBlank(req.getAcntValueType())) {
                errors.add("Account Value Type is required");
            }

            // Duplicate check - checking if same combination already exists
            if (!StringUtils.isBlank(req.getCompanyId())
                    && !StringUtils.isBlank(req.getAcntType())
                    && !StringUtils.isBlank(req.getAcntSubType())
                    && !StringUtils.isBlank(req.getAcntTable())
                    && !StringUtils.isBlank(req.getAcntColumn())) {

                boolean exists = repository.existsByCompanyIdAndAcntTypeAndAcntSubTypeAndAcntTableAndAcntColumn(
                        req.getCompanyId(),
                        req.getAcntType(),
                        req.getAcntSubType(),
                        req.getAcntTable(),
                        req.getAcntColumn()
                );

                if (exists) {
                    errors.add("Duplicate entry found for Company: " + req.getCompanyId()
                            + ", Account Type: " + req.getAcntType()
                            + ", Sub Type: " + req.getAcntSubType()
                            + ", Table: " + req.getAcntTable()
                            + ", Column: " + req.getAcntColumn());
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
    public List<String> validateUpdate(WnmAccountingEntryMappingRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // ID is mandatory for update
            if (req.getAemSysId() == null || req.getAemSysId() == 0) {
                errors.add("System ID (aemSysId) is required for update operation");
                return errors; // Return early if ID is missing
            }

            // Check if record exists
            if (!repository.existsById(req.getAemSysId())) {
                errors.add("Record not found with System ID: " + req.getAemSysId());
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

            if (StringUtils.isBlank(req.getAcntTable())) {
                errors.add("Account Table is required");
            }

            if (StringUtils.isBlank(req.getAcntColumn())) {
                errors.add("Account Column is required");
            }

            if (StringUtils.isBlank(req.getAcntColumnType())) {
                errors.add("Account Column Type is required");
            }

            if (StringUtils.isBlank(req.getAcntValueType())) {
                errors.add("Account Value Type is required");
            }

            // Duplicate check excluding current record
            if (!StringUtils.isBlank(req.getCompanyId())
                    && !StringUtils.isBlank(req.getAcntType())
                    && !StringUtils.isBlank(req.getAcntSubType())
                    && !StringUtils.isBlank(req.getAcntTable())
                    && !StringUtils.isBlank(req.getAcntColumn())) {

                boolean exists = repository.existsByCompanyIdAndAcntTypeAndAcntSubTypeAndAcntTableAndAcntColumnAndAemSysIdNot(
                        req.getCompanyId(),
                        req.getAcntType(),
                        req.getAcntSubType(),
                        req.getAcntTable(),
                        req.getAcntColumn(),
                        req.getAemSysId()
                );

                if (exists) {
                    errors.add("Duplicate entry found for the same combination (excluding current record)");
                }
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateUpdate: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    /**
     * Validation for Delete operation
     */
    @Override
    public List<String> validateDelete(Long aemSysId) {
        List<String> errors = new ArrayList<>();

        try {
            // ID is mandatory for delete
            if (aemSysId == null || aemSysId == 0) {
                errors.add("System ID (aemSysId) is required for delete operation");
                return errors;
            }

            // Check if record exists
            if (!repository.existsById(aemSysId)) {
                errors.add("Record not found with System ID: " + aemSysId);
            }

            // Add any dependency checks here if needed
            // Example: Check if this mapping is being used in transactions
            // if (transactionRepository.existsByAccountMappingId(aemSysId)) {
            //     errors.add("Cannot delete: Account mapping is being used in transactions");
            // }

        } catch (Exception ex) {
            System.err.println("Exception in validateDelete: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }
}