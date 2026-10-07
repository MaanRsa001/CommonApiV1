package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetallSurrenderDetailsReq;
import com.maan.eway.master.req.GetoneSurvivalDetailsReq;
import com.maan.eway.master.req.InsertSurvivalReq;
import com.maan.eway.master.res.GetoneSurvivalDetailsRes;
import com.maan.eway.res.SuccessRes;

public interface SurvivalBenefitMasterService {

	List<Error> validateSurvival(InsertSurvivalReq req);

	SuccessRes insertSurvival(InsertSurvivalReq req);

	InsertSurvivalReq getallSurvivalDetails(GetallSurrenderDetailsReq req);

	GetoneSurvivalDetailsRes getoneSurvivalDetails(GetoneSurvivalDetailsReq req);

}
