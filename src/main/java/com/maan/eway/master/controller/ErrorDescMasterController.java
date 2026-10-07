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

import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ErrorDescMasterGetReq;
import com.maan.eway.master.req.ErrorDescMasterSaveReq;
import com.maan.eway.master.req.ErrorMasterGetAllReq;
import com.maan.eway.master.res.ErrorDescMasterRes;
import com.maan.eway.master.service.ErrorDescMasterService;
import com.maan.eway.res.SuccessRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/master")
@Api(tags = "MASTER : Error Desc Master ", description = "API's")
public class ErrorDescMasterController {
	
	@Autowired
	private ErrorDescMasterService errorDescMasterService;
	
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/inserterrormodules")
	@ApiOperation(value = "This method is Insert Error Modules Details")
	public ResponseEntity<com.maan.eway.common.res.CommonRes> insertProduct(@RequestBody ErrorDescMasterSaveReq req) {

		

		CommonRes data = new CommonRes();
		List<String> validationCodes = errorDescMasterService.validateErrorDesc(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getInsuranceId());
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

			// Get All
			SuccessRes res = errorDescMasterService.inserterrordesc(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getallerrordetails")
	@ApiOperation("This method is getall error Details")
	public ResponseEntity<CommonRes> getallCityDetails(@RequestBody ErrorMasterGetAllReq req)
	{
		CommonRes data = new CommonRes();
		
		List<ErrorDescMasterRes> res = errorDescMasterService.getallErrorDetails(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getbyerrorcode")
	@ApiOperation("This Method is to get by error code")
	public ResponseEntity<CommonRes> getByCityId(@RequestBody ErrorDescMasterGetReq req)
	{
	CommonRes data = new CommonRes();
	ErrorDescMasterRes res = errorDescMasterService.getbyerrorcodeDetails(req);
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

}
