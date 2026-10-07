package com.maan.eway.coinsurance.service;

import java.util.Map;

import com.maan.eway.coinsurance.req.DetailsReq;
import com.maan.eway.coinsurance.req.QuoteNoReq;
import com.maan.eway.common.res.CommonRes;

public interface CoinsuranceService {

	Map<Boolean, String> validationShare(DetailsReq req);

	CommonRes insertCo( QuoteNoReq req);

	CommonRes insertCoDetails(DetailsReq req);

	CommonRes getDetails(QuoteNoReq req);

	Map<Boolean, String> validationRole(DetailsReq req);

	//CommonRes updateCoDetails(@Valid DetailsReq req);

	

}
