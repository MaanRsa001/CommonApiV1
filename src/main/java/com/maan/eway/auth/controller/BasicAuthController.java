//package com.maan.eway.auth.controller;
//
//import java.util.Collections;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.maan.eway.auth.dto.ChangePasswordReq;
//import com.maan.eway.auth.dto.CommonLoginRes;
//import com.maan.eway.auth.dto.ForgetPasswordReq;
//import com.maan.eway.auth.service.AuthendicationService;
//import com.maan.eway.auth.service.LoginValidatedService;
//import com.maan.eway.error.Error;
//import com.maan.eway.res.SuccessRes;
//import com.maan.eway.service.PrintReqService;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//
//@RestController
//@Api(  tags="Basic Authentication", description = "API's.")
//@RequestMapping("/basicauth")
//public class BasicAuthController {
//
//	@Autowired
//	private PrintReqService reqPrinter;
//	
//	private Logger log = LogManager.getLogger(LoginController.class);
//	
//	@Autowired
//	private AuthendicationService authservice;
//	
//	@Autowired
//	private LoginValidatedService loginValidationComponent;
//	
//	/*
//	@PostMapping("/getLoginEncryptResponse")   
//	private LoginEncryptResponse getLoginEncryptResponse(@RequestBody PaymentResUrlReq request , HttpServletRequest http) {
//		return authservice.getLoginEncryptResponse(request , http);
//	} */
//
//	@PostMapping("/changepassword")
//	@ApiOperation(value="This method is to change Login Password")
//	public ResponseEntity<CommonLoginRes> getChangePassword(@RequestBody ChangePasswordReq req) throws Exception {
//		reqPrinter.reqPrint(req);
//		CommonLoginRes data = new CommonLoginRes();
//		
//		// Validation
//		
//		List<Error> validation = loginValidationComponent.LoginChangePasswordValidation(req);
//		if(validation!= null && validation.size()!=0) {
//			data.setCommonResponse(null);
//			data.setIsError(true);
//			data.setErrorMessage(validation);
//			data.setMessage("Failed");
//			
//			return new ResponseEntity<CommonLoginRes>(data, HttpStatus.OK);
//		}
//		else {
//			// Save 
//			String res = authservice.LoginChangePassword(req);
//			data.setCommonResponse(res);
//			data.setIsError(false);
//			data.setErrorMessage(Collections.emptyList());
//			data.setMessage("Success");
//			
//			if( res !=null && StringUtils.isNotBlank(res)  ) {
//				return new ResponseEntity<CommonLoginRes>(data, HttpStatus.CREATED);
//			}
//			else {
//				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//			}
//		}
//	
//	}
//	
//	@PostMapping("/forgotpassword")
//	public  ResponseEntity<CommonLoginRes> getForgotPwd(@RequestBody ForgetPasswordReq req){
//		
//		reqPrinter.reqPrint(req);
//		CommonLoginRes data = new CommonLoginRes(); 
//		List<Error> validation = loginValidationComponent.forgetPwdValidation(req);
//		if(validation!= null && validation.size()!=0) {
//			data.setCommonResponse(null);
//			data.setIsError(true);
//			data.setErrorMessage(validation);
//			data.setMessage("Failed");
//			
//			return new ResponseEntity<CommonLoginRes>(data, HttpStatus.OK);
//		}
//		else {
//			// Save 
//			SuccessRes res = authservice.LoginForgetPassword(req);
//			data.setCommonResponse(res);
//			data.setIsError(false);
//			data.setErrorMessage(Collections.emptyList());
//			data.setMessage("Success");
//			
//			if( res !=null  ) {
//				return new ResponseEntity<CommonLoginRes>(data, HttpStatus.CREATED);
//			}
//			else {
//				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//			}
//		}
//		
//	}
//}
