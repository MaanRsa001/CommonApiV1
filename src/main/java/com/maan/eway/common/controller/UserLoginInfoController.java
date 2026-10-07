package com.maan.eway.common.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.UpdateUserInfoLoginReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.LoginUserInfoGetRes;
import com.maan.eway.common.res.SuccessRes;
import com.maan.eway.common.service.UserLoginInfoService;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/logininfo")
@Api(tags = "User Login Info Details", description = "API's")
public class UserLoginInfoController {
	@Autowired
	private  UserLoginInfoService userlogininfoservice;
	@Autowired
	private  PrintReqService reqPrinter;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	// Update User Info
				@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
				@PostMapping("/updateuserinfo")
				@ApiOperation("This method is Update User Info Details")
				public ResponseEntity<CommonRes> updateUserInfo(@RequestBody UpdateUserInfoLoginReq req) {
					reqPrinter.reqPrint(req);
					CommonRes data = new CommonRes();
						// Save
						SuccessRes res = userlogininfoservice.updateLoginUserInfo(req);
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
				
				// Get User Info by login id
				@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
				@PostMapping("/getbyloginid")
				@ApiOperation("This Method is to get by Login Id")
				public ResponseEntity<CommonRes> getUseInfoByLoginId(@RequestBody UpdateUserInfoLoginReq req)
				{
				CommonRes data = new CommonRes();
				LoginUserInfoGetRes res = userlogininfoservice.getByLoginId(req);
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

				

}
