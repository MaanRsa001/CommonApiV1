package com.maan.eway.integration.service;

import java.util.List;

import com.maan.eway.admin.res.GetallPortfolioActiveRes;
import com.maan.eway.integration.req.GetAllPolicy;
import com.maan.eway.integration.req.IntegrationStateByPolicyReq;
import com.maan.eway.integration.req.PremiaGetReq;
import com.maan.eway.integration.res.CreditLimitDetailGetRes;
import com.maan.eway.integration.res.IntegrationStatgingRes;
import com.maan.eway.integration.res.MotCommDiscountDetailGetRes;
import com.maan.eway.integration.res.MotDriverDetailGetRes;
import com.maan.eway.integration.res.PgithPolRiskAddlInfoGetRes;
import com.maan.eway.integration.res.YiChargeDetailsGetRes;
import com.maan.eway.integration.res.YiCoverDetailsGetRes;
import com.maan.eway.integration.res.YiPolicyApprovalGetRes;
import com.maan.eway.integration.res.YiPolicyDetailsGetRes;
import com.maan.eway.integration.res.YiPremCalGetRes;
import com.maan.eway.integration.res.YiSectionDetailGetRes;
import com.maan.eway.integration.res.YiVatDetailGetRes;

public interface IntegrationGetService {

	List<YiPolicyDetailsGetRes> getYiPolicyDetails(PremiaGetReq req);

	List<YiChargeDetailsGetRes> getYiChargeDetails(PremiaGetReq req);

	List<YiCoverDetailsGetRes> getYiCoverDetails(PremiaGetReq req);

	List<YiPolicyApprovalGetRes> getYiPolicyApproval(PremiaGetReq req);

	List<YiPremCalGetRes> getYiPremCal(PremiaGetReq req);

	List<YiSectionDetailGetRes> getYiSectionDetail(PremiaGetReq req);

	List<YiVatDetailGetRes> getVatDetail(PremiaGetReq req);

	List<MotDriverDetailGetRes> getMotDriverDetail(PremiaGetReq req);

	List<CreditLimitDetailGetRes> getCreditLimitDetail(PremiaGetReq req);

	List<MotCommDiscountDetailGetRes> getMotCommDiscountDetail(PremiaGetReq req);

	List<PgithPolRiskAddlInfoGetRes> getPgithPolRiskAddlInfo(PremiaGetReq req);

	GetallPortfolioActiveRes getAllPolicyDetails(GetAllPolicy req);

	List<IntegrationStatgingRes> getIntegrationStageDetails(IntegrationStateByPolicyReq req);

	

}
