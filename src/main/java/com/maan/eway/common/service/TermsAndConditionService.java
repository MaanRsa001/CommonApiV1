package com.maan.eway.common.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.maan.eway.common.req.AutoInsertTermsReq;
import com.maan.eway.common.req.TermsAndConditionGetBySubIdReq;
import com.maan.eway.common.req.TermsAndConditionGetReq;
import com.maan.eway.common.req.TermsAndConditionInsertReq;
import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.TermsAndConditionGetBySubIdRes;
import com.maan.eway.common.res.TermsAndConditionGetRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;

public interface TermsAndConditionService {

	TermsAndConditionRes viewTermsAndCondition(TermsAndConditionReq req);

	List<Error> validateTermsAndCondition(TermsAndConditionInsertReq req);

	SuccessRes insertTermsAndCondition(TermsAndConditionInsertReq req);

	TermsAndConditionGetRes getTermsAndCondition(TermsAndConditionGetReq req);

	TermsAndConditionGetBySubIdRes getTermsAndConditionSubId(TermsAndConditionGetBySubIdReq req);

	ResponseEntity<CommonRes> fetchTermsAndCondition(TermsAndConditionReq req);

	ResponseEntity<CommonRes> fetchSectionsBasedOnRisk(String requestReferenceNo, Integer riskId);
	
	 TermsAndConditionRes getMasterData(TermsAndConditionReq req);

	SuccessRes insertTermsAndConditionWithLocation(TermsAndConditionInsertReq req);
	
	public void autoInsertDefaultTermsAfterBuyPolicy(AutoInsertTermsReq dto);
	
	public List<Error> validateTermsAndConditionAutoInsert(AutoInsertTermsReq req);
	
}