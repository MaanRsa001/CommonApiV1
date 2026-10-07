package com.maan.eway.crm.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.maan.eway.admin.req.BrokerLoginGridReq;
import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.crm.bean.CustomerDetail;
import com.maan.eway.crm.bean.QuoteReq;
import com.maan.eway.crm.bean.UserLoginResponseData;

public interface CrmService {

	UserLoginResponseData validateTokenForCRM(String token);

	List<ProductDropDownRes> getProductDetailByLoginId(String loginId, String companyId);

	void updatePassword(ChangePasswordReq req, String url);

	String getEnqiryDetail(Long enquiryId, String token);

	List<EserviceCustomerDetails> getCustomerDetailByLeadseqNo(Long leadSeqNo, String companyId, String token);

	ResponseEntity<CommonRes> getApproverDropDownByClientId(BrokerLoginGridReq req);

	ResponseEntity<CommonRes> updateCustomerProductId(CustomerDetail customerDetailReq);

	ResponseEntity<CommonRes> updateCRMEnquiryQuotestatus(CustomerDetail req, String tokens);

	ResponseEntity<CommonRes> updateEnquiryQuotestatus(QuoteReq quoteReq);

	ResponseEntity<CommonRes> fetchQuoteDetailByLeqdSeqNo(Long leadId, String insuranceId);

	ResponseEntity<CommonRes> getProductDetailByCompanyId(String companyid);

	ResponseEntity<CommonRes> getsectionDetailByProductIdandCompanyId(String companyId, Integer productId);
}
