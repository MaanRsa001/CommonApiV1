package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.ClaimHistoryInfoGetReq;
import com.maan.eway.common.req.ClaimHistoryInfoSaveReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;

public interface ClaimHistoryInfoService {
	
	public List<Error> validateClaimHistoryInfo(ClaimHistoryInfoSaveReq req);
	
	public CommonRes saveUpdateClaimHistoryInfo(ClaimHistoryInfoSaveReq req);
	
	public CommonRes getClaimHistoryInfo(ClaimHistoryInfoGetReq req);

}
