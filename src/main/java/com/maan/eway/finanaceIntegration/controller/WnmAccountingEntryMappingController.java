package com.maan.eway.finanaceIntegration.controller;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.finanaceIntegration.service.WnmAccountingEntryMappingValidationService;
import com.maan.eway.finanaceIntegration.res.WnmAccountingEntryMappingRes;
import com.maan.eway.finanaceIntegration.service.WnmAccountingEntryMappingService;
import com.maan.eway.service.PrintReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accountMapping")
public class WnmAccountingEntryMappingController {

    @Autowired
    private WnmAccountingEntryMappingService service;

    @Autowired
    private WnmAccountingEntryMappingValidationService validationService;

    @Autowired
    private PrintReqService reqPrinter;

    /**
     * Filter/Search endpoint - Get all or filter by criteria
     */
    @PostMapping("/all")
    public ResponseEntity<CommonRes> search(@RequestBody(required = false) WnmAccountingEntryMappingRes request) {
        CommonRes res = new CommonRes();

        try {
            if (request == null) {
                request = new WnmAccountingEntryMappingRes();
            }

            List<WnmAccountingEntryMappingRes> response = service.filterData(request);

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
     * Create new account mapping entry
     */
    @PostMapping("/create")
    public ResponseEntity<CommonRes> create(@RequestBody WnmAccountingEntryMappingRes request) {
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
            WnmAccountingEntryMappingRes response = service.create(request);

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
     * Update existing account mapping entry using aemSysId from request body
     */
    @PutMapping("/update")
    public ResponseEntity<CommonRes> update(@RequestBody WnmAccountingEntryMappingRes request) {
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
            WnmAccountingEntryMappingRes response = service.update(request);

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
                        new Error("", "NOT_FOUND", "Record not found with System ID: " + request.getAemSysId())
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
     * Delete account mapping entry by ID - Returns boolean true if deleted
     */
    @DeleteMapping("/{aemSysId}")
    public ResponseEntity<CommonRes> delete(@PathVariable Long aemSysId) {
        CommonRes res = new CommonRes();

        try {
            // Validation
            List<String> validationCodes = validationService.validateDelete(aemSysId);

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
            boolean deleted = service.delete(aemSysId);

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
}