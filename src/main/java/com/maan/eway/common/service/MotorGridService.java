package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import com.maan.eway.admin.res.GetMotorAdminReferalPendingDetailsRes;
import com.maan.eway.admin.res.GetMotorProtfolioActiveRes;
import com.maan.eway.admin.res.MotorGridCriteriaAdminRes;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SearchBrokerPolicyReq;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetMotorProtfolioPendingRes;
import com.maan.eway.common.res.GetMotorReferalDetailsRes;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.PortfolioSearchDataRes;

import com.maan.eway.common.res.QuoteCriteriaResponse;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;

import jakarta.persistence.Tuple;

public interface MotorGridService {
	
	QuoteCriteriaResponse getMotorExistingQuoteDetails(ExistingQuoteReq req , Date startDate ,Date  endDate , Integer limit , Integer offset );

	QuoteCriteriaResponse getMotorLapsedQuoteDetails(ExistingQuoteReq req, Date before30, int limit,int offset);

	GetRejectedQuoteDetailsRes getMotorRejectedQuoteDetails(ExistingQuoteReq req,  Date startDate ,Date  endDate ,int limit, int offset);

	GetMotorReferalDetailsRes getMotorReferalDetails(ExistingQuoteReq req,  int limit,int offset , String Status);

	GetMotorReferalDetailsRes getMotorAdminReferalDetails(ExistingQuoteReq req,  int limit,int offset , String Status);
	
	List<Tuple> searchMotorQuote(CopyQuoteReq req, List<String> branches);

	CopyQuoteSuccessRes motorCopyQuote(CopyQuoteReq req, List<String> branches,String loginId);

	List<ListItemValue> geMotorCoptyQuotetListItem(CopyQuoteDropDownReq req,String itemType);

	CopyQuoteSuccessRes motorEndt(CopyQuoteReq req, List<String> branches,String loginId);

	List<Tuple> validateMotorEndt(String quoteNo);

	GetMotorProtfolioActiveRes getMotorProtfolioActive(ExistingQuoteReq req, Date startDate,int limit,
			int offset, String string);

	GetMotorProtfolioPendingRes getMotorProtfolioPending(ExistingQuoteReq req,List<String> branches,Date startDate,  int limit,
			int offset, String string);

	GetMotorProtfolioActiveRes getMotorPortfolioCancelled(ExistingQuoteReq req, Date startDate,  int limit,
			int offset, String string);

	List<Tuple> getMotorIssuerQuoteDetails(IssuerQuoteReq req, Date startDate, Date endDate);

	List<Tuple> getMotorReportDetails(GetallPolicyReportsReq req);

	GetMotorAdminReferalPendingDetailsRes getMotorAdminReferalPendingDetails(RevertGridReq req,int limit, int offset,
			String string);

	List<MotorGridCriteriaAdminRes> getMotorAdminReferalPendingDetailsCount(RevertGridReq req, String string);


	List<PortfolioSearchDataRes> getProtfolioSearchData(SearchBrokerPolicyReq req);

	Long getProtfolioSearchDataCount(SearchBrokerPolicyReq req);

	List<GetExistingBrokerListRes> getMotorProtfolioDropdownPending(ExistingBrokerUserListReq req, Date today);

	List<GetExistingBrokerListRes> getMotorExistingDropdown(ExistingBrokerUserListReq req, Date today, Date before30);

	List<GetExistingBrokerListRes> getBrokerUserListLapsedMotor(ExistingBrokerUserListReq req, Date today, Date before30);

	List<GetExistingBrokerListRes> getBrokerUserListMotorRejected(ExistingBrokerUserListReq req, Date today, Date before30);

	List<GetExistingBrokerListRes> getMotorRPDropdown(ExistingBrokerUserListReq req, Date today);

	List<GetExistingBrokerListRes> getMotorRADropdown(ExistingBrokerUserListReq req, Date today);

	List<GetExistingBrokerListRes> getMotorRRDropdown(ExistingBrokerUserListReq req, Date today);

	List<GetExistingBrokerListRes> getMotorREDropdown(ExistingBrokerUserListReq req, Date today);

	List<GetExistingBrokerListRes> getAdminMotorRPropdown(ExistingBrokerUserListReq req, Date today);

	List<GetExistingBrokerListRes> getMotorAdminReferalDropdown(ExistingBrokerUserListReq req, Date today,String status);

	QuoteCriteriaResponse getMotorExistingQuoteDetailsSQ(ExistingQuoteReq req , Date startDate ,Date  endDate , Integer limit , Integer offset );

	QuoteCriteriaResponse getMotorLapsedQuoteDetailsSQ(ExistingQuoteReq req, Date before30, int limit, int offset);

	GetRejectedQuoteDetailsRes getMotorRejectedQuoteSQ(ExistingQuoteReq req, Date before30, Date today, int limit,
			int offset);

	QuoteCriteriaResponse getCustomerReferenceNoMap(ExistingQuoteReq req, String companyId, String productId, Date startDate, Date endDate,
			String appId, String bdmCode, String branchCode, Integer limit, Integer offset);

	QuoteCriteriaResponse getCustomerReferenceNoMapNonMotor(ExistingQuoteReq req, String companyId, String productId,
			Date startDate, Date endDate, String appId, String bdmCode, String branchCode, Integer limit,
			Integer offset);

	QuoteCriteriaResponse getNonMotorProduct(ExistingQuoteReq req, Date before30, Date today, int limit, int offset);

	QuoteCriteriaResponse getNonMotorProductDashBoard(ExistingQuoteReq req, Date before30, Date today, int limit,
			int offset);

	

	QuoteCriteriaResponse getCustomerReferenceNoMapDB(ExistingQuoteReq req, String companyId, String productId,
			Date startDate, Date endDate, String appId, String bdmCode, String branchCode, Integer limit,
			Integer offset);


	


}
