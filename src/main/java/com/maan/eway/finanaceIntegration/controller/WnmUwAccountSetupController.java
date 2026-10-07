package com.maan.eway.finanaceIntegration.controller;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.finanaceIntegration.service.WnmUwAccountSetupValidationService;
import com.maan.eway.finanaceIntegration.service.WnmUwAccountSetupService;
import com.maan.eway.finanaceIntegration.req.CoverReq;
import com.maan.eway.finanaceIntegration.req.SectionReq;
import com.maan.eway.finanaceIntegration.res.*;
import com.maan.eway.service.PrintReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/uwAccountSetup")
public class WnmUwAccountSetupController {

    @Autowired
    private WnmUwAccountSetupService service;

    @Autowired
    private WnmUwAccountSetupValidationService validationService;

    @Autowired
    private PrintReqService reqPrinter;

    /**
     * Filter/Search endpoint - Get all or filter by criteria
     */
    @PostMapping("/all")
    public ResponseEntity<CommonRes> search(@RequestBody(required = false) WnmUwAccountSetupRes request) {
        CommonRes res = new CommonRes();

        try {
            if (request == null) {
                request = new WnmUwAccountSetupRes();
            }

            List<WnmUwAccountSetupRes> response = service.filterData(request);

            res.setCommonResponse(response);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Fetched Successfully");

            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to fetch data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "FETCH_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    /**
     * Create new UW account setup entry
     */
    @PostMapping("/create")
    public ResponseEntity<CommonRes> create(@RequestBody WnmUwAccountSetupRes request) {
        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            // Validation
            List<String> validationCodes = validationService.validateCreate(request);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(null);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Create
            WnmUwAccountSetupRes response = service.create(request);

            res.setCommonResponse(response);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Created Successfully");

            return new ResponseEntity<>(res, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to create data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "CREATE_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    /**
     * Update existing UW account setup entry using pasSysId from request body
     */
    @PutMapping("/update")
    public ResponseEntity<CommonRes> update(@RequestBody WnmUwAccountSetupRes request) {
        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            // Validation
            List<String> validationCodes = validationService.validateUpdate(request);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(null);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Update
            WnmUwAccountSetupRes response = service.update(request);

            if (response != null) {
                res.setCommonResponse(response);
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("Updated Successfully");
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                res.setCommonResponse(null);
                res.setIsError(true);
                res.setMessage("Record not found");
                res.setErrorMessage(Collections.singletonList(
                        new Error("", "NOT_FOUND", "Record not found with System ID: " + request.getPasSysId())
                ));
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to update data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "UPDATE_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    /**
     * Delete UW account setup entry by ID - Returns boolean true if deleted
     */
    @DeleteMapping("/{pasSysId}")
    public ResponseEntity<CommonRes> delete(@PathVariable Long pasSysId) {
        CommonRes res = new CommonRes();

        try {
            // Validation
            List<String> validationCodes = validationService.validateDelete(pasSysId);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(false);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Delete
            boolean deleted = service.delete(pasSysId);

            res.setCommonResponse(deleted);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage(deleted ? "Deleted Successfully" : "Delete Failed");

            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(false);
            res.setIsError(true);
            res.setMessage("Failed to delete data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "DELETE_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<CommonRes> getAllActiveProducts() {
        CommonRes res = new CommonRes();

        try {
            List<ProductDTO> response = service.getActiveProducts();

            res.setCommonResponse(response);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Fetched Successfully");

            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to fetch data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "FETCH_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @PostMapping("/getSections")
    public ResponseEntity<CommonRes> getSections(@RequestBody SectionReq request) {
        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            // Validation
            List<String> validationCodes = validationService.validateSectionRequest(request);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(null);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Get Sections
            List<SectionDTO> response = service.getSectionsByProductRangeAndCompany(request);

            res.setCommonResponse(response);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Sections Retrieved Successfully");

            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to retrieve sections");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "RETRIEVE_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getCovers")
    public ResponseEntity<CommonRes> getCovers(@RequestBody CoverReq request) {
        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            List<String> validationCodes = validationService.validateCoverRequest(request);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(null);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Get Covers
            List<CoverDTO> response = service.getCoversByProductRangeAndCompany(request);

            res.setCommonResponse(response);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Covers Retrieved Successfully");

            return new ResponseEntity<>(res, HttpStatus.OK);
            // Validation

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to retrieve covers");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "RETRIEVE_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getCompany")
    public ResponseEntity<CommonRes> getCompany(@RequestBody CompanyMasterRes request) {
        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            // Validation
            List<String> validationCodes = validationService.validateGetCompany(request);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(null);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Get company data
            List<CompanyMasterRes> response = service.getCompany(request);

            if (response != null && !response.isEmpty()) {
                res.setCommonResponse(response);
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("Company data fetched successfully");
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                res.setCommonResponse(Collections.emptyList());
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("No active company found for the given ID");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to fetch company data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "FETCH_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @PostMapping("/getDivision")
    public ResponseEntity<CommonRes> getBranches(@RequestBody CompanyMasterRes request) {
        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            // Validation
            List<String> validationCodes = validationService.validateGetBranches(request);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(null);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Get branches data
            List<BranchMasterAccRes> response = service.getBranches(request);

            if (response != null && !response.isEmpty()) {
                res.setCommonResponse(response);
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("Branch data fetched successfully");
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                res.setCommonResponse(Collections.emptyList());
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("No active branches found for the given company");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to fetch branch data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "FETCH_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @PostMapping("/getDepartments")
    public ResponseEntity<CommonRes> getDepartments(@RequestBody CompanyMasterRes request) {
        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            // Validation
            List<String> validationCodes = validationService.validateGetDepartments(request);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(null);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Get departments data
            List<DepartmentRes> response = service.getDepartments(request);

            if (response != null && !response.isEmpty()) {
                res.setCommonResponse(response);
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("Department data fetched successfully");
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                res.setCommonResponse(Collections.emptyList());
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("No departments found for the given company");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to fetch department data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "FETCH_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @PostMapping("/getNarrationDetails")
    public ResponseEntity<CommonRes> getNarrationDetails(@RequestBody WnmUwAccountSetupRes request) {

        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            // Validation
            List<String> validationCodes = validationService.validateGetNarrationDetails(request);

            if (validationCodes != null && !validationCodes.isEmpty()) {
                List<Error> errors = validationCodes.stream()
                        .map(code -> new Error("", "VALIDATION_ERROR", code))
                        .collect(Collectors.toList());

                res.setCommonResponse(null);
                res.setIsError(true);
                res.setErrorMessage(errors);
                res.setMessage("Validation Failed");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            // Get narration details
            List<NarrationResponse> response = service.getNarrationDetails(
                    request.getAcntType(),
                    request.getAcntSubType()
            );

            if (response != null && !response.isEmpty()) {
                res.setCommonResponse(response);
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("Narration details fetched successfully");
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                res.setCommonResponse(Collections.emptyList());
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("No narration details found for the given account type and sub type");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to fetch narration details");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "FETCH_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }
}