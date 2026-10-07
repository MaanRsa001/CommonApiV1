package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import com.maan.eway.admin.res.GetTravelAdminReferalPendingDetailsRes;
import com.maan.eway.admin.res.ReferalGridCriteriaAdminRes;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetMotorProtfolioPendingRes;
import com.maan.eway.common.res.GetTravelReferalDetailsRes;
import com.maan.eway.common.res.GetTravelRejectedQuoteDetailsRes;
import com.maan.eway.common.res.TravelQuoteCriteriaResponse;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;

import jakarta.persistence.Tuple;

public interface TravelGridService {

	TravelQuoteCriteriaResponse getTravelExistingQuoteDetails(ExistingQuoteReq req ,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	TravelQuoteCriteriaResponse getTravelLapsedQuoteDetails(ExistingQuoteReq req,Date before30, int limit,int offset);

	GetTravelRejectedQuoteDetailsRes getTravelRejectedQuoteDetails(ExistingQuoteReq req, Date startDate ,Date  endDate ,int limit, int offset);

	GetTravelReferalDetailsRes getTravelReferalDetails(ExistingQuoteReq req,  int limit,int offset , String Status);

	GetTravelReferalDetailsRes getTravelAdminReferalDetails(ExistingQuoteReq req, int limit,int offset , String Status);

	List<ListItemValue> getTravelCoptyQuotetListItem(CopyQuoteDropDownReq req, String itemType);

	List<Tuple> searchTravelQuote(CopyQuoteReq req, List<String> branches);

	CopyQuoteSuccessRes travelCopyQuote(CopyQuoteReq req, List<String> branches,String loginId);

	CopyQuoteSuccessRes travelEndt(CopyQuoteReq req, List<String> branches, String loginId);

	List<Tuple> getTravelReportDetails(GetallPolicyReportsReq req);

	GetMotorProtfolioPendingRes getTravelProtfolioPending(ExistingQuoteReq req, List<String> branches,
			Date today, int limit, int offset, String string);

	GetTravelAdminReferalPendingDetailsRes getTravelAdminReferalPendingDetails(RevertGridReq req, int limit, int offset,
			String string);

	List<ReferalGridCriteriaAdminRes> getTravelAdminReferalPendingDetailsCount(RevertGridReq req, String string);

	List<GetExistingBrokerListRes> getTravelProtfolioDropdownPending(ExistingBrokerUserListReq req, Date today);


	List<GetExistingBrokerListRes> getTravelExistingDropdown(ExistingBrokerUserListReq req, Date today,
			Date before30);

	List<GetExistingBrokerListRes> getBrokerUserListLapsedTravel(ExistingBrokerUserListReq req, Date today, Date before30);

	List<GetExistingBrokerListRes> getBrokerUserListTravelRejected(ExistingBrokerUserListReq req, Date today, Date before30);

	List<GetExistingBrokerListRes> getTravelReferalDropdown(ExistingBrokerUserListReq req, Date today, String string);

	List<GetExistingBrokerListRes> getAdminTravelRPDropdown(ExistingBrokerUserListReq req, Date today);

	List<GetExistingBrokerListRes> getTravelAdminReferalDropdown(ExistingBrokerUserListReq req, Date today,
			String string);


}
