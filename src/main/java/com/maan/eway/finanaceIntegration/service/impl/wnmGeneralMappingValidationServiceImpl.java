package com.maan.eway.finanaceIntegration.service.impl;

import com.maan.eway.finanaceIntegration.service.wnmGeneralMappingValidationService;
import com.maan.eway.repository.wnmGeneralMappingRepository;
import com.maan.eway.finanaceIntegration.res.WnmGeneralMappingRes;
import com.maan.eway.finanaceIntegration.req.ObjectMappingRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class wnmGeneralMappingValidationServiceImpl implements wnmGeneralMappingValidationService {
    @Autowired
    private wnmGeneralMappingRepository repository;


    private boolean isBalanced(String str, char open, char close) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == open) count++;
            if (c == close) count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    /**
     * Validation for Create operation
     */
    @Override
    public List<String> validateCreate(WnmGeneralMappingRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // Mandatory field validations
            if (StringUtils.isBlank(req.getCompanyId())) {
                errors.add("Company ID is required");
            }

            if (StringUtils.isBlank(req.getGmMapType())) {
                errors.add("Map Type is required");
            }

            if (StringUtils.isBlank(req.getGmTable())) {
                errors.add("Table is required");
            }

            if (StringUtils.isBlank(req.getGmColumn())) {
                errors.add("Column is required");
            }

            if (StringUtils.isBlank(req.getGmColumnType())) {
                errors.add("Column Type is required");
            }

            if (StringUtils.isBlank(req.getGmValueType())) {
                errors.add("Value Type is required");
            }

            // Duplicate check
            if (!StringUtils.isBlank(req.getCompanyId())
                    && !StringUtils.isBlank(req.getGmMapType())
                    && !StringUtils.isBlank(req.getGmTable())
                    && !StringUtils.isBlank(req.getGmColumn())) {

                boolean exists = repository
                        .existsByCompanyIdAndGmMapTypeAndGmTableAndGmColumn(
                                req.getCompanyId(),
                                req.getGmMapType(),
                                req.getGmTable(),
                                req.getGmColumn()
                        );

                if (exists) {
                    errors.add("Duplicate entry found for Company: " + req.getCompanyId()
                            + ", Map Type: " + req.getGmMapType()
                            + ", Table: " + req.getGmTable()
                            + ", Column: " + req.getGmColumn());
                }
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateCreate: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    @Override
    public List<String> validateUpdate(WnmGeneralMappingRes req) {
        List<String> errors = new ArrayList<>();

        try {
            // ID mandatory
            if (req.getGmSysId() == null || req.getGmSysId() == 0) {
                errors.add("System ID (gmSysId) is required for update operation");
                return errors;
            }

            // Check existence
            if (!repository.existsById(req.getGmSysId())) {
                errors.add("Record not found with System ID: " + req.getGmSysId());
                return errors;
            }

            // Mandatory fields
            if (StringUtils.isBlank(req.getCompanyId())) {
                errors.add("Company ID is required");
            }

            if (StringUtils.isBlank(req.getGmMapType())) {
                errors.add("Map Type is required");
            }

            if (StringUtils.isBlank(req.getGmTable())) {
                errors.add("Table is required");
            }

            if (StringUtils.isBlank(req.getGmColumn())) {
                errors.add("Column is required");
            }

            if (StringUtils.isBlank(req.getGmColumnType())) {
                errors.add("Column Type is required");
            }

            if (StringUtils.isBlank(req.getGmValueType())) {
                errors.add("Value Type is required");
            }

            // Duplicate check excluding current record
            if (!StringUtils.isBlank(req.getCompanyId())
                    && !StringUtils.isBlank(req.getGmMapType())
                    && !StringUtils.isBlank(req.getGmTable())
                    && !StringUtils.isBlank(req.getGmColumn())) {

                boolean exists = repository
                        .existsByCompanyIdAndGmMapTypeAndGmTableAndGmColumnAndGmSysIdNot(
                                req.getCompanyId(),
                                req.getGmMapType(),
                                req.getGmTable(),
                                req.getGmColumn(),
                                req.getGmSysId()
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
    public List<String> validateDelete(Long gmSysId) {
        List<String> errors = new ArrayList<>();

        try {
            if (gmSysId == null || gmSysId == 0) {
                errors.add("System ID (gmSysId) is required for delete operation");
                return errors;
            }

            if (!repository.existsById(gmSysId)) {
                errors.add("Record not found with System ID: " + gmSysId);
            }


        } catch (Exception ex) {
            System.err.println("Exception in validateDelete: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }

    @Override
    public List<String> validateGetObjectMappings(ObjectMappingRequest req) {
        List<String> errors = new ArrayList<>();

        try {
            // Table Name is mandatory
            if (StringUtils.isBlank(req.getTableName())) {
                errors.add("Table Name is required");
            }
            if (StringUtils.isBlank(req.getCompanyId())) {
                errors.add("Company Id is required");
            }

        } catch (Exception ex) {
            System.err.println("Exception in validateGetObjectMappings: " + ex.getMessage());
            ex.printStackTrace();
            errors.add("Validation error occurred: " + ex.getMessage());
        }

        return errors;
    }
}
