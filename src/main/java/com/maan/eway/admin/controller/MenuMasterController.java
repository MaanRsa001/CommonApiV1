package com.maan.eway.admin.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.admin.req.GetAllMenuReq;
import com.maan.eway.admin.req.GetMenuTypeReq;
import com.maan.eway.admin.req.MenuDetails;
import com.maan.eway.admin.req.MenuServiceReq;
import com.maan.eway.admin.res.GetMenuTypeRes;
import com.maan.eway.admin.res.GetmenuDetailsRes2;
import com.maan.eway.admin.res.MenuDetailsRes;
import com.maan.eway.admin.res.MenuServiceRes;
import com.maan.eway.admin.service.MenuMasterService;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MENU : Menu List", description = "API's")
@RequestMapping("/master")
public class MenuMasterController {

	@Autowired
	private MenuMasterService menuservice;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	@Autowired
	private PrintReqService reqPrinter;
 
	@PostMapping("/menu")
	@ApiOperation(value="This method is to Display Menu Service")
	public ResponseEntity<CommonRes> menudisplay(@RequestBody MenuServiceReq req){
	CommonRes data = new CommonRes();
	MenuServiceRes res = menuservice.menudisplay(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	
	}
	
	@PostMapping("/savemenu")
	@ApiOperation(value="This method is to Display Menu Service")
	public ResponseEntity<CommonRes> savemenudetails(@RequestBody MenuDetails req){
	CommonRes data = new CommonRes();
	List<String> validationCodes =menuservice.validateMenuDetails(req);
	
	List<Error> validation = null;
	if(validationCodes!=null && validationCodes.size() > 0 ) {
		CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
		comErrDescReq.setBranchCode("99999");
		comErrDescReq.setInsuranceId("99999");
		comErrDescReq.setProductId("99999");
		comErrDescReq.setModuleId("31");
		comErrDescReq.setModuleName("MASTERS");
		
		validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
	}
	if (validation != null && validation.size() != 0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

	} else {
	  MenuDetailsRes res = menuservice.savemenu(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	}
	
	@PostMapping("/getmenudetails")
	@ApiOperation(value="This method is to Display Menu Service")
	public ResponseEntity<CommonRes> getAllMenuDetails(@RequestBody GetAllMenuReq req){
	CommonRes data = new CommonRes();
	Set<GetmenuDetailsRes2> res = menuservice.getAllMenuList(req);
	
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	
	}
	
	@PostMapping("/getByUserType")
	@ApiOperation(value="This method is to Display Menu Service")
	public ResponseEntity<CommonRes> getByusertype(@RequestBody GetMenuTypeReq req){
	CommonRes data = new CommonRes();
	List<GetMenuTypeRes> res = menuservice.getByUserType(req);
	
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	
	}
	
	

}