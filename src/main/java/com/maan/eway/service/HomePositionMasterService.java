package com.maan.eway.service;

import java.util.List;

import com.maan.eway.master.req.GetPolicyDetailsReq;
import com.maan.eway.master.req.GetQuoteCountReq;
import com.maan.eway.master.req.GetQuoteDetailsReq;
import com.maan.eway.res.GetPolicyDetailsRes;
import com.maan.eway.res.GetQuoteCountRes;
import com.maan.eway.res.GetQuoteDetailsRes;

public interface HomePositionMasterService {

	List<GetQuoteCountRes> getCustomerQuoteCount(GetQuoteCountReq req);

	List<GetQuoteDetailsRes> getCustomerQuoteDetails(GetQuoteDetailsReq req);

	List<GetPolicyDetailsRes> getCustomerPolicyDetails(GetPolicyDetailsReq req);

}
