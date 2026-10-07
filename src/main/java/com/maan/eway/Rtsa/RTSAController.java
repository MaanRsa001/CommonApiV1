package com.maan.eway.Rtsa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.Rtsa.Req.GetRegDetailsReq;
import com.maan.eway.common.res.CommonRes;

@RestController
@RequestMapping("/madison")
public class RTSAController {
	
	@Autowired
	private RSTAService service;
	
	@GetMapping("/get/token")
	public CommonRes getToken(){
		return service.getToken();
	}
	
	@PostMapping("/get/RegDetails")
	public CommonRes GetRegDetails(@RequestBody GetRegDetailsReq req) {
		return service.GetRegDetails(req);
	}
	
	@GetMapping("/get/Rtsalist")
	public CommonRes getRtsaList(@RequestParam(value = "RegNo",required = false) String RegNo){
		return service.getRtsaList(RegNo);
	}

}
