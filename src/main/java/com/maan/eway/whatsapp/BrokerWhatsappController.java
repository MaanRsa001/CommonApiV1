package com.maan.eway.whatsapp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.ticket.CustomerTrackDashboadReq;

@RestController
@RequestMapping("/api")
public class BrokerWhatsappController {

	@Autowired
	private BrokerWhatsappService brokerWhatsappService;
	
	@PostMapping("/whatsapp/brokercheck")
	public ResponseEntity<CommonRes> brokerCheck(@RequestBody BrokerCheckReq req){
		
		CommonRes res = brokerWhatsappService.brokerCheckWhatsapp(req);
		
		if(res.getIsError() == false) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(res,HttpStatus.OK);
		}
	}
	
	@PostMapping("/save/brokerwhatsapp")
	public ResponseEntity<CommonRes> saveBrokerWhatsapp(@RequestBody BrokerCheckReq req){
		
        CommonRes res = brokerWhatsappService.saveBrokerWhatsapp(req);
		
		if(res.getIsError() == false) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(res,HttpStatus.OK);
		}
	}
	
	@PostMapping("/edit/brokerwhatsapp")
	public ResponseEntity<CommonRes> editBrokerWhatsapp(@RequestBody BrokerCheckReq req){
		
		CommonRes res = brokerWhatsappService.editBrokerWhatsapp(req);
		
		if(res.getIsError() == false) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(res,HttpStatus.OK);
		}
	}
	
	@PostMapping("/brokerwhatsapp/grit")
	public ResponseEntity<CommonRes> gritBrokerWhatsapp(@RequestBody BrokerCheckReq req){
		
		CommonRes res = brokerWhatsappService.gritBrokerWhatsapp(req);
		
		if(res.getIsError() == false) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(res,HttpStatus.OK);
		}
	}
	
	@PostMapping("/brokerwhatsapp/updateemployee")
	public ResponseEntity<CommonRes> UpdateEmployee(@RequestBody UpdateWhatsappEmployeeReq req){
		
		CommonRes res = brokerWhatsappService.UpdateEmployee(req);
		
		if(res.getIsError() == false) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(res,HttpStatus.OK);
		}
	}
}
