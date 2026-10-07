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
import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetallSurrenderDetailsReq;
import com.maan.eway.master.req.GetoneSurvivalDetailsReq;
import com.maan.eway.master.req.InsertSurvivalReq;
import com.maan.eway.master.res.GetoneSurvivalDetailsRes;
import com.maan.eway.master.service.SurvivalBenefitMasterService;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MASTER : Survival Master", description = "API's")
@RequestMapping("/master")
public class SurvivalBenefitMasterController {
	
	@Autowired
	private SurvivalBenefitMasterService lifeService;

	@Autowired
	private PrintReqService reqPrinter;
	
	//Save
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/insertsurvival")
	@ApiOperation(value = "This method is Insert Surrender Details")
	public ResponseEntity<CommonRes> insertSurvival(@RequestBody InsertSurvivalReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<Error> validation = lifeService.validateSurvival(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Get All
			SuccessRes res = lifeService.insertSurvival(req);
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
	
	//Get All
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER')")
	@PostMapping("/getallsurvival")
	@ApiOperation("This method is getall Survival Details")
	public ResponseEntity<CommonRes> getallSurvivalDetails(@RequestBody GetallSurrenderDetailsReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		InsertSurvivalReq res = lifeService.getallSurvivalDetails(req);
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
	
	//Get By Id
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER')")
	@PostMapping("/getonesurvival")
	@ApiOperation("This method is getall Surrender Details")
	public ResponseEntity<CommonRes> getoneSurvivalDetails(@RequestBody GetoneSurvivalDetailsReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		GetoneSurvivalDetailsRes res = lifeService.getoneSurvivalDetails(req);
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

}
