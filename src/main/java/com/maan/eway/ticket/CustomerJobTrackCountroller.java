package com.maan.eway.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;

@RestController
@RequestMapping("/job")
public class CustomerJobTrackCountroller {

	@Autowired
	private CustomerJobTrackerService trackerService;
	
	@PostMapping("/customer/tracker")
	public ResponseEntity<CommonRes> customerTrackingDashboad(@RequestBody CustomerTrackDashboadReq req){
		CommonRes res = trackerService.customerTrackingDashboad(req);
		
		if(res.getIsError() == false) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(res,HttpStatus.OK);
		}	
	}
	
	@PostMapping("/customer/tracker/dashboad")
	public ResponseEntity<CommonRes> customerDashboad(@RequestBody CustomerTrackDashboadReq req){
        CommonRes res = trackerService.customerDashboad(req);
		
		if(res.getIsError() == false) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(res,HttpStatus.OK);
		}
	}
	
	@PostMapping("/customer/status/update")
	public ResponseEntity<CommonRes> updateCustomer(@RequestBody CustomerTrackDashboadReq req){
		CommonRes res = trackerService.customerStatusUpdate(req);
		
		if(res.getIsError() == false) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(res,HttpStatus.OK);
		}
	}
	
}
