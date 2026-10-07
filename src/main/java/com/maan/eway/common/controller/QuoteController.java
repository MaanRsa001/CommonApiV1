package com.maan.eway.common.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.common.req.ChangeFinalyzereq;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.req.DeleteOldQuoteReq;
import com.maan.eway.common.req.EmployeeCountGetReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.SectionSumInsuredGetReq;
import com.maan.eway.common.req.TracesRemovedReq;
import com.maan.eway.common.req.UpdatePolicyStartEndDateReq;
import com.maan.eway.common.req.UpdateQuoteStatusReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.common.service.impl.QuoteThreadServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CoInsuranceSaveReq;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.res.GetEmployeeCountRes;
import com.maan.eway.res.GroupSuminsuredDetailsRes;
import com.maan.eway.res.SectionWiseSumInsuredRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/quote")
public class QuoteController {

	@Autowired
	private  PrintReqService reqPrinter;
	
	@Autowired
	private  QuoteService entityService ;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	@Autowired
	private FactorRateRequestDetailsRepository facRateRepo ;
	
	@Autowired
	private QuoteThreadServiceImpl quoteThreadimpl;
	
	private final ConcurrentHashMap<String, Boolean> processingRequests = 
		    new ConcurrentHashMap<>();
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/buypolicy")
	@ApiOperation(value = "This method is New Quote ")
	public ResponseEntity<CommonRes> generateNewQuote(@RequestBody NewQuoteReq req1) {
		System.out.println("****BuyPolicy Api Started********"+req1.getRequestReferenceNo());
		
		String refNo = req1.getRequestReferenceNo();
		
		NewQuoteReq req= entityService.frameRequest(refNo,req1);
		if(req.isEntd() || StringUtils.isBlank(req.getRequestReferenceNo()))
		{
			 req =req1;
		}
		
	    if (processingRequests.putIfAbsent(refNo, Boolean.TRUE) != null) {
	        CommonRes res = new CommonRes();
	        List<Error> errors = new ArrayList<>();
	        errors.add(new Error("01", "Duplicate Request", 
	            "Request " + refNo + " is already being processed"));
	        res.setIsError(true);
	        res.setErrorMessage(errors);
	        res.setMessage("Failed");
	        return new ResponseEntity<>(res, HttpStatus.OK);
	    }
		
	    try {
		reqPrinter.reqPrint(req);
		CommonRes res = new CommonRes();
		List<String> validationCodes = entityService.validateNewQuoteDetails(req);

		List<Error> validation =null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo()); 
			String companyId = covers.size() > 0 ? covers.get(0).getCompanyId()  :"" ;
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(companyId);
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("3");
			comErrDescReq.setModuleName("MASTERS");
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		//// validation
		if (validation != null && validation.size() != 0) {
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErrorMessage(validation);
			res.setMessage("Failed");
			return new ResponseEntity<CommonRes>(res, HttpStatus.OK);

		} else {
		// Save
		quoteThreadimpl.updateSection(req);
		 res = entityService.generateNewQuote(req);
		 if (res.getCommonResponse() instanceof NewQuoteRes result && result.getQuoteNo() != null) {
			    quoteThreadimpl.QuoteGenerateUwsysIdAndPolIdxID(req, result.getQuoteNo());
			}
			System.out.println("****BuyPolicy Api Ended********"+req.getRequestReferenceNo());

		if (res != null) {
			return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		}
	    }
	    finally {
	        
	        processingRequests.remove(refNo);
	    }
		

	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/deleteoldquoterecord")
	@ApiOperation(value = "This method is New Quote ")
	public ResponseEntity<CommonRes> deleteOldQuoteRecord(@RequestBody DeleteOldQuoteReq req) {
		CommonRes commonRes = new  CommonRes() ;
		reqPrinter.reqPrint(req);
		// Save
		SuccessRes res = entityService.deleteOldQuoteRecord(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/productsuminsureddetails")
	@ApiOperation(value = "This method is New Quote ")
	public ResponseEntity<CommonRes> sectionWiseSuminsuredDetails(@RequestBody SectionSumInsuredGetReq req) {
		CommonRes commonRes = new  CommonRes() ;
		reqPrinter.reqPrint(req);
		// Save
		SectionWiseSumInsuredRes res = entityService.sectionWiseSuminsuredDetails(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/groupsuminsureddetails")
	@ApiOperation(value = "This method is New Quote ")
	public ResponseEntity<CommonRes> groupSuminsuredDetails(@RequestBody SectionSumInsuredGetReq req) {
		CommonRes commonRes = new  CommonRes() ;
		reqPrinter.reqPrint(req);
		// Save
		List<GroupSuminsuredDetailsRes> res = entityService.groupSuminsuredDetails(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	}

	 
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/viewquotedetails")
	@ApiOperation(value = "This method is Get Quote Details")
	public ResponseEntity<CommonRes> viewQuoteDetails(@RequestBody ViewQuoteReq req) {
		CommonRes commonRes = new  CommonRes() ;
		reqPrinter.reqPrint(req);
		
		// Save
		ViewQuoteRes res = entityService.viewQuoteDetails(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	} 
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/update/referalstatus")
	public ResponseEntity<CommonRes> saveCustomerDetails(@RequestBody  AdminReferalStatusReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = entityService.validateReferralStatus(req);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			QuoteUpdateRes res = entityService.updateReferralStatus(req);
			if(res.getQuoteNo() != null && !res.getQuoteNo().isEmpty()) {
			quoteThreadimpl.QuoteGenerateUwsysIdAndPolIdxID(null, res.getQuoteNo());
			}
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/updatestatus")
	public ResponseEntity<CommonRes> saveCustomerDetails(@RequestBody  UpdateQuoteStatusReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = entityService.validateQuoteStatus(req);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			QuoteUpdateRes res = entityService.updateQuoteStatus(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/tracesremoved")
	@ApiOperation(value = "This method is to remove traces ")
	public ResponseEntity<CommonRes> tracesRemoved(@RequestBody TracesRemovedReq req) {
		CommonRes commonRes = new  CommonRes() ;
		reqPrinter.reqPrint(req);
		// Save
		SuccessRes res = entityService.tracesRemoved(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/productemployeecount")
	@ApiOperation(value = "This method is New Quote ")
	public ResponseEntity<CommonRes> getProductEmplyee(@RequestBody EmployeeCountGetReq req) {
		CommonRes commonRes = new  CommonRes() ;
		reqPrinter.reqPrint(req);
		// Save
		GetEmployeeCountRes res = entityService.getProductEmplyee(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")

	@PostMapping("/changefinalyzestatus")
	public ResponseEntity<CommonRes> changefinalyzestatus(@RequestBody ChangeFinalyzereq req)
	{
		CommonRes common = new CommonRes();
		SuccessRes res = entityService.changefinalyzestatus(req);
		common.setCommonResponse(res);
		common.setIsError(false);
		common.setErrorMessage(Collections.emptyList());
		common.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(common, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}


	@PostMapping("/updatepolicystartenddate")
	@ApiOperation(value = "This method is New Update Policy Start And End Date")
	public ResponseEntity<CommonRes> updatepolicystartenddate(@RequestBody UpdatePolicyStartEndDateReq req) {

		reqPrinter.reqPrint(req);
	
		CommonRes data = new CommonRes();

		List<Error> validation = entityService.validateStartdate(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Get All
			SuccessRes res = entityService.updatePolicyStartEndDate(req);
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

	// Co-Insurance Insert
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/insertcoinsurance")
	@ApiOperation(value = "This method is Insert Branch Details")
	public ResponseEntity<CommonRes> insertCoInsurance(@RequestBody List<CoInsuranceSaveReq> req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = entityService.validateCoInsurance(req);
		List<Error> validation = null;
		if (validationCodes != null && validationCodes.size() > 0) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.get(0).getCompanyId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("35");
			comErrDescReq.setModuleName("CO INSURANCE");

			validation = errorDescService.getErrorDesc(validationCodes, comErrDescReq);
		}

		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Get All
			SuccessRes res = entityService.insertCoInsurance(req);
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
	
	 
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	 
		@PostMapping("/useroptupdate")
	 
		@ApiOperation(value = "This method is New Quote ")
	 
		public ResponseEntity<CommonRes> userOptUpdate(@RequestBody NewQuoteReq req) {
	 
	 
	 
			reqPrinter.reqPrint(req);
	 
			CommonRes res = new CommonRes();
	 
//			List<String> validationCodes = entityService.validateNewQuoteDetails(req);
	 
			
	 
			List<String> validationCodes =null;
	 
			
	 
			List<Error> validation =null;
	 
			if(validationCodes!=null && validationCodes.size() > 0 ) {
	 
				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
	 
				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
	 
				String companyId = covers.size() > 0 ? covers.get(0).getCompanyId()  :"" ;
	 
				comErrDescReq.setBranchCode("99999");
	 
				comErrDescReq.setInsuranceId(companyId);
	 
				comErrDescReq.setProductId("99999");
	 
				comErrDescReq.setModuleId("3");
	 
				comErrDescReq.setModuleName("MASTERS");
	 
				validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
	 
			}
	 
			//// validation
	 
			if (validation != null && validation.size() != 0) {
	 
				res.setCommonResponse(null);
	 
				res.setIsError(true);
	 
				res.setErrorMessage(validation);
	 
				res.setMessage("Failed");
	 
				return new ResponseEntity<CommonRes>(res, HttpStatus.OK);
	 
	 
	 
			} else {
	 
			// Save
	 
//			quoteThreadimpl.updateSection(req);
	 
			 res = entityService.userOptUpdate(req);
	 
			
	 
			if (res != null) {
	 
				return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED);
	 
			} else {
	 
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	 
			}
	 
			}
	 
			
	 
	 
	 
		}
	 
			
}
