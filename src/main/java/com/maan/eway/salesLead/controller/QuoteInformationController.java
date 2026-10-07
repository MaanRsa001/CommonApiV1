package com.maan.eway.salesLead.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.salesLead.req.GetQuotationDetailsReq;
import com.maan.eway.salesLead.req.QuoteInformationDTO;
import com.maan.eway.salesLead.service.QuoteInformationService;

@RestController
@RequestMapping("/api/salesQuote")
public class QuoteInformationController {

    @Autowired
    private QuoteInformationService service;

    @PostMapping("saveQuote")
	public ResponseEntity<?> saveOrUpdate(@RequestBody QuoteInformationDTO dto) {
		QuoteInformationDTO que = service.saveOrUpdate(dto);
		if (que != null) {
			CommonRes res = new CommonRes();
			res.setMessage("SUCCESS");
			res.setCommonResponse(que);
			res.setIsError(false);
			res.setErroCode(0);
			res.setErrorMessage(null);
			return new ResponseEntity<CommonRes>(res, HttpStatus.ACCEPTED);
		} else {
			CommonRes res = new CommonRes();
			res.setCommonResponse(null);
			res.setErroCode(1);
            res.setErrorMessage(Arrays.asList(new Error("E001", "Save Failed", "Unable to save quote")));
			return new ResponseEntity<CommonRes>(res, HttpStatus.ACCEPTED);
		}
	}

    @PostMapping("/getQuotationDetails")
    public ResponseEntity<?> getQuotationDetails(@RequestBody GetQuotationDetailsReq req) {
    	List<QuoteInformationDTO> que = service.getQuotationDetails(req);
    	if (!que.isEmpty()) {
			CommonRes res = new CommonRes();
			res.setMessage("SUCCESS");
			res.setCommonResponse(que);
			res.setIsError(false);
			res.setErroCode(0);
			res.setErrorMessage(null);
			return new ResponseEntity<CommonRes>(res, HttpStatus.ACCEPTED);
		} else {
			CommonRes res = new CommonRes();
			res.setCommonResponse(null);
			res.setErroCode(0);
            res.setErrorMessage(Arrays.asList(new Error("E003", "Not Found", "No quote found for the given ID")));
			return new ResponseEntity<CommonRes>(res, HttpStatus.NO_CONTENT);
		}
       
    }
}
