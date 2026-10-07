package com.maan.eway.common.service;


import java.util.List;

import com.maan.eway.bean.ClaimIntimation;
import com.maan.eway.common.req.ClaimIntimationGetAllREq;
import com.maan.eway.common.req.ClaimIntimationGetReq;
import com.maan.eway.common.req.ClaimIntimationReq;
import com.maan.eway.common.req.ClaimIntimationUpdateReq;
import com.maan.eway.common.res.ProductRes;
import com.maan.eway.common.res.SuccessRes;

public interface ClaimIntimationService {
	
    SuccessRes insertClaimIntimation(ClaimIntimationReq req);
	public List<String> validateClaimIntimation(ClaimIntimationReq reqList) ;

    SuccessRes updateClaimIntimation(ClaimIntimationUpdateReq req);
	public List<String> validateUpdateClaimIntimation(ClaimIntimationUpdateReq reqList) ;
	List<ClaimIntimation> getallClaimIntimation(ClaimIntimationGetAllREq req);
	ClaimIntimation getByClaimReferenceNo(ClaimIntimationGetReq req);
	List<ProductRes> getProductByClaimNo(String companyId);
}
