package com.maan.eway.common.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.common.req.UpdateUserInfoLoginReq;
import com.maan.eway.common.res.LoginUserInfoGetRes;
import com.maan.eway.common.res.SuccessRes;
import com.maan.eway.common.service.UserLoginInfoService;
import com.maan.eway.repository.LoginUserInfoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserLoginInfoImpl implements UserLoginInfoService{

	@Autowired
	private LoginUserInfoRepository loginUserInfoRepo;
	
	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(UserLoginInfoImpl.class);
	
	@Override
	public SuccessRes updateLoginUserInfo(UpdateUserInfoLoginReq req) {
		DozerBeanMapper mapper=new DozerBeanMapper();
		SuccessRes res = new SuccessRes();
		try {
		LoginUserInfo loginuser= loginUserInfoRepo.findByLoginId(req.getLoginId());
		
		loginuser.setMobileCode(req.getMobileCode());
		loginuser.setUserMobile(req.getUserMobile());
		loginuser.setUserMail(req.getUserMail());
		
		loginUserInfoRepo.saveAndFlush(loginuser);
		 res.setResponse("Update Claim Intimation Details successfully");
			res.setSuccessId(loginuser.getLoginId());
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public LoginUserInfoGetRes getByLoginId(UpdateUserInfoLoginReq req) {
		DozerBeanMapper mapper = new DozerBeanMapper();
		// Fetch entity from the repository
		LoginUserInfo loginUser = loginUserInfoRepo.findByLoginId(req.getLoginId());

		if (loginUser != null) {
		    // Correct way to map entity to DTO
			LoginUserInfoGetRes userLoginGetRes = mapper.map(loginUser, LoginUserInfoGetRes.class);
		    
		    return userLoginGetRes;  
		} else {
		    return null;
		}
	}

	
}
