package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.GetMasterTableIdsReq;
import com.maan.eway.master.req.OriginatingCountryDropdownReq;
import com.maan.eway.master.res.CountryMasterRes;
import com.maan.eway.master.service.MasterTablesDescAndIdService;
import com.maan.eway.res.SuccessRes;

@RestController
@RequestMapping("/master")
public class MasterTablesDescAndIdController {

	@Autowired
	private MasterTablesDescAndIdService service;
	
	@PostMapping("/getid")
	public ResponseEntity<CommonRes> getIdsfromMasterTable(@RequestBody GetMasterTableIdsReq req){
		CommonRes data = new CommonRes();
		SuccessRes res = service.getIdsfromMastersTable(req);
		
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res != null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
		 
		
		
	}
	
	@PostMapping("/dropdown/originationcountry")
	public ResponseEntity<CommonRes> getOrinatingCountryDropdown(@RequestBody OriginatingCountryDropdownReq req){
		CommonRes data = new CommonRes();
		List<CountryMasterRes> res = service.getOriginatingCountryDropdown(req);
		
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res != null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
}
