package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetPolicyTermsDetailsReq;
import com.maan.eway.master.req.GetallPolicyTermsDetailsReq;
import com.maan.eway.master.req.InsertPolicyTermsReq;
import com.maan.eway.master.res.GetallPolicyTermsDetailsRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface LifePolicyTermsMasterService {

	List<Error> validatePolicyTerms(InsertPolicyTermsReq req);

	SuccessRes insertPolicyTerms(InsertPolicyTermsReq req);

	List<GetallPolicyTermsDetailsRes> getallPolicyTermsDetails(GetallPolicyTermsDetailsReq req);

	GetallPolicyTermsDetailsRes getPolicyTermsDetails(GetPolicyTermsDetailsReq req);

	List<DropDownRes> getPolicyTermsMasterDropdown(GetallPolicyTermsDetailsReq req);

}
