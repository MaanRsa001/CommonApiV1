package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.ColorChangeStatusReq;
import com.maan.eway.master.req.MotorColorGetAllReq;
import com.maan.eway.master.req.MotorColorGetReq;
import com.maan.eway.master.req.MotorColorSaveReq;
import com.maan.eway.master.res.MotorColorGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface MotorColorMasterService {

	List<String> validateColorMotor(MotorColorSaveReq req);

	SuccessRes saveColor(MotorColorSaveReq req);

	MotorColorGetRes getMotorColor(MotorColorGetReq req);

	List<MotorColorGetRes> getallMotorColor(MotorColorGetAllReq req);

	List<MotorColorGetRes> getactiveMotorColor(MotorColorGetAllReq req);

	List<DropDownRes> getColorMasterDropdown(MotorColorGetAllReq req);

	SuccessRes changeStatusOfColor(ColorChangeStatusReq req);

}