package com.maan.eway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.admin.req.ModuleIdsInsReq;
import com.maan.eway.admin.req.ModuleIdsReq;
import com.maan.eway.admin.service.ModuleMasterService;
import com.maan.eway.common.res.CommonRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Module : Module list", description = "API's")
@RequestMapping("/master")
public class ModuleMasterController {
	
	@Autowired
	private ModuleMasterService service;

	@PostMapping("/getModuleList")
	@ApiOperation(value="This method is to Display Menu Service")
	public ResponseEntity<CommonRes> menudisplay(@RequestBody ModuleIdsReq req){
	CommonRes data = new CommonRes();
	data = service.getModuleIdsList(req);
	if(data!=null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	}
	else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	}
	
	@PostMapping("/insertModule")
	@ApiOperation(value="This method is to Display Menu Service")
	public ResponseEntity<CommonRes> insertModule(@RequestBody ModuleIdsInsReq req){
	CommonRes data = new CommonRes();
	data = service.insertModuleIds(req);
	if(data!=null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	}
	else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	}
}
