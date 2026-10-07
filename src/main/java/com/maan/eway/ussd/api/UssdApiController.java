package com.maan.eway.ussd.api;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;

@RestController
@RequestMapping({"/api"})
public class UssdApiController {

	private Logger log = LogManager.getLogger(UssdApiController.class);
	   @Autowired
	   private UssdApiService ussdService;

	   @PostMapping({"/ussd"})
	   public ResponseEntity<CommonRes> ussdApi(@RequestBody UssdApiReq req) {
	      CommonRes data = new CommonRes();
	      this.log.info("USSD REQUEST: " + String.valueOf(req));
	      Object res = this.ussdService.ussdApi(req);
	      data.setCommonResponse(res);
	      data.setErrorMessage(Collections.EMPTY_LIST);
	      data.setIsError(false);
	      data.setMessage("Success");
	      if(res != null) {
	    	   return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
	      }
	      else {
	    	   return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
	      }
		
	   }
}
