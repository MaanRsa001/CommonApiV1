package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetPolicyTypesubcoverReq;
import com.maan.eway.master.req.PolicyTypeMasterSubCoverSaveReq;
import com.maan.eway.master.req.PolicyTypeSubCoverMasterGetAllReq;
import com.maan.eway.master.res.PolicyTypeSubCoverMasterGetRes;
import com.maan.eway.res.SuccessRes;

public interface PolicyTypeMasterSubCoverService {

	List<Error> validatePolicyTypeSubCover(PolicyTypeMasterSubCoverSaveReq req);

	SuccessRes insertPolicyTypeSubCover(PolicyTypeMasterSubCoverSaveReq req);

	List<PolicyTypeSubCoverMasterGetRes> getallPolicyTypesubcover(PolicyTypeSubCoverMasterGetAllReq req);

	PolicyTypeSubCoverMasterGetRes getPolicyTypesubcover(GetPolicyTypesubcoverReq req);

}
