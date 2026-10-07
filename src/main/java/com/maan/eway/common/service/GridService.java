package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.admin.res.GetallPortfolioActiveRes;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetApproverListReq;
import com.maan.eway.common.req.GetExistingBrokerListReq;
import com.maan.eway.common.req.GetPaymentStatusReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.GetallReferralPendingDetailsRes;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.req.PortFolioGridReq;
import com.maan.eway.common.req.PortfolioSearchReq;
import com.maan.eway.common.req.RegSearchReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SearchBrokerPolicyReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.res.AdminPendingGridRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.GetApproverListRes;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetPaymentStatusRes;
import com.maan.eway.common.res.GetallExistingRejectedLapsedRes;
import com.maan.eway.common.res.GetallPolicyReportsRes;
import com.maan.eway.common.res.GetallPortfolioPendingRes;
import com.maan.eway.common.res.GetallReferralApprovedDetailsRes;
import com.maan.eway.common.res.GetallReferralDetailsCommonRes;
import com.maan.eway.common.res.GetallReferralRejectedDetailsRes;
import com.maan.eway.common.res.PortFolioDashBoardRes;
import com.maan.eway.common.res.PortfolioByRegNoRes;
import com.maan.eway.common.res.PortfolioGridRes;
import com.maan.eway.common.res.RegNumberRes;
import com.maan.eway.common.res.RevertGridRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;
import com.maan.eway.common.res.ViewLoginDetailsRes;
import com.maan.eway.common.service.impl.PortFolioSearchGridRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface GridService {

	GetallExistingRejectedLapsedRes getallExistingQuoteDetails(ExistingQuoteReq req);

	GetallExistingRejectedLapsedRes getallLapsedQuoteDetails(ExistingQuoteReq req);

	GetallExistingRejectedLapsedRes getallRejectedQuoteDetails(ExistingQuoteReq req);

	GetallReferralPendingDetailsRes getallReferralPendingDetails(ExistingQuoteReq req);

	GetallReferralApprovedDetailsRes getallReferralApprovedDetails(ExistingQuoteReq req);

	GetallReferralRejectedDetailsRes getallReferralRejectedDetails(ExistingQuoteReq req);

	GetallReferralDetailsCommonRes getallAdminReferralPendings(ExistingQuoteReq req);

	GetallReferralDetailsCommonRes getallAdminReferralApproved(ExistingQuoteReq req);

	GetallReferralDetailsCommonRes getallAdminReferralRejected(ExistingQuoteReq req);



	List<GetAllMotorDetailsRes> getbyReqRefNo(CopyQuoteReq req);

	CopyQuoteSuccessRes copyQuote(CopyQuoteReq req);

	List<DropDownRes> copyQuoteByDropdown(CopyQuoteDropDownReq req);

	GetallReferralDetailsCommonRes getallReferralRequoteDetails(ExistingQuoteReq req);

	GetallReferralDetailsCommonRes getallAdminReferralRequote(ExistingQuoteReq req);

	List<Error> validateQuotoNo(CopyQuoteReq req);


	UpdateLapsedQuoteRes updateLapsedQuoteDetails(UpdateLapsedQuoteReq req);

	GetallPortfolioActiveRes getallPortfolioActive(ExistingQuoteReq req);

	GetallPortfolioPendingRes getallPortfolioPending(ExistingQuoteReq req);

	GetallPortfolioActiveRes getallPortfolioCancelled(ExistingQuoteReq req);

	List<DropDownRes> getallIssuerQuoteDetails(IssuerQuoteReq req);

	List<GetallPolicyReportsRes> getallPolicyReports(GetallPolicyReportsReq req);

	List<PortFolioDashBoardRes> getAllAdminPortfolio(PortFolioDashBoardReq req);

	List<PortFolioDashBoardRes> getAllPolicyPendingDashboard(PortFolioDashBoardReq req);

	List<PortfolioGridRes> getAllPolicyGrid(PortFolioGridReq req);

	List<GetApproverListRes> getApproverList(GetApproverListReq req);

	RevertGridRes getUwPendingGrid(RevertGridReq req);

	AdminPendingGridRes getReAllotUwPendingGrid(RevertGridReq req);

	SuccessRes updateUwReferralDetails(List<RevertGridReq> req);


	PortFolioSearchGridRes searchBrokerPolicies(SearchBrokerPolicyReq req);

	List<GetExistingBrokerListRes> getExistingBrokerList(GetExistingBrokerListReq req);
	
	List<GetExistingBrokerListRes> getPortfolioPendingDropdown(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getBrokerUserList(ExistingBrokerUserListReq req);


	List<GetExistingBrokerListRes> getPortfolioBrokerUserList(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getCancelPolicyIssuerDropdownList(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getBrokerUserListLapsed(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getBrokerUserListRejected(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getReferralPendingDropdown(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getReferralApprovedDropdown(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getReferralRejectDropdown(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getReferralRequoteDropdown(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getAdminReferralPendingDropdown(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getAdminReferralApproveDropdown(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getAdminReferralRejectDropdown(ExistingBrokerUserListReq req);

	List<GetExistingBrokerListRes> getAdminReferralReQuoteDropdown(ExistingBrokerUserListReq req);

	RegNumberRes getRegNumberQuotes(RegSearchReq req);

	GetPaymentStatusRes getPaymentStatus(GetPaymentStatusReq req);

	GetPaymentStatusRes getPaymentFailedStatus(GetPaymentStatusReq req);

	GetPaymentStatusRes getPaymentSucessStatus(GetPaymentStatusReq req);

	List<PortFolioDashBoardRes> getB2cAdminPortfolio(PortFolioDashBoardReq req);

	List<PortfolioGridRes> getAllPolicyB2cGrid(PortFolioGridReq req);

	List<GetExistingBrokerListRes> getReportBrokerUserList(ExistingBrokerUserListReq req);

	GetallExistingRejectedLapsedRes getallExistingQuoteSQ(ExistingQuoteReq req);

	GetallExistingRejectedLapsedRes getallLapsedQuoteDetailSQ(ExistingQuoteReq req);

	GetallExistingRejectedLapsedRes getallRejectedQuoteSQ(ExistingQuoteReq req);

	ViewLoginDetailsRes viewLoginDetails(ExistingQuoteReq req);

	List<PortfolioByRegNoRes> getAllPorfolioByRegNo(PortfolioSearchReq req);

	GetallExistingRejectedLapsedRes getallExistfromHPM(ExistingQuoteReq req);

	GetallExistingRejectedLapsedRes getDashBoardallExistfromHPM(ExistingQuoteReq req);

}
