package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.GetProductMasterReq;
import com.maan.eway.common.req.ProductStructureMasterReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.ProductStructureMasterRes;
import com.maan.eway.common.res.ProductStructureMasterResponse;
import com.maan.eway.common.service.InsuranceTypeMasterService;
import com.maan.eway.error.Error;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class InsuranceTypeMasterController {
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired 
	private InsuranceTypeMasterService entityService ; 
	
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping(value="/SaveProductStructure",produces = "application/json")
public  ResponseEntity<CommonRes> insertProductStructure(@RequestBody  ProductStructureMasterReq req){
	reqPrinter.reqPrint(req);
	CommonRes data= new CommonRes();
	List<Error> validation = entityService.validationInsuranceTypeMaster(req);
	if (validation != null && validation.size() != 0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

	} else {
		//save
     data=entityService.saveproductMaster(req);
	if (data != null) {
	return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	}
}
@PostMapping("/getAllProductStructureMaster")
@ApiOperation(value="This method is to Display Menu Service")
public ResponseEntity<CommonRes> getAllProductStructureMaster(@RequestBody GetProductMasterReq req){
CommonRes data = new CommonRes();

List<ProductStructureMasterResponse> res= entityService.getAllProductStructureMaster(req);
data.setCommonResponse(res);
data.setErrorMessage(Collections.emptyList());
data.setIsError(false);
data.setMessage("Success");
if(res!=null) {
	return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
}
else {
	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
}  
}	

@PostMapping("/getInsuranceTypeById")
@ApiOperation(value="This method is to Display Menu Service")
public ResponseEntity<CommonRes> getInsuranceMaster(@RequestBody GetProductMasterReq req){


CommonRes res= entityService.getInsuranceMaster(req);

if(res!=null) {
	return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED);
}
else {
	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
}  
}	
@PostMapping("/getByIndsutryType")
@ApiOperation(value="This method is to Industry Type")
public ResponseEntity<CommonRes> getByIndutryType(@RequestBody GetProductMasterReq req, @RequestHeader("Authorization") String token){
CommonRes data = new CommonRes();

List<ProductStructureMasterRes> res= entityService.getByIndustryTypeId(req, token);

if(res!=null) {
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
}
else {
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(true);
	data.setMessage("failed");
	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
}  
}	

@PostMapping("/deleteIndustryType")
@ApiOperation(value="This method is to Display Menu Service")
public  ResponseEntity<CommonRes> DeletedproductStructure(@RequestBody GetProductMasterReq req)
{
	CommonRes data=entityService.DeleteproductStructureMaster(req);
	if (data != null) {
	return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}

}
