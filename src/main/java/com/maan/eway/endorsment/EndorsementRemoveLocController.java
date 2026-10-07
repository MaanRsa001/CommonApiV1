package com.maan.eway.endorsment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.endorsment.request.RemoveLocationReq;
import com.maan.eway.endorsment.service.RemoveLocationService;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "ENDORESMENT : Endorsment ", description = "API's")
@RequestMapping("/removelocation")
public class EndorsementRemoveLocController {

	@Autowired
	private RemoveLocationService service;
	
	
	@PostMapping("/api")
	public ResponseEntity<CommonRes> removelocation(@RequestBody RemoveLocationReq request , @RequestHeader("Authorization") String tokens) {
	 	CommonRes data = service.cancelPolicyLocation(request,tokens.replaceAll("Bearer ", "").split(",")[0]);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	
}
