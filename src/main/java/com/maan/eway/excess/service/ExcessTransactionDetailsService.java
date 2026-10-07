package com.maan.eway.excess.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.excess.req.ExcessInsertDetailsReq;
import com.maan.eway.excess.req.ExcessTransactionReq;

public interface ExcessTransactionDetailsService {

	CommonRes getExcessTransactionDetails(ExcessTransactionReq req);

	CommonRes insertExessTransactionDetails(ExcessTransactionReq req);

	CommonRes insertExess(ExcessInsertDetailsReq req);

}
