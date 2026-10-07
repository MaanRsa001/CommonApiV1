package com.maan.eway.endorsment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.endorsment.service.SingleApiForCancellationService;
import com.maan.eway.error.Error;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "ENDORESMENT : Endorsment ", description = "Cancellation's")
@RequestMapping("/single")
public class SingleApiForCancellationController {
	
	@Autowired
	private SingleApiForCancellationService service;
	
	@PostMapping("/endtCancellation")
	public ResponseEntity<CommonRes> removelocation(@RequestBody Endorsment request , @RequestHeader("Authorization") String tokens) {
		CommonRes data= service.singleCancelPolicy(request,tokens.replaceAll("Bearer ", "").split(",")[0]);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/waCancellation")
	public ResponseEntity<CommonRes> waCancellationALL(@RequestBody Endorsment request , @RequestHeader("Authorization") String tokens) {
		CommonRes data = new CommonRes();
		List<Error> validation = service.validateEndtDetails(request);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			data= service.waCancellationALL(request,tokens.replaceAll("Bearer ", "").split(",")[0]);
		 	if (data != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
		 
	 	
	}

}
