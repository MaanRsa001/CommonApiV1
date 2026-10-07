package com.maan.eway.promoCode.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.promoCode.req.PromoCodeAgentMappingListReq;
import com.maan.eway.promoCode.req.PromoCodeHeaderListReq;
import com.maan.eway.promoCode.req.PromoCodeMappingListReq;
import com.maan.eway.promoCode.req.PromoCodeReq;

public interface PromoCodeService {

	CommonRes saveOrAmendPromoCode(PromoCodeReq req);
	CommonRes getPromoCodeHeaderList(PromoCodeHeaderListReq req);
	CommonRes getPromoCodeMappingList(PromoCodeMappingListReq req);
	CommonRes getPromoCodeAgentMappingList(PromoCodeAgentMappingListReq req);

}
