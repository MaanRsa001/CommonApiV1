package com.maan.eway.bond.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.bond.Dto.BondInformationRequest;
import com.maan.eway.bond.Dto.BondInformationRes;
import com.maan.eway.bond.Dto.GetAllReq;
import com.maan.eway.bond.Dto.commonResponse;
import com.maan.eway.bond.Dto.errors;
import com.maan.eway.bond.service.BondInformationService;
import com.maan.eway.bond.vali.BondInformationValidation;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;


@RestController
@RequestMapping("/api/bond")
public class BondInformationController {

    @Autowired
    private BondInformationService service;
    
    @Autowired
    private BondInformationValidation vali;

    @PostMapping("/save")
    public ResponseEntity<CommonRes> save(@RequestBody BondInformationRequest req) {
    	CommonRes data = new CommonRes();
    	List<Error> errors = vali.saveBondInformationRequest(req);
        if (errors != null && errors.size()>0) {
        	data.setMessage("Failed");
        	data.setErroCode(105);
        	data.setErrorMessage(errors);
        	data.setCommonResponse(null);
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }else {
        	BondInformationRes response = service.save(req);
            data.setIsError(false);
            data.setMessage("Saved Successfully");
            data.setErroCode(110);
            data.setErrorMessage(Collections.emptyList());
            data.setCommonResponse(response);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
    }

    @PostMapping("/get")
    public ResponseEntity<commonResponse> getByFilters(@RequestBody GetAllReq req) {
    	commonResponse res = new commonResponse();

        try {
            BondInformationRequest response = service.getAllByFilters(req);
            res.setIsError(false);
            res.setMessage("Fetched Successfully");
            res.setErroCode(6);
            res.setErrorMessage(Collections.emptyList());
            res.setCommonResponse(response);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
        	errors err = new errors();
            err.setCode("101");
            err.setMessage(e.getMessage());

            res.setIsError(true);
            res.setMessage("");
            res.setErroCode(1);
            res.setErrorMessage(List.of(err));
            res.setCommonResponse(null);
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }
}