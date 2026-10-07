package com.maan.eway.finanaceIntegration.controller;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.finanaceIntegration.res.WntAcntDtlRes;
import com.maan.eway.finanaceIntegration.service.WntAcntDtlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/accountDtl")
public class WntAcntDtlController {

    @Autowired
    private WntAcntDtlService service;

    @PostMapping("/search")
    public ResponseEntity<CommonRes> search(@RequestBody(required = false) WntAcntDtlRes request) {

        CommonRes res = new CommonRes();

        try {
            if (request == null) request = new WntAcntDtlRes(); // initialize empty

            List<WntAcntDtlRes> response = service.filterData(request);

            res.setCommonResponse(response);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Fetched Successfully");

            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace(); // log exception for debugging

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage("Failed to fetch data");
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

}
