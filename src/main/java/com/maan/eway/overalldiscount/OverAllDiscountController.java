package com.maan.eway.overalldiscount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.service.PrintReqService;

@RestController
@RequestMapping("/discount")
public class OverAllDiscountController {
	
	@Autowired
	private  EserviceSaveFleetService entityService;
	
	@Autowired
	private PrintReqService reqPrinter;

	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping(value="/savefleetdetails",produces = "application/json")
//	@PostMapping("/savemotordetails")
	public ResponseEntity<CommonRes> saveMotorDetails(@RequestBody  FleetDetailsSaveReq req,@RequestHeader("Authorization") String tokens) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		List<Error> validation = null;
		if(StringUtils.isBlank(req.getRequestReferenceNo()) ) {
			validation = new ArrayList<Error>();
			validation.add( new Error("01","RequestReferenceNo","Please Enter RequestReferenceNo"));
			
		}
		
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			EserviceMotorDetailsSaveRes res = entityService.updateFleetDetails(req,tokens);
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

}
