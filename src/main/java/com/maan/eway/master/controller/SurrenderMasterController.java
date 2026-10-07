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
import com.maan.eway.master.req.GetoneSurrenderDetailsReq;
import com.maan.eway.master.req.InsertSurrenderReq;
import com.maan.eway.master.res.GetoneSurrenderDetailsRes;
import com.maan.eway.master.service.SurrenderMasterService;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MASTER : Surrender Master", description = "API's")
@RequestMapping("/master")
public class SurrenderMasterController {
	
	@Autowired
	private SurrenderMasterService lifeService;

	@Autowired
	private PrintReqService reqPrinter;
	
	//save
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/insertsurrender")
	@ApiOperation(value = "This method is Insert Surrender Details")
	public ResponseEntity<CommonRes> insertSurrender(@RequestBody InsertSurrenderReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<Error> validation = lifeService.validateSurrender(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Get All
			SuccessRes res = lifeService.insertSurrender(req);
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
	@PostMapping("/getallsurrender")
	@ApiOperation("This method is getall Surrender Details")
	public ResponseEntity<CommonRes> getallSurrenderDetails(@RequestBody GetallSurrenderDetailsReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		InsertSurrenderReq res = lifeService.getallSurrenderDetails(req);
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
	@PostMapping("/getonesurrender")
	@ApiOperation("This method is getall Surrender Details")
	public ResponseEntity<CommonRes> getoneSurrenderDetails(@RequestBody GetoneSurrenderDetailsReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		GetoneSurrenderDetailsRes res = lifeService.getoneSurrenderDetails(req);
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
