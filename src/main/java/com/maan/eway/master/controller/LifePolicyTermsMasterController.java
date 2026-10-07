package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetPolicyTermsDetailsReq;
import com.maan.eway.master.req.GetallPolicyTermsDetailsReq;
import com.maan.eway.master.req.InsertPolicyTermsReq;
import com.maan.eway.master.res.GetallPolicyTermsDetailsRes;
import com.maan.eway.master.service.LifePolicyTermsMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MASTER : Life Policy Terms Master", description = "API's")
@RequestMapping("/master")
public class LifePolicyTermsMasterController {
	
	@Autowired
	private LifePolicyTermsMasterService lifeService;

	@Autowired
	private PrintReqService reqPrinter;
	
	// save
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
		@PostMapping("/insertpolicyterms")
		@ApiOperation(value = "This method is Insert Policy Terms Details")
		public ResponseEntity<CommonRes> insertPolicyTerms(@RequestBody InsertPolicyTermsReq req) {

			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();

			List<Error> validation = lifeService.validatePolicyTerms(req);
			// validation
			if (validation != null && validation.size() != 0) {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(validation);
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

			} else {

				// Get All
				SuccessRes res = lifeService.insertPolicyTerms(req);
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
		
		// Get All Policy Terms
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/getallpolicyterms")
		@ApiOperation("This method is getall Policy Terms Details")
		public ResponseEntity<CommonRes> getallPolicyTermsDetails(@RequestBody GetallPolicyTermsDetailsReq req) {
			CommonRes data = new CommonRes();
			reqPrinter.reqPrint(req);

			List<GetallPolicyTermsDetailsRes> res = lifeService.getallPolicyTermsDetails(req);
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
		
		// Get single Policy Terms
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/getpolicyterm")
		@ApiOperation("This method is get Single Policy Terms Details")
		public ResponseEntity<CommonRes> getPolicyTermsDetails(@RequestBody GetPolicyTermsDetailsReq req) {
			CommonRes data = new CommonRes();
			reqPrinter.reqPrint(req);

			GetallPolicyTermsDetailsRes res = lifeService.getPolicyTermsDetails(req);
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
		
		//Drop down
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping(value="/dropdown/policytermsmaster")
		@ApiOperation(value = "This method is get Policy Terms Master Drop Down")  // only Active
		public ResponseEntity<DropdownCommonRes> getPolicyTermsMasterDropdown(@RequestBody GetallPolicyTermsDetailsReq req) {

			DropdownCommonRes data = new DropdownCommonRes();

			// Save
			List<DropDownRes> res = lifeService.getPolicyTermsMasterDropdown(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}


}
