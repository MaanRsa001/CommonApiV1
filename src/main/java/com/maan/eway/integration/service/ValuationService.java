package com.maan.eway.integration.service;

import java.util.List;

import com.maan.eway.integration.req.ValuationDetailsReq;
import com.maan.eway.integration.req.ValuationListReq;
import com.maan.eway.integration.req.ValuationReq;
import com.maan.eway.integration.req.ValuationStatusReq;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.res.ValuationListRes;

public interface ValuationService {

	PremiaResponse pushValuation(ValuationReq req);

	PremiaResponse getStatus(ValuationStatusReq req);

	PremiaResponse getDetails(ValuationDetailsReq req);

	List<ValuationListRes> getValuationList(ValuationListReq req);

	List<ValuationStatusReq> getValuationStatusPendingList();

	

	
}
