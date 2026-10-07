package com.maan.eway.document.ai.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.maan.eway.common.res.CommonRes;
import com.maan.eway.document.ai.res.RegistrationDocumentRecognitionRes;
import com.maan.eway.document.ai.service.RegistrationDocumentRecognitionService;

@RestController
@RequestMapping("/api/recognition")
public class RegistrationDocumentRecognitionController {
	
	@Autowired
	private RegistrationDocumentRecognitionService service;
	
	
	@PostMapping("/registration/document")
	public ResponseEntity<CommonRes> regDocRegistration(@RequestParam("file") MultipartFile file,@RequestParam("req") Object req){
		System.out.println("Document inserts");
		
		System.out.println("request: "+req);
		CommonRes response = new  CommonRes();
		try {
			RegistrationDocumentRecognitionRes resp = service.getDocumentResult(file,req);
			boolean flag = false;
            boolean allNull = false;
            
	       String regNo="",chassisNo="",make="",engineNo="";
	    	if(resp != null) {
	    		 regNo = resp.getRegistrationNumber() == null ? null : resp.getRegistrationNumber();
	             chassisNo = resp.getChassisNumber() == null ? null : resp.getChassisNumber();
	             make = resp.getMake() == null ? null : resp.getMake();
	            // model = resp.getModel() == null ? null : resp.getModel();
	            engineNo = resp.getEngineNumber() == null ? null : resp.getEngineNumber();
	    		}else {
	    			allNull = true;
	    		}
				if (regNo == null) {
					flag = true;
				}
				if (chassisNo == null) {
					flag = true;
				}
				if (make == null) {
					flag = true;
				}
				if (engineNo == null) {
					flag = true;
				}
		            
	            if(resp == null || allNull || flag) {
	   			response.setMessage("Failed");
	   			response.setCommonResponse(null);
	   			response.setIsError(true);
	   			response.setErroCode(0);
	   			com.maan.eway.error.Error error = new com.maan.eway.error.Error();
	   			error.setCode("101");
	   			error.setField("REGISTRATION DOCUMENT UPLOAD");
	   			error.setMessage("INVALID DOCUMENT: PLEASE UPLOAD VEHICLE REGISTRATION DOCUMENT");
	   			
	   			response.setErrorMessage(Arrays.asList(error));
	   			
		        return ResponseEntity.status(200).body(response);
		       }
	            response.setMessage("Success");
	            response.setCommonResponse(resp);
	   			response.setIsError(false);
	   			response.setErrorMessage(Collections.emptyList());
	            return ResponseEntity.ok(response);
	    		      
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(417).body(response);
		}
	}

}
