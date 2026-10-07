package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.bean.ClaimIntimation;
import com.maan.eway.common.req.ClaimIntimationGetAllREq;
import com.maan.eway.common.req.ClaimIntimationGetReq;
import com.maan.eway.common.req.ClaimIntimationReq;
import com.maan.eway.common.req.ClaimIntimationUpdateReq;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.ProductRes;
import com.maan.eway.common.res.SuccessRes;
import com.maan.eway.common.service.ClaimIntimationService;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/claim")
@Api(tags = "CLAIM INTIMATION DETAILS", description = "API's")
public class ClaimIntimationController {
	@Autowired
	private  ClaimIntimationService claimIntimationService;
	@Autowired
	private  PrintReqService reqPrinter;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	

	// save
			@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
			@PostMapping("/insertclaimintimation")
			@ApiOperation(value = "This method is Insert Claim Intimation Details")
			public ResponseEntity<CommonRes> insertclaimIntimation(@RequestBody ClaimIntimationReq req) {

				reqPrinter.reqPrint(req);
				CommonRes data = new CommonRes();
				List<String> validationCodes =  claimIntimationService.validateClaimIntimation(req);
				List<Error> validation = null;
				if(validationCodes!=null && validationCodes.size() > 0 ) {
					CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
					//comErrDescReq.setBranchCode(req.getBranchCode());
					comErrDescReq.setInsuranceId(req.getCompanyId());
					comErrDescReq.setProductId("99999");
					comErrDescReq.setModuleId("31");
					comErrDescReq.setModuleName("MASTERS");
					
					validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
				}

			
				// validation
				if (validation != null && validation.size() != 0) {
					data.setCommonResponse(null);
					data.setIsError(true);
					data.setErrorMessage(validation);
					data.setMessage("Failed");
					return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

				} else {

					// Save
					SuccessRes res = claimIntimationService.insertClaimIntimation(req);
					data.setCommonResponse(res);
					data.setIsError(false);
					data.setErrorMessage(Collections.emptyList());
					data.setMessage("Success");

					if (res != null) {
						return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
					} else {
						return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
					}
				}

			}
			
		//  Get All Claim Intimation
			@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
			@PostMapping("/getallclaimintimation")
			@ApiOperation("This method is getall Claim Intimation Details")
			public ResponseEntity<CommonRes> getallClaimIntimation(@RequestBody ClaimIntimationGetAllREq req)
			{
				CommonRes data = new CommonRes();
				reqPrinter.reqPrint(req);
				
				List<ClaimIntimation> res = claimIntimationService.getallClaimIntimation(req);
				data.setCommonResponse(res);
				data.setErrorMessage(Collections.emptyList());
				data.setIsError(false);
				data.setMessage("Success");
				
				if(res!= null) {
					return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
				}
				else {
					return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
				}
			}
			
			
// Update Claim Intimation
			@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
			@PostMapping("/updateclaimintimation")
			@ApiOperation("This method is getall Claim Intimation Details")
			public ResponseEntity<CommonRes> insertclaimIntimation(@RequestBody ClaimIntimationUpdateReq req) {
				reqPrinter.reqPrint(req);
				CommonRes data = new CommonRes();
				List<String> validationCodes =  claimIntimationService.validateUpdateClaimIntimation(req);
				List<Error> validation = null;
				if(validationCodes!=null && validationCodes.size() > 0 ) {
					CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
					//comErrDescReq.setBranchCode(req.getBranchCode());
					comErrDescReq.setInsuranceId(req.getCompanyId());
					comErrDescReq.setProductId("99999");
					comErrDescReq.setModuleId("31");
					comErrDescReq.setModuleName("MASTERS");
					
					validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
				}

			
				// validation
				if (validation != null && validation.size() != 0) {
					data.setCommonResponse(null);
					data.setIsError(true);
					data.setErrorMessage(validation);
					data.setMessage("Failed");
					return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

				} else {

					// Save
					SuccessRes res = claimIntimationService.updateClaimIntimation(req);
					data.setCommonResponse(res);
					data.setIsError(false);
					data.setErrorMessage(Collections.emptyList());
					data.setMessage("Success");

					if (res != null) {
						return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
					} else {
						return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
					}
				}

			}
			
			// Get By Claim Reference Number 
			@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER')")
			@PostMapping("/getbyclaimreferencenumber")
			@ApiOperation("This Method is to get by Claim Reference Number")
			public ResponseEntity<CommonRes> getByClaimReferenceNo(@RequestBody ClaimIntimationGetReq req)
			{
			CommonRes data = new CommonRes();
			ClaimIntimation res = claimIntimationService.getByClaimReferenceNo(req);
			data.setCommonResponse(res);
			data.setErrorMessage(Collections.emptyList());
			data.setIsError(false);
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
			
		@GetMapping("/product/{companyId}")
		public ResponseEntity<CommonRes> getProduct(@PathVariable String companyId) {
			CommonRes data = new CommonRes();

			List<ProductRes> res = claimIntimationService.getProductByClaimNo(companyId);

			if (res != null) {
				data.setCommonResponse(res);
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				return new ResponseEntity<>(data, HttpStatus.OK);
			} else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setMessage("Failed");
				return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);

			}
		}

	

}
