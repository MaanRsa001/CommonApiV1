package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.req.MakePaymentRes;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveRes;
import com.maan.eway.common.req.PaymentIntegerationReq;
import com.maan.eway.common.req.PaymentResUrlReq;
import com.maan.eway.common.req.TinyUrlGetReq;
import com.maan.eway.common.req.TiraFrameReqCall;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.LoginEncryptResponse;
import com.maan.eway.common.res.PaymentIntegerationRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.common.service.ValidationForTira;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.common.service.impl.TiraIntegerationServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/payment")
@Api(tags = "PAYMENT DETAILS", description = "API's")
public class PaymentController {

	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private  PaymentService service;
	
	@Autowired
	private  TiraIntegerationServiceImpl tiraService;
	
	@Autowired
	private ValidationForTira tiraValidation;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	// Payment Details Save
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/makepayment")
	@ApiOperation(value="This method is to Save Make Payment")
	public ResponseEntity<CommonRes> makepayment(@RequestBody  MakePaymentSaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes =  service.validatemakepayment(req);
		List<Error> validation =null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.getInsuranceId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("6");
			comErrDescReq.setModuleName("MAKE PAYMENT");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		//Validation
		if(validation!=null && validation.size()!=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
		else {
			MakePaymentRes res = service.savemakepayment(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if(res !=null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	
		}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/insertpaymentdetails")
	@ApiOperation(value="This method is to Save Make Payment")
	public ResponseEntity<CommonRes> savePaymentDetails(@RequestBody  PaymentDetailsSaveReq req,@RequestHeader("Authorization") String tokens) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes =  service.validatePaymentInsert(req);
		List<Error> validation =null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.getInsuranceId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("22");
			comErrDescReq.setModuleName("INSERT PAYMENT");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		//Validation
		if(validation!=null && validation.size()!=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
		else {
			PaymentDetailsSaveRes res = service.savePaymentDetails(req,tokens.replaceAll("Bearer ", "").split(",")[0]);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if(res !=null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	
		}
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/pushtira")
	@ApiOperation(value="This method is to Push Tira")
	public ResponseEntity<CommonRes> callTiraIntegeration(@RequestBody  TiraFrameReqCall req,@RequestHeader("Authorization") String tokens) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = tiraValidation.validationForTira(req);
		List<Error> validation =null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.getCompanyid());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("101");
			comErrDescReq.setModuleName("TIRA");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		if(validation!=null && validation.size()!=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
		else {
			SuccessRes res = tiraService.callTiraIntegeration(req,tokens.replaceAll("Bearer ", "").split(",")[0]);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if(res !=null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
		}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/gettinyurl")
	@ApiOperation(value = "This method is Get Quote Details")
	public ResponseEntity<CommonRes> getTinyUrl(@RequestBody TinyUrlGetReq req) {
		reqPrinter.reqPrint(req);
		
		// Save
		CommonRes res = service.getTinyUrl(req);
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/decrypttinyurl")
	@ApiOperation(value = "This method is Decrypt Tiny Url")
	public ResponseEntity<CommonRes> decryptTinyUrl(@RequestBody PaymentResUrlReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		// Save
		LoginEncryptResponse res = service.decryptTinyUrl(req);
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
	
	// Payment Details Get
//	@PostMapping("/getpaymentinfo")
//	@ApiOperation(value="This method is to Get Payment Details")
//	public ResponseEntity<CommonRes> getPaymentInfo(@RequestBody  PaymentInfoGetReq req) {
//		reqPrinter.reqPrint(req);
//		CommonRes data = new CommonRes();
//		PaymentInfoGetRes res = service.getPaymentInfo(req);
//			data.setCommonResponse(res);
//			data.setIsError(false);
//			data.setErrorMessage(Collections.emptyList());
//			data.setMessage("Success");
//			if(res !=null) {
//				return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//			}
//			else {
//				return new ResponseEntity<>(null, HttpStatus.CREATED);
//			}
//		}
//	
//				
//	// Payment Details Getall
//	@PostMapping("/viewpaymentinfo")
//	@ApiOperation(value="This method is to Get all Payment Details")
//	public ResponseEntity<CommonRes> viewPaymentInfo(@RequestBody  PaymentInfoGetAllReq req) {
//		reqPrinter.reqPrint(req);
//		CommonRes data = new CommonRes();
//		List<PaymentInfoGetRes> res = service.viewPaymentInfo(req);
//			data.setCommonResponse(res);
//			data.setIsError(false);
//			data.setErrorMessage(Collections.emptyList());
//			data.setMessage("Success");
//			if(res !=null) {
//				return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//			}
//			else {
//				return new ResponseEntity<>(null, HttpStatus.CREATED);
//			}
//		}
//		// Payment Details Update
//		@PostMapping("/updatepayment")
//		@ApiOperation(value="This method is to Update Make Payment")
//		public ResponseEntity<CommonRes> updatemakepayment(@RequestBody  MakePaymentUpdateReq req) {
//			reqPrinter.reqPrint(req);
//			CommonRes data = new CommonRes();
//				SuccessRes res = service.updatemakepayment(req);
//				data.setCommonResponse(res);
//				data.setIsError(false);
//				data.setErrorMessage(Collections.emptyList());
//				data.setMessage("Success");
//				if(res !=null) {
//					return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//				}
//				else {
//					return new ResponseEntity<>(null, HttpStatus.CREATED);
//				}
//			}
//		
//			
//		// Payment Details Get
//		@PostMapping("/getpaymentdetails")
//		@ApiOperation(value="This method is to Get Payment Details")
//		public ResponseEntity<CommonRes> getpaymentdetails(@RequestBody  PaymentDetailsGetReq req) {
//			reqPrinter.reqPrint(req);
//			CommonRes data = new CommonRes();
//			PaymentDetailGetRes res = service.getpaymentdetails(req);
//				data.setCommonResponse(res);
//				data.setIsError(false);
//				data.setErrorMessage(Collections.emptyList());
//				data.setMessage("Success");
//				if(res !=null) {
//					return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//				}
//				else {
//					return new ResponseEntity<>(null, HttpStatus.CREATED);
//				}
//			}
//		
//					
//		// Payment Details Getall
//		@PostMapping("/getallpaymentdetails")
//		@ApiOperation(value="This method is to Get all Payment Details")
//		public ResponseEntity<CommonRes> getallpaymentdetails(@RequestBody  PaymentDetailsGetallReq req) {
//			reqPrinter.reqPrint(req);
//			CommonRes data = new CommonRes();
//			List<PaymentDetailGetRes> res = service.getallpaymentdetails(req);
//				data.setCommonResponse(res);
//				data.setIsError(false);
//				data.setErrorMessage(Collections.emptyList());
//				data.setMessage("Success");
//				if(res !=null) {
//					return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//				}
//				else {
//					return new ResponseEntity<>(null, HttpStatus.CREATED);
//				}
//			}
//	
//
//
//		// Payment Details History
//		@PostMapping("/paymentdetailshistory")
//		@ApiOperation(value="This method is to Get Payment Details History")
//		public ResponseEntity<CommonRes> paymentdetailshistory(@RequestBody  PaymentDetailsHistoryReq req) {
//			reqPrinter.reqPrint(req);
//			CommonRes data = new CommonRes();
//			List<PaymentDetailGetRes> res = service.paymentdetailshistory(req);
//				data.setCommonResponse(res);
//				data.setIsError(false);
//				data.setErrorMessage(Collections.emptyList());
//				data.setMessage("Success");
//				if(res !=null) {
//					return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//				}
//				else {
//					return new ResponseEntity<>(null, HttpStatus.CREATED);
//				}
//			}

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("/gettira/{QuoteNo}")
	@ApiOperation(value="This method is to Push Tira")
	public ResponseEntity<CommonRes> resultOfTiraIntegration(@PathVariable("QuoteNo") String quoteNo,@RequestHeader("Authorization") String tokens) {
		
			CommonRes data = new CommonRes();		
			JSONObject res = tiraService.resultOfTiraIntegration(quoteNo,tokens.replaceAll("Bearer ", "").split(",")[0]);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if(res !=null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(data, HttpStatus.CREATED);
			}
		}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("/getCreditLimit/{brokerId}")
	public CommonRes getCreditLimit(@PathVariable ("brokerId") String brokerId) {
		return service.getCreditLimit(brokerId);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/paymentStatusList")
	public ResponseEntity<CommonRes> paymentStatusList(@RequestBody PaymentIntegerationReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<PaymentIntegerationRes> res = service.paymentStatusList(req);
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
	 
	@GetMapping("/merchantReferenceNumber")

	public ResponseEntity<CommonRes> paymentReferenceNo() {

		CommonRes data = new CommonRes();

		String res = service.generateMerchantReferenceNo();

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

	@PostMapping("/generatePaymentid")

	public String generatePaymentId() {

		return service.generatePaymentid();

	}
 
		
}