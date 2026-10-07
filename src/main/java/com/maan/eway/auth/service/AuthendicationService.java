package com.maan.eway.auth.service;

import java.util.List;

import com.maan.eway.auth.dto.AuthToken2;
import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.ForgetPasswordReq;
import com.maan.eway.auth.dto.GetEncryptionkeyReq;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.dto.LogoutRequest;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.res.SuccessRes;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthendicationService {

	CommonLoginRes checkUserLogin(LoginRequest mslogin, HttpServletRequest http);
	
	CommonLoginRes checkUserOtpLogin(LoginRequest mslogin, HttpServletRequest http);
	
	String LoginChangePassword(ChangePasswordReq req);

	CommonLoginRes logout(LogoutRequest mslogin);

	SuccessRes LoginForgetPassword(ForgetPasswordReq req);

	AuthToken2 loginTokenRegenerate(LoginRequest req, HttpServletRequest http);

	String getEncryptionkey(GetEncryptionkeyReq req);

	List<ProductDropDownRes> getProductDetailByLoginId(String loginId, String companyId);


/*	CommonCrmRes LoginChangePassword(ChangePasswordReq req);

	CommonCrmRes logout(LogoutRequest mslogin);

	SuccessRes InsertLogin(InsertLoginMasterReq req);

	LoginEncryptResponse getLoginEncryptResponse(PaymentResUrlReq request, HttpServletRequest http);

	


	LoginGetRes getloginid(LoginGetReq req);

	List<LoginDetailsGetRes> getLogintDetails(LoginDetailsGetReq req); */


}
