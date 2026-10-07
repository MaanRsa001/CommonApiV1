package com.maan.eway.common.service;

import com.maan.eway.common.req.UpdateUserInfoLoginReq;
import com.maan.eway.common.res.LoginUserInfoGetRes;
import com.maan.eway.common.res.SuccessRes;


public interface UserLoginInfoService {

	SuccessRes updateLoginUserInfo(UpdateUserInfoLoginReq req);

	LoginUserInfoGetRes getByLoginId(UpdateUserInfoLoginReq req);
 
	
}
