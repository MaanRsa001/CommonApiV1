package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.common.res.SuccessRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ValuationCompanyChangeStatusReq;
import com.maan.eway.master.req.ValuationCompanyMasterDropdownReq;
import com.maan.eway.master.req.ValuationCompanyMasterGetReq;
import com.maan.eway.master.req.ValuationCompanyMasterGetallReq;
import com.maan.eway.master.req.ValuationCompanyMasterSaveReq;
import com.maan.eway.master.res.ValuationCompanyMasterRes;
import com.maan.eway.master.service.ValuationCompanyMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags="MASTER : Valuation Company MASTER",description="API's")
@RequestMapping("/master")
public class ValuationCompanyMasterController {
	
	@Autowired
	private ValuationCompanyMasterService service;
	
	@Autowired
	private PrintReqService reqPrinter;

	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;

	//save
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/insertvaluationcompany")
	@ApiOperation(value = "This Method is to save Valuation Company Master")
	public ResponseEntity<CommonRes> saveValuation(@RequestBody ValuationCompanyMasterSaveReq req){
		
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = service.validateValuationCompany(req);
		List<Error> validation = null;
		
		
		if(validationCodes !=null && validationCodes.size() >0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getCompanyId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		
		//validation
		if(validation != null && validation.size() !=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
		}
		else {
			//save
			SuccessRes res = service.saveValuationCompany(req); 
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			
			if(res != null) {
				return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			}
		}
		
	}
	
	//Get All Valuation Company Master
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getallvaluationcompany")
	@ApiOperation(value = "This Method is get Active Valuation Company Master")
	public ResponseEntity<CommonRes> getActiveValuationCompany(@RequestBody ValuationCompanyMasterGetallReq req){
		
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<ValuationCompanyMasterRes> res = service.getActiveValuationCompany(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.EMPTY_LIST);
		data.setIsError(false);
		data.setMessage("Success");
		if(res != null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
		
		
	}
	
	// Get By CompanyCode
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getbyvaluationcompanycode")
	@ApiOperation("This Method is to get by Valuation Company Code")
	public ResponseEntity<CommonRes> getByValuationCompanyCode(@RequestBody ValuationCompanyMasterGetReq req){
		
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		ValuationCompanyMasterRes res = service.getByValuationCompanyCode(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.EMPTY_LIST);
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res != null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
		
	}
	
	//Change Status
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/valuationcompany/changestatus")
	@ApiOperation(value = "This method is get Valuation Company Change Status")
	public ResponseEntity<CommonRes> cheangeCompanySatatus(@RequestBody ValuationCompanyChangeStatusReq req){
		
		CommonRes data = new CommonRes();
		SuccessRes res = service.changeStatusOfCompany(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.EMPTY_LIST);
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res != null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	//Company DropDown type
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value="/dropdown/companyvaluation",produces = "application/json")
	@ApiOperation(value = "This method is get Company Valuation Master Drop Down")
	public ResponseEntity<DropdownCommonRes> getCompanyValuationDropdown(@RequestBody ValuationCompanyMasterDropdownReq req){
		
		DropdownCommonRes data = new DropdownCommonRes();
		
		List<DropDownRes> res = service.getValuationCompanyMasterDropdown(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.EMPTY_LIST);
		data.setMessage("Success");
		
		if(res != null) {
			return new ResponseEntity<DropdownCommonRes>(data,HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}				
	}
}
