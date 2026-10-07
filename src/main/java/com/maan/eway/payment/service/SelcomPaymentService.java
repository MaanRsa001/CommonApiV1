package com.maan.eway.payment.service;

import java.util.Map;

import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.JsonObject;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.res.CommonRes;


public interface SelcomPaymentService {

	JsonObject createOrderForPayment(String merchantRefernceNo);

	JsonObject methodWebhook(Map<String,Object> jsObject);
	
	JsonObject lipilamethodWebhook(Map<String,Object> jsObject);

	JsonObject orderStatus(String orderId, String tokens);
	JsonObject createOrderForPayment(PaymentDetail payment); 
	
	JsonObject createOrderMinimal(String merchantRefernceNo);

	void callRSTAIntegeration(String quoteNo);

	JsonObject cybersourceorderStatusminimal(String orderId, String paymentIds, String token);

	RedirectView handleCyberSourceRedirect(MultiValueMap<String, String> formData);
	
	JsonObject orderCancel(String merchantRefernceNo);

	CommonRes checkPaymentDetails(PaymentDetailsGetReq req);

	JsonObject createOrderForPaymentMTP(PaymentDetail payment);

	Object handleSmartPaySourceWebhook(Object req, String tokens);

	Map<String,Object>  checkOmanPayment(MultiValueMap<String, String> req);

//	RedirectView handleLipilaWebhook(Map<String, Object> webhookData);

}
