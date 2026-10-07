package com.maan.eway.upr;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.upr.dto.UprPolicyResponse;
import com.maan.eway.upr.service.UprService;

@RestController
@RequestMapping("/upr")
public class UprController {
	
	@Autowired
	private  UprService service;
	
	@PostMapping("/insertUpr")
	public ResponseEntity<CommonRes> saveUprMethod(@RequestParam  String policyno) {
		
		CommonRes data = new CommonRes();
		List<UprPolicyResponse> res = service.saveUpr(policyno);
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
