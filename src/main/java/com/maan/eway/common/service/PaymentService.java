package com.maan.eway.common.service;

import java.util.List;

import com.google.gson.JsonObject;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.common.req.MakePaymentRes;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.req.MakePaymentUpdateReq;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.req.PaymentDetailsGetallReq;
import com.maan.eway.common.req.PaymentDetailsHistoryReq;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveRes;
import com.maan.eway.common.req.PaymentInfoGetAllReq;
import com.maan.eway.common.req.PaymentInfoGetReq;
import com.maan.eway.common.req.PaymentIntegerationReq;
import com.maan.eway.common.req.PaymentResUrlReq;
import com.maan.eway.common.req.TinyUrlGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.LoginEncryptResponse;
import com.maan.eway.common.res.PaymentDetailGetRes;
import com.maan.eway.common.res.PaymentInfoGetRes;
import com.maan.eway.common.res.PaymentIntegerationRes;
import com.maan.eway.res.SuccessRes;

public interface PaymentService {

	List<String> validatemakepayment(MakePaymentSaveReq req);

	MakePaymentRes savemakepayment(MakePaymentSaveReq req);

	SuccessRes updatemakepayment(MakePaymentUpdateReq req);

	PaymentDetailGetRes getpaymentdetails(PaymentDetailsGetReq req);

	List<PaymentDetailGetRes> getallpaymentdetails(PaymentDetailsGetallReq req);

	PaymentInfoGetRes getPaymentInfo(PaymentInfoGetReq req);

	List<PaymentInfoGetRes> viewPaymentInfo(PaymentInfoGetAllReq req);

	List<String> validatePaymentInsert(PaymentDetailsSaveReq req);

	PaymentDetailsSaveRes savePaymentDetails(PaymentDetailsSaveReq req, String string);

	List<PaymentDetailGetRes> paymentdetailshistory(PaymentDetailsHistoryReq req);
	
	CommonRes getTinyUrl(TinyUrlGetReq req);

	LoginEncryptResponse decryptTinyUrl(PaymentResUrlReq req);

	List<PolicyDrcrDetail>  generatePolicy(PaymentInfo paymentInfo, PaymentDetailsSaveReq req, PaymentDetail paymentDetail, String token);

	CommonRes getCreditLimit(String brokerId);
	
	public List<PaymentIntegerationRes> paymentStatusList(PaymentIntegerationReq req);

	JsonObject cyberSourcePaymentDetails(PaymentVendorMaster vendor, PaymentDetail payment, PaymentInfo paymentInfo);

	String generateMerchantReferenceNo();

	String generatePaymentid();
 


}
