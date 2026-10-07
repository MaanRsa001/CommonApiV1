package com.maan.eway.reinsurance.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.reinsurance.req.ReInsuranceApprovedReq;
import com.maan.eway.reinsurance.req.ReInsuranceFreezeReq;
import com.maan.eway.reinsurance.req.ReInsuranceQuoteReq;
import com.maan.eway.reinsurance.res.ReInsuranceCommonRes;
import com.maan.eway.reinsurance.res.ReInsuranceUnfreezeRes;
import com.maan.eway.reinsurance.res.SectionCoverRes;
import com.maan.eway.reinsurance.res.ViewReInsuranceDetails;

import java.util.List;

public interface ReinsuranceService {
	
	CommonRes insertReinsurance(ReInsuranceQuoteReq req);
	
	ViewReInsuranceDetails viewReinsuranceDetials(String quoteNo);
	
	CommonRes updateReInsurance(ViewReInsuranceDetails req);

	CommonRes pushReInsuranceDetails(ReInsuranceQuoteReq req);
	
	CommonRes updatePolicyNo(ReInsuranceQuoteReq req);
	
	ReInsuranceCommonRes updateReInsuranceApprovedStatus(ReInsuranceApprovedReq req);
	
	ReInsuranceUnfreezeRes updateReInsuranceUnfreezeStatus(ReInsuranceFreezeReq req);

	List<SectionCoverRes> getSectionCovers(String companyId);
}
