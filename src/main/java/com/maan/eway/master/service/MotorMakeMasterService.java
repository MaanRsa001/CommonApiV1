package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.MotorMakeChangeStatusReq;
import com.maan.eway.master.req.MotorMakeGetAllReq;
import com.maan.eway.master.req.MotorMakeGetReq;
import com.maan.eway.master.req.MotorMakeSaveReq;
import com.maan.eway.master.res.MotorMakeGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface MotorMakeMasterService {

	List<String> validateMakeMotor(MotorMakeSaveReq req);

	SuccessRes saveMakeMotor(MotorMakeSaveReq req);

	MotorMakeGetRes getMakeId(MotorMakeGetReq req);

	List<MotorMakeGetRes> getallMotorMake(MotorMakeGetAllReq req);

	List<MotorMakeGetRes> getactiveMotorMake(MotorMakeGetAllReq req);

	List<DropDownRes> getMotorMakeDropdown(MotorMakeGetAllReq req);

	SuccessRes changeStatusOfMotorMake(MotorMakeChangeStatusReq req);



}