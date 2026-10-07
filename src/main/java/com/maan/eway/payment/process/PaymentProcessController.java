package com.maan.eway.payment.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.payment.process.req.SavePaymentProcessReq;
import com.maan.eway.payment.process.req.StatusListReq;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = "/paymentprocess")
@Api(tags = "Policy Tracking", description = "API's")
public class PaymentProcessController {

	@Autowired
	private PaymentProcessService service;
	
	@PostMapping("/save")
	public ResponseEntity<?> savePaymentProcess(@RequestBody SavePaymentProcessReq req){
		CommonRes res = service.savePaymentProcess(req);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	@PostMapping("/get/{type}/{status}")
	public ResponseEntity<?> getStatusList(@PathVariable("type") String type,@PathVariable("status") String status,@RequestBody StatusListReq req){
		CommonRes res = service.getStatusList(type,status,req);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
		
}
