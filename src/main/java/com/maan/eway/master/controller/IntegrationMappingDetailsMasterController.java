package com.maan.eway.master.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterGetAllReq;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterGetReq;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterSaveReq;
import com.maan.eway.master.service.impl.IntegrationMappingDetailsMasterServiceImpl;

@RestController
@RequestMapping("/master")
public class IntegrationMappingDetailsMasterController {
	
	@Autowired
	private IntegrationMappingDetailsMasterServiceImpl integrMappingService;
	@Autowired
	private FetchErrorDescServiceImpl errorDescService;

	@PostMapping("/saveintegrationmapping")
	public ResponseEntity<CommonRes> saveUpdateIntegrationMappingDetails(@RequestBody IntegrationMappingDetailsMasterSaveReq request){
		
		List<String> errorCodes = integrMappingService.validateIntegrationMappingDetails(request);
		
		if(errorCodes != null && !errorCodes.isEmpty()) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setModuleId("36");
			comErrDescReq.setModuleName("MASTERS");					
			comErrDescReq.setInsuranceId("100002");
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setProductId("99999");
			
			List<Error> errorList = errorDescService.getErrorDesc(errorCodes, comErrDescReq);
			
			CommonRes result = new CommonRes();
			result.setMessage("Failed");
			result.setIsError(true);			
			if(errorList != null && !errorList.isEmpty()) {
				result.setErrorMessage(errorList);				
			}
			else {
				result.setErrorMessage(List.of(new Error("404", "Status", "Error Description Is Not Found.")));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);			
		}
	
	//No Validation Error Proceeds to Save & Update
		else {
			//Save Method	
			if(request.getIntegrationId() == null) {
				CommonRes response = integrMappingService.saveIntegrationMappingDetails(request);				
				if(response == null) {
					return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
				}
				else {
					return new ResponseEntity<>(response, HttpStatus.CREATED);
				}
			}
			//Update Method
			else {
				CommonRes response = integrMappingService.updateIntegrationMappingDetails(request);
				if(response == null) {
					return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
				}
				else {
					return new ResponseEntity<>(response, HttpStatus.OK);
				}				
			}
		}
	}

	@PostMapping("/getintegrationmapping")
	public ResponseEntity<CommonRes> getIntegrationMappingDetails(@RequestBody IntegrationMappingDetailsMasterGetReq request){
		CommonRes response = integrMappingService.getIntegrationMappingDetails(request);
		if(response == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}	
	}
	
	@PostMapping("/getallintegrationmapping")
	public ResponseEntity<CommonRes> getAllIntegrationMappingDetails(@RequestBody IntegrationMappingDetailsMasterGetAllReq request){
		CommonRes response = integrMappingService.getAllIntegrationMappingDetails(request);
		if(response == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}	
	}
	
		
}
