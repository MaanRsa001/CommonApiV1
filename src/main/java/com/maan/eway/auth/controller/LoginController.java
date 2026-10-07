package com.maan.eway.auth.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.auth.dto.AuthToken2;
import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.ForgetPasswordReq;
import com.maan.eway.auth.dto.GetEncryptionkeyReq;
import com.maan.eway.auth.dto.IpAddressAuthenticationRequest;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.dto.LogoutRequest;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.auth.service.LoginValidatedService;
import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.error.Error;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Api(  tags="LOGIN : Login Token Creation", description = "API.")
//@RequestMapping("/authentication")
public class LoginController {
	
	@Autowired
	private AuthendicationService authservice;
	@Autowired
	private LoginValidatedService loginValidationComponent;
	@Autowired
	private PrintReqService reqPrinter;
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@PostMapping("/authentication/login")
	@ApiOperation(value="This method is to Create Token For Access Other Apis")
	public ResponseEntity<CommonLoginRes> getloginToken(@RequestBody LoginRequest mslogin, HttpServletRequest http)  {
		CommonLoginRes res = new CommonLoginRes();
		
		reqPrinter.reqPrint(mslogin);
		res =loginValidationComponent.loginInputValidation(mslogin); 
		if(res.getErrorMessage()!=null &&  res.getErrorMessage().size()>0 ) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.OK);
		} 
		
		res = authservice.checkUserLogin(mslogin,http);
		if(res.getCommonResponse() !=null) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
	}

	@PostMapping("/authentication/logout")
	@ApiOperation(value="This method is used to Logout From Screen")
	public CommonLoginRes logout(@RequestBody LogoutRequest mslogin)  {		
		return authservice.logout(mslogin);
	}
	
	
	
	
	@PostMapping("/authentication/doauth")
	@ApiOperation(value="This method is to Create Token For Access Other Apis")
	public ResponseEntity<CommonLoginRes> getloginTokenEncrypt(@RequestBody LoginRequest msloginx, HttpServletRequest http)  {
		Map<String,Object> encValue=new HashMap<String,Object>();
		try {
			String decrypt = EncryDecryService.decrypt(URLDecoder.decode(msloginx.getEncryptionkey(), "UTF-8"));
			if (StringUtils.isNotBlank(decrypt) && decrypt.indexOf(",") != -1) {
				String[] split = decrypt.replaceAll("\\{", "").replaceAll("\\}", "").split(",");
				if (split.length > 0) {
					for (int i = 0; i < split.length; i++) {
						String text=split[i].replaceAll("\n", "").replaceAll("\r", "").replaceAll("\"", "");
						String[] keyValuePair = text.split(":");
						encValue.put(keyValuePair[0].trim(), keyValuePair[1].trim());
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		List<Error> errors=null;
		if(encValue.get("TinyUrlId")!=null) {
			boolean executeValidate=true;
			if(encValue.containsKey("SubUserType") && encValue.get("SubUserType")!=null && encValue.get("SubUserType").equals("b2c"))
				executeValidate=false;
			if(executeValidate)
			errors=loginValidationComponent.validateTinyUrlId(encValue.get("TinyUrlId").toString(),encValue.get("TinyGroupId").toString());
			if(errors==null || errors.size()==0 ) {
				LoginRequest mslogin=new LoginRequest();
				LoginMaster login = loginRepo.findByLoginId(encValue.get("LoginId").toString());
				mslogin.setLoginId(encValue.get("LoginId").toString());
				mslogin.setPassword(login.getPassword());
				mslogin.setReLoginKey("Y");
				ResponseEntity<CommonLoginRes> getloginToken = getloginToken(mslogin,http);
				CommonLoginRes body = getloginToken.getBody();
				body.setAdditionalInfo(encValue);
				
				if(body.getErrorMessage().size()>0)
					loginValidationComponent.updateTinyUrlId(encValue.get("TinyUrlId").toString(),encValue.get("TinyGroupId").toString());
				return getloginToken;
			}else {
				CommonLoginRes body=new CommonLoginRes();
				body.setErrorMessage(errors);
				body.setIsError(true);
				return new ResponseEntity<CommonLoginRes>(body, HttpStatus.OK);
			}
				
		 
		}
		CommonLoginRes body=new CommonLoginRes();
		Error err=new Error();
		err.setCode("9844");
		err.setField("TinyUrl");
		err.setMessage("Tiny Url is Not Valid");
		errors=new ArrayList<Error>();
		errors.add(err);
		body.setErrorMessage(errors);
		body.setIsError(true);
		return new ResponseEntity<CommonLoginRes>(body, HttpStatus.OK);
		
	}
	
	@PostMapping("/authentication/tokenregenrate")
	public  ResponseEntity<CommonLoginRes>  loginTokenRegenerate(@RequestBody LoginRequest req, HttpServletRequest http) throws Exception {
		CommonLoginRes res = new CommonLoginRes();
		reqPrinter.reqPrint(req);
		AuthToken2 res2 = new AuthToken2();
	//	log.info("Error-->" + json.toJson(req));
	/*	List<Error> error = loginValidationComponent.LoginTokenRegenerateValidation(req);
		if (error != null && error.size() > 0) {
			throw new CommonValidationException(error, null);
		} */
		
		res2 = authservice.loginTokenRegenerate(req,http);
		res.setCommonResponse(res2);
		res.setErrorMessage(Collections.emptyList());
		res.setIsError(false);
		res.setMessage("Success");
		if(res2 !=null) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("/api/changepassword")
	@ApiOperation(value="This method is to change Login Password")
	public ResponseEntity<CommonLoginRes> getChangePassword(@RequestBody ChangePasswordReq req) throws Exception {
		reqPrinter.reqPrint(req);
		CommonLoginRes data = new CommonLoginRes();
		
		// Validation
		
		List<Error> validation = loginValidationComponent.LoginChangePasswordValidation(req);
		if(validation!= null && validation.size()!=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			
			return new ResponseEntity<CommonLoginRes>(data, HttpStatus.OK);
		}
		else {
			// Save 
			String res = authservice.LoginChangePassword(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			
			if( res !=null && StringUtils.isNotBlank(res)  ) {
				return new ResponseEntity<CommonLoginRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	
	}
	
//	@PostMapping("/api/forgotpassword")
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
	
	@PostMapping("/api/forgotpassword")
	public ResponseEntity<CommonLoginRes> getForgotPwd(@RequestBody ForgetPasswordReq req) {

	    reqPrinter.reqPrint(req);

	    CommonLoginRes data = new CommonLoginRes();

	    List<Error> validation = loginValidationComponent.forgetPwdValidation(req);

	    if (validation != null && !validation.isEmpty()) {

	        data.setCommonResponse(null);
	        data.setIsError(true);
	        data.setErrorMessage(validation);
	        data.setMessage("Failed");

	        return ResponseEntity.ok(data);
	    }

	    SuccessRes res = authservice.LoginForgetPassword(req);

	    data.setCommonResponse(res);
	    data.setIsError(false);
	    data.setErrorMessage(Collections.emptyList());
	    data.setMessage("Success");

	    return ResponseEntity.ok(data);
	}
	
	@PostMapping("/authentication/byipaddress")
	@ApiOperation(value="This method is to Create Token For Access Other Apis")
	public ResponseEntity<CommonLoginRes> getAuthenticationByIpAddress(@RequestBody IpAddressAuthenticationRequest req, HttpServletRequest http)  {
		CommonLoginRes res = new CommonLoginRes();
		reqPrinter.reqPrint(req);
		res =loginValidationComponent.loginIpAddressValidation(req); 
		if(res.getErrorMessage()!=null &&  res.getErrorMessage().size()>0 ) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.OK);
		} 
		String guestloginId = "";
		String guestpassword = "";
		if(StringUtils.isNotBlank(req.getUserType()) && req.getUserType().equalsIgnoreCase("B2C") ) {
			guestloginId = "guest";
			guestpassword = "Admin@01";
		}
		LoginRequest mslogin = new LoginRequest();
		mslogin.setLoginId(guestloginId);
		mslogin.setPassword(guestpassword);
		mslogin.setReLoginKey("Y");
		mslogin.setIpAddress(req.getIpAddress());
		
		res =loginValidationComponent.loginInputValidation(mslogin); 
		if(res.getErrorMessage()!=null &&  res.getErrorMessage().size()>0 ) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.OK);
		} 	
		res = authservice.checkUserLogin(mslogin,http);
		if(res.getCommonResponse() !=null) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@PostMapping("/authentication/getEncryptionkey")
	public ResponseEntity<?> getEncryptionkey(@RequestBody GetEncryptionkeyReq req){
		CommonLoginRes data = new CommonLoginRes();
		reqPrinter.reqPrint(req);
		List<Error> validation = loginValidationComponent.getEncryptionkeyReq(req);
		if(validation!= null && validation.size()!=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			
			return new ResponseEntity<CommonLoginRes>(data, HttpStatus.OK);
		}
		else {
			// Save 
			String  res = authservice.getEncryptionkey(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			
			if( res !=null  ) {
				return new ResponseEntity<CommonLoginRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	}
	
}
