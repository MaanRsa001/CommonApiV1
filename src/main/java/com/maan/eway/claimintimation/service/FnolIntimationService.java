package com.maan.eway.claimintimation.service;

import java.util.List;

import com.maan.eway.claimintimation.dto.FnolIntimationRequest;
import com.maan.eway.claimintimation.dto.GetIntimationReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;

public interface FnolIntimationService {

	CommonRes saveFnolIntimation(FnolIntimationRequest req);

	CommonRes getByIntimationNo(GetIntimationReq req);

	List<Error> validateFnolIntimation(FnolIntimationRequest req);

	CommonRes getIntimationDetailsByPolicyNo(String policyNo);

}
