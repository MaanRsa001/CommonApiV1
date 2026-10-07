package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ExchangeChangeStatusReq;
import com.maan.eway.master.req.ExchangeMasterGetReq;
import com.maan.eway.master.req.ExchangeMasterGetallReq;
import com.maan.eway.master.req.ExchangeMasterSaveReq;
import com.maan.eway.master.res.ExchangeMasterGetRes;
import com.maan.eway.master.service.ExchangeMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MASTER : Exchange Master", description = "API's")
@RequestMapping("/master")
public class ExchangeMasterController {

	@Autowired
	private ExchangeMasterService service;

	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;

	// save
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/insertexchangemaster")
	@ApiOperation(value = "This method is to Insert Exchange Master")
	public ResponseEntity<CommonRes> insertExchangeMaster(@RequestBody ExchangeMasterSaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = service.validateInsertExchangeMaster(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.getCompanyId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		// Validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Save
			SuccessRes res = service.insertExchangeMaster(req);
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

	// Get
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getexchangemaster")
	public ResponseEntity<CommonRes> getExchangeMaster(@RequestBody ExchangeMasterGetReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		ExchangeMasterGetRes res = service.getExchangeMaster(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	// Getall
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getallexchangemaster")
	public ResponseEntity<CommonRes> getallExchangeMaster(@RequestBody ExchangeMasterGetallReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<ExchangeMasterGetRes> res = service.getallExchangeMaster(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	// Active
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getactiveexchange")
	public ResponseEntity<CommonRes> getActiveExchange(@RequestBody ExchangeMasterGetallReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<ExchangeMasterGetRes> res = service.getActiveExchange(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	// Exchange Master Drop Down Type
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@GetMapping(value="/dropdown/exchange",produces = "application/json")
		@ApiOperation(value = "This method is get Exchange Master Drop Down")

		public ResponseEntity<DropdownCommonRes> getExchangeMasterDropdown() {

			DropdownCommonRes data = new DropdownCommonRes();

			// Save
			List<DropDownRes> res = service.getExchangeMasterDropdown();
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/exchange/changestatus")
	@ApiOperation(value = "This method is get Exchange Change Status")
	public ResponseEntity<CommonRes> changeStatusOfExchange(@RequestBody ExchangeChangeStatusReq req) {

		CommonRes data = new CommonRes();
		// Change Status
		SuccessRes res = service.changeStatusOfExchange(req);
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