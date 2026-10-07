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
import com.maan.eway.master.req.AgricultureCropListReq;
import com.maan.eway.master.res.AgricultureCropListResp;
import com.maan.eway.master.service.AgricultureMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.service.PrintReqService;

@RestController
@RequestMapping("/api")
public class AgricultureMasterController {

	@Autowired
	private AgricultureMasterService agriService;
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/agriculture/croplist")
	public ResponseEntity<CommonRes> agricultureCropList(@RequestBody AgricultureCropListReq req){
		
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<Error> validation = agriService.validationCropList(req);
		if(validation!=null && validation.size()>0) {
			data.setCommonResponse(null);
			data.setErrorMessage(validation);
			data.setIsError(true);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}else {
			List<AgricultureCropListResp> res = agriService.getCropList(req);
			data.setErrorMessage(Collections.emptyList());
			data.setIsError(false);
			data.setMessage("Success");
			data.setCommonResponse(res);
			if(res!=null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/agriculture/regionlist")
	public ResponseEntity<DropdownCommonRes> agricultureRegionList(@RequestBody AgricultureCropListReq req){
		
		DropdownCommonRes data = new DropdownCommonRes();
		reqPrinter.reqPrint(req);
	//	List<Error> validation = agriService.validationCropList(req);
	//	if(validation!=null && validation.size()>0) {
	//		data.setCommonResponse(null);
	//		data.setErrorMessage(validation);
	//		data.setIsError(true);
	//		data.setMessage("Failed");
	//		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
	//	}else {
			List<DropDownRes> res = agriService.getRegionList(req);
			data.setErrorMessage(Collections.emptyList());
			data.setIsError(false);
			data.setMessage("Success");
			data.setCommonResponse(res);
			if(res!=null) {
				return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
		
	//}
}
