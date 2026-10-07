package com.maan.eway.reinsurance.controller;

import java.util.Collections;
import java.util.List;

import com.maan.eway.reinsurance.req.SectionCoverReq;
import com.maan.eway.reinsurance.res.SectionCoverRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.reinsurance.req.ReInsuranceApprovedReq;
import com.maan.eway.reinsurance.req.ReInsuranceFreezeReq;
import com.maan.eway.reinsurance.req.ReInsuranceQuoteReq;
import com.maan.eway.reinsurance.res.ReInsuranceCommonRes;
import com.maan.eway.reinsurance.res.ReInsuranceUnfreezeRes;
import com.maan.eway.reinsurance.res.ViewReInsuranceDetails;
import com.maan.eway.reinsurance.service.ReinsuranceService;

@RestController
@RequestMapping("/reinsurance")
public class ReinsuranceController {
	
	@Autowired
	private ReinsuranceService reinsuranceService;

	@PostMapping("/insertReInsurance")
	public ResponseEntity<CommonRes> insertReInsurance(@RequestBody ReInsuranceQuoteReq req) {
		CommonRes data =  reinsuranceService.insertReinsurance(req);
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.CREATED);
		}
	}

	@PostMapping("/viewReInsuranceDetails")
	public ResponseEntity<CommonRes> viewReInsuranceDetails(@RequestBody ReInsuranceQuoteReq req) {
		CommonRes data = new CommonRes();
		ViewReInsuranceDetails viewReinsuranceDetials = reinsuranceService.viewReinsuranceDetials(req.getQuoteNo());

		data.setCommonResponse(viewReinsuranceDetials);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (viewReinsuranceDetials != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.CREATED);
		}

	}
	

	@PostMapping("/updateReInsurance")
	public ResponseEntity<CommonRes> updateReInsurance(@RequestBody ViewReInsuranceDetails req) {
		CommonRes data =  reinsuranceService.updateReInsurance(req);
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("/push/ReInsuranceDetails")
	public ResponseEntity<CommonRes> pushReInsuranceDetails(@RequestBody ReInsuranceQuoteReq req) {
		CommonRes data =  reinsuranceService.updatePolicyNo(req);
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("/freezeApproval")
	public ResponseEntity<ReInsuranceCommonRes> freezeApproval(@RequestBody ReInsuranceApprovedReq req) {
		ReInsuranceCommonRes data = reinsuranceService.updateReInsuranceApprovedStatus(req);
		return new ResponseEntity<ReInsuranceCommonRes>(data, HttpStatus.CREATED);
	}
	
	@PostMapping("/unfreezeReInsurance")
	public ResponseEntity<ReInsuranceUnfreezeRes> unfreezeReInsurance(@RequestBody ReInsuranceFreezeReq req) {
		ReInsuranceUnfreezeRes data = reinsuranceService.updateReInsuranceUnfreezeStatus(req);
		return new ResponseEntity<ReInsuranceUnfreezeRes>(data, HttpStatus.CREATED);
	}

	@PostMapping("/sectioncover")
	public List<SectionCoverRes> search(@RequestBody SectionCoverReq req) {
		return reinsuranceService.getSectionCovers(req.getCompanyId());
	}

}
