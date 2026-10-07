package com.maan.eway.finanaceIntegration.controller;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.finanaceIntegration.service.wnmGeneralMappingValidationService;
import com.maan.eway.finanaceIntegration.res.ObjectMappingRes;
import com.maan.eway.finanaceIntegration.res.WnmGeneralMappingRes;
import com.maan.eway.finanaceIntegration.service.wnmGeneralMappingService;
import com.maan.eway.finanaceIntegration.req.ObjectMappingRequest;
import com.maan.eway.service.PrintReqService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("generalMapping")
public class wnmGeneralMappingController {


    @Autowired
    private wnmGeneralMappingService service;

    @Autowired
    private wnmGeneralMappingValidationService validationService;

    @Autowired
    private PrintReqService reqPrinter;

    @PostMapping("/all")
    public ResponseEntity<CommonRes> search(
            @RequestBody(required = false) WnmGeneralMappingRes request) {

        CommonRes res = new CommonRes();
        try {
            if (request == null) {
                request = new WnmGeneralMappingRes();
            }

            List<WnmGeneralMappingRes> response = service.filterData(request);

            res.setCommonResponse(response);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Fetched Successfully");

        } catch (Exception e) {
            res.setIsError(true);
            res.setMessage("Failed to fetch data");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "FETCH_ERROR", e.getMessage())
            ));
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<CommonRes> create(@RequestBody WnmGeneralMappingRes request) {

        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
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

            WnmGeneralMappingRes response = service.create(request);

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


    @PutMapping("/update")
    public ResponseEntity<CommonRes> update(@RequestBody WnmGeneralMappingRes request) {

        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
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

            WnmGeneralMappingRes response = service.update(request);

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
                        new Error("", "NOT_FOUND",
                                "Record not found with System ID: " + request.getGmSysId())
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

    @DeleteMapping("/{gmSysId}")
    public ResponseEntity<CommonRes> delete(@PathVariable Long gmSysId) {

        CommonRes res = new CommonRes();

        try {
            List<String> validationCodes = validationService.validateDelete(gmSysId);

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

            boolean deleted = service.delete(gmSysId);

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

    @PostMapping("/getTableDetails")
    public ResponseEntity<CommonRes> getObjectMappings(@RequestBody ObjectMappingRequest request) {

        CommonRes res = new CommonRes();
        reqPrinter.reqPrint(request);

        try {
            // Validation
            List<String> validationCodes = validationService.validateGetObjectMappings(request);

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

            // Get object mappings
            List<ObjectMappingRes> response = service.getObjectMappings(request.getTableName(),request.getCompanyId());

            if (response != null && !response.isEmpty()) {
                res.setCommonResponse(response);
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("Object mappings fetched successfully");
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                res.setCommonResponse(Collections.emptyList());
                res.setIsError(false);
                res.setErrorMessage(Collections.emptyList());
                res.setMessage("No object mappings found for the given table");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to fetch object mappings");
            res.setErrorMessage(Collections.singletonList(
                    new Error("", "FETCH_ERROR", e.getMessage())
            ));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }
}
