package com.maan.eway.document.ai.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.document.ai.service.PassportRecognitionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recognition")
@RequiredArgsConstructor
public class PassportRecognitionController {
	
	@Autowired
	private PassportRecognitionService passportService;

	@PostMapping("passport")
    public ResponseEntity<?> docAnalyse(@RequestParam("file") MultipartFile file,
    		@RequestParam("req") Object request,@RequestHeader("Authorization") String token){
    	System.out.println("Document inserts");
    	try {
    		JsonNode resp = passportService.generateAiResult(file);
    		boolean flag = false;
            boolean allNull = false;
    		 if(!resp.get("data").isEmpty()) {
    			 String jsonResp = resp.toString();
		            ObjectMapper mapper = new ObjectMapper();
		            Map<String,Object> root = mapper.readValue(jsonResp, Map.class);
		            Map<String, Object> data = (Map<String, Object>) root.get("data");
		            
		            String type = data.get("Type") == null ? null : data.get("Type").toString();
		            String countryCode = data.get("CountryCode") == null ? null : data.get("CountryCode").toString();
		            String passportNo = data.get("PassportNo") == null ? null : data.get("PassportNo").toString();
		            if(type == null) {
		            	flag = true;
		            } if(countryCode == null) {
		            	flag = true;
		            } if(passportNo == null) {
		            	flag = true;
		            }
		            allNull = data.values().stream().allMatch(value -> value == null);
		            
    		 }
    		 if(resp.get("data").isEmpty() || allNull || flag) {
    			 Map<String,Object> errorMap = new HashMap<>();
    			 errorMap.put("Message", "Failed");
    			 errorMap.put("IsError", true);
    			 errorMap.put("Result", null);
    			 errorMap.put("ErroCode", 0);
    			 Map<String,Object> errorMessage = new HashMap<>();
    			 errorMessage.put("Code", "101");
    			 errorMessage.put("Field", "PASSPORT UPLOAD");
    			 errorMessage.put("Message", "INVALID DOCUMENT: PLEASE UPLOAD PASSPORT");
    			 errorMessage.put("FieldLocal", "");
    			 errorMessage.put("MessageLocal", "");
    			 errorMap.put("ErrorMessage", Arrays.asList(errorMessage));
    			 
	            return ResponseEntity.status(200).body(errorMap);
	            }
    		 JsonNode res = passportService.savePassportDetails(resp,request,token);
    		 Map<String,Object> response = new HashMap<>();
    		 response.put("UploadedPassportDetails", resp);
    		 response.put("SavePassportDetails", res);
    		return ResponseEntity.ok(response);
    	}catch(Exception e) {
    		return ResponseEntity.status(500).body("Error Processing document: "+e.getMessage());
    	}
    }
	
	@PostMapping("pincertificate")
    public ResponseEntity<CommonRes> PinCertificateAnalyse(@RequestParam("file") MultipartFile file, @RequestParam("req") Object req){
		System.out.println("Document inserts");
		CommonRes response = new  CommonRes();
		try {
			JsonNode resp = passportService.pinCertificateAiResult(file);
			boolean flag = false;
            boolean allNull = false;
            String pinNo="",customerName="";
            if(!resp.get("data").isEmpty()) {
            	String jsonResp = resp.toString();
	            ObjectMapper mapper = new ObjectMapper();
	            Map<String,Object> root = mapper.readValue(jsonResp, Map.class);
	            Map<String, Object> data = (Map<String, Object>) root.get("data");
            	pinNo = data.get("PersonalIdNo") == null ? null : data.get("PersonalIdNo").toString();
            	customerName = data.get("Name") == null ? null : data.get("Name").toString();
            	
            	if(StringUtils.isNoneBlank(pinNo)) {
            		Boolean validation = passportService.checkPinNumber(pinNo,req);
            		
            		if(validation) {
            			response.setMessage("Success");
			            response.setCommonResponse("KRA PIN is mismatched");
			   			response.setIsError(false);
			   			response.setErrorMessage(Collections.emptyList());
			            return ResponseEntity.ok(response);
            		}
            		
            	}
	      
	    		}else {
	    			allNull = true;
	    		}
				if (pinNo == null) {
					flag = true;
				}
				if (customerName == null) {
					flag = true;
				}
				 if(resp == null || allNull || flag) {
			   			response.setMessage("Failed");
			   			response.setCommonResponse(null);
			   			response.setIsError(true);
			   			response.setErroCode(0);
			   			com.maan.eway.error.Error error = new com.maan.eway.error.Error();
			   			error.setCode("101");
			   			error.setField("DOCUMENT UPLOAD");
			   			error.setMessage("INVALID DOCUMENT: PLEASE UPLOAD PIN CERTIFICATE DOCUMENT");
			   			
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
	
	@PostMapping("ai")
    public ResponseEntity<?> aiDocAnalyse(@RequestParam("file") MultipartFile file, @RequestParam("req") Object req,@RequestHeader("Authorization") String token){
		System.out.println("Document inserts");
		CommonRes response = new  CommonRes();
		try {
			JsonNode resp = passportService.generateDocResultFromAI(file,req);
			boolean flag = false;
            boolean allNull = false;
            String pinNo="",customerName="",idNumber="",serialNumber="",regNo="",engineNo="",chassisNo="";
            ObjectMapper mapper = new ObjectMapper();
			Map<String,Object> request = mapper.readValue(req.toString(),
                                            new TypeReference<Map<String, Object>>() {});
			String docType = request.get("DocType") == null ? "" : request.get("DocType").toString();
			
            	if("IdNumber".equalsIgnoreCase(docType)) {
            		if(!resp.get("data").isEmpty()) {
                    	String jsonResp = resp.toString();
        	           // ObjectMapper mapper = new ObjectMapper();
        	            Map<String,Object> root = mapper.readValue(jsonResp, Map.class);
        	            Map<String, Object> data = (Map<String, Object>) root.get("data");
        	            idNumber = data.get("IdNumber") == null ? null : data.get("IdNumber").toString();
        	            serialNumber = data.get("SerialNumber") == null ? null : data.get("SerialNumber").toString();
        	            customerName = data.get("FullName") == null ? null : data.get("FullName").toString();
                    	
                    	if(StringUtils.isNoneBlank(idNumber)) {
                    		Boolean validation = passportService.checkIdNumber(idNumber,req);
                    		
                    		if(validation) {
                    			response.setMessage("Success");
        			            response.setCommonResponse("ID Number is mismatched");
        			   			response.setIsError(false);
        			   			response.setErrorMessage(Collections.emptyList());
        			            return ResponseEntity.ok(response);
                    		}
                    		
                    	}
        	      
        	    		}else {
        	    			allNull = true;
        	    		}
        				if (idNumber == null) {
        					flag = true;
        				}
        				if (serialNumber == null) {
        					flag = true;
        				}
        				if (customerName == null) {
        					flag = true;
        				}
        				 if(resp == null || allNull || flag) {
        			   			response.setMessage("Failed");
        			   			response.setCommonResponse(null);
        			   			response.setIsError(true);
        			   			response.setErroCode(0);
        			   			com.maan.eway.error.Error error = new com.maan.eway.error.Error();
        			   			error.setCode("101");
        			   			error.setField("DOCUMENT UPLOAD");
        			   			error.setMessage("INVALID DOCUMENT: PLEASE UPLOAD VALID ID DOCUMENT");
        			   			
        			   			response.setErrorMessage(Arrays.asList(error));
        			   			
        				        return ResponseEntity.status(200).body(response);
        				       }
        			            response.setMessage("Success");
        			            response.setCommonResponse(resp);
        			   			response.setIsError(false);
        			   			response.setErrorMessage(Collections.emptyList());
        			            return ResponseEntity.ok(response);
    			}else if ("Passport".equalsIgnoreCase(docType)) {
    				if(!resp.get("data").isEmpty()) {
    	    			 String jsonResp = resp.toString();
    			           // ObjectMapper mapper = new ObjectMapper();
    			            Map<String,Object> root = mapper.readValue(jsonResp, Map.class);
    			            Map<String, Object> data = (Map<String, Object>) root.get("data");
    			            
    			            String type = data.get("Type") == null ? null : data.get("Type").toString();
    			            String countryCode = data.get("CountryCode") == null ? null : data.get("CountryCode").toString();
    			            String passportNo = data.get("PassportNo") == null ? null : data.get("PassportNo").toString();
    			            if(type == null) {
    			            	flag = true;
    			            } if(countryCode == null) {
    			            	flag = true;
    			            } if(passportNo == null) {
    			            	flag = true;
    			            }
    			            allNull = data.values().stream().allMatch(value -> value == null);
    			            
    	    		 }
    	    		 if(resp.get("data").isEmpty() || allNull || flag) {
    	    			 Map<String,Object> errorMap = new HashMap<>();
    	    			 errorMap.put("Message", "Failed");
    	    			 errorMap.put("IsError", true);
    	    			 errorMap.put("Result", null);
    	    			 errorMap.put("ErroCode", 0);
    	    			 Map<String,Object> errorMessage = new HashMap<>();
    	    			 errorMessage.put("Code", "101");
    	    			 errorMessage.put("Field", "PASSPORT UPLOAD");
    	    			 errorMessage.put("Message", "INVALID DOCUMENT: PLEASE UPLOAD PASSPORT");
    	    			 errorMessage.put("FieldLocal", "");
    	    			 errorMessage.put("MessageLocal", "");
    	    			 errorMap.put("ErrorMessage", Arrays.asList(errorMessage));
    	    			 
    		            return ResponseEntity.status(200).body(errorMap);
    		            }
    	    		 JsonNode res = passportService.savePassportDetails(resp,request,token);
    	    		 Map<String,Object> responses = new HashMap<>();
    	    		 responses.put("UploadedPassportDetails", resp);
    	    		 responses.put("SavePassportDetails", res);
    	    		return ResponseEntity.ok(responses);
    			}else if("KRAPin".equalsIgnoreCase(docType)) {
    				if(!resp.get("data").isEmpty()) {
    	            	String jsonResp = resp.toString();
    		           // ObjectMapper mapper = new ObjectMapper();
    		            Map<String,Object> root = mapper.readValue(jsonResp, Map.class);
    		            Map<String, Object> data = (Map<String, Object>) root.get("data");
    	            	pinNo = data.get("PersonalIdNo") == null ? null : data.get("PersonalIdNo").toString();
    	            	customerName = data.get("Name") == null ? null : data.get("Name").toString();
    	            	
    	            	if(StringUtils.isNoneBlank(pinNo)) {
    	            		Boolean validation = passportService.checkPinNumber(pinNo,req);
    	            		
    	            		if(validation) {
    	            			response.setMessage("Success");
    				            response.setCommonResponse("KRA PIN is mismatched");
    				   			response.setIsError(false);
    				   			response.setErrorMessage(Collections.emptyList());
    				            return ResponseEntity.ok(response);
    	            		}
    	            		
    	            	}
    		      
    		    		}else {
    		    			allNull = true;
    		    		}
    					if (pinNo == null) {
    						flag = true;
    					}
    					if (customerName == null) {
    						flag = true;
    					}
    					 if(resp == null || allNull || flag) {
    				   			response.setMessage("Failed");
    				   			response.setCommonResponse(null);
    				   			response.setIsError(true);
    				   			response.setErroCode(0);
    				   			com.maan.eway.error.Error error = new com.maan.eway.error.Error();
    				   			error.setCode("101");
    				   			error.setField("DOCUMENT UPLOAD");
    				   			error.setMessage("INVALID DOCUMENT: PLEASE UPLOAD PIN CERTIFICATE DOCUMENT");
    				   			
    				   			response.setErrorMessage(Arrays.asList(error));
    				   			
    					        return ResponseEntity.status(200).body(response);
    					       }
    				            response.setMessage("Success");
    				            response.setCommonResponse(resp);
    				   			response.setIsError(false);
    				   			response.setErrorMessage(Collections.emptyList());
    				            return ResponseEntity.ok(response);
    			}else if("LogBook".equalsIgnoreCase(docType)) {
    				if(!resp.get("data").isEmpty()) {
    	            	String jsonResp = resp.toString();
    		           // ObjectMapper mapper = new ObjectMapper();
    		            Map<String,Object> root = mapper.readValue(jsonResp, Map.class);
    		            Map<String, Object> data = (Map<String, Object>) root.get("data");
    	            	pinNo = data.get("PersonalIdNo") == null ? null : data.get("PersonalIdNo").toString();
    	            	regNo = data.get("Registration") == null ? null : data.get("Registration").toString();
    	            	chassisNo = data.get("Registration") == null ? null : data.get("Registration").toString();
    	            	engineNo = data.get("Registration") == null ? null : data.get("Registration").toString();
    	            	
    	            	if(StringUtils.isNoneBlank(pinNo)) {
    	            		Boolean validation = passportService.checkLogBook(pinNo,regNo,chassisNo,engineNo,req);
    	            		
    	            		if(validation) {
    	            			response.setMessage("Success");
    				            response.setCommonResponse("Log Book is mismatched");
    				   			response.setIsError(false);
    				   			response.setErrorMessage(Collections.emptyList());
    				            return ResponseEntity.ok(response);
    	            		}
    	            		
    	            	}
    		      
    		    		}else {
    		    			allNull = true;
    		    		}
    					if (pinNo == null) {
    						flag = true;
    					}
    					if (regNo == null) {
    						flag = true;
    					}
    					if (chassisNo == null) {
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
    				   			error.setField("DOCUMENT UPLOAD");
    				   			error.setMessage("INVALID DOCUMENT: PLEASE UPLOAD LOGBOOK DOCUMENT");
    				   			
    				   			response.setErrorMessage(Arrays.asList(error));
    				   			
    					        return ResponseEntity.status(200).body(response);
    					       }
    				            response.setMessage("Success");
    				            response.setCommonResponse(resp);
    				   			response.setIsError(false);
    				   			response.setErrorMessage(Collections.emptyList());
    				            return ResponseEntity.ok(response);
    			}
            
            return ResponseEntity.status(417).body(response);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(417).body(response);
		}
	}
    

}
