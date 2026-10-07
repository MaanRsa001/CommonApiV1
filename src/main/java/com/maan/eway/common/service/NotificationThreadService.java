package com.maan.eway.common.service;

import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.res.QuoteUpdateRes;

public interface NotificationThreadService {

	QuoteUpdateRes getUpdateReferral(NewQuoteReq req);

	

}
