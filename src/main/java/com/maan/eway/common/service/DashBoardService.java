package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.DashBoardGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DasboardCountRes;
import com.maan.eway.common.res.DasboardListRes;
import com.maan.eway.common.res.DasboardRecentCusListRes;
import com.maan.eway.common.res.DasboardReferalPendingRes;


public interface DashBoardService {

	List<DasboardCountRes> getallCount(DashBoardGetReq req);

	DasboardListRes getallList(DashBoardGetReq req);

	List<DasboardReferalPendingRes> getallReferalPendingbyLogin(DashBoardGetReq req);

	List<DasboardRecentCusListRes> getRecentCustomerList(DashBoardGetReq req);

	CommonRes getPaymentDetailsByLoginId(String loginId);




}
