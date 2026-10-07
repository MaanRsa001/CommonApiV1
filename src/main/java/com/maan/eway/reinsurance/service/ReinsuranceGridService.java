package com.maan.eway.reinsurance.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.reinsurance.req.RIGridBrokerReq;
import com.maan.eway.reinsurance.req.RiGridReq;

public interface ReinsuranceGridService {

	CommonRes getReferralPendinglist(RiGridReq req,String Status);

	CommonRes getBrokerlist(RIGridBrokerReq req, String string);

}
