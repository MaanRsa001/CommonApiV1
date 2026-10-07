package com.maan.eway.payment.service.impl; 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maan.eway.auth.dto.ClaimLoginResponse;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.bean.RSTAPushDetails;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.TiraFrameReqCall;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PaymentDetailGetRes;
import com.maan.eway.common.service.impl.GeneratePolicyEwayServiceImpl;
import com.maan.eway.common.service.impl.TiraIntegerationServiceImpl;
import com.maan.eway.integration.controller.IntegrationController;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.mtpintegration.dto.MtpPolicyReq;
import com.maan.eway.mtpintegration.dto.PolicyResponse;
import com.maan.eway.mtpintegration.service.MtpPartnerService;
import com.maan.eway.payment.service.MpesaPaymentService;
import com.maan.eway.payment.service.SelcomPaymentService;
import com.maan.eway.payment.util.ApigwClient;
import com.maan.eway.payment.util.CyberSouceIntegration;
import com.maan.eway.realpay.dto.MailRequestDTO;
import com.maan.eway.realpay.service.EmailNotificationService;
import com.maan.eway.reinsurance.req.ReInsuranceQuoteReq;
import com.maan.eway.reinsurance.service.ReinsuranceService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PaymentVendorMasterRepository;
import com.maan.eway.repository.RSTAPushDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;


@Service
public class SelcomPaymentImpl implements SelcomPaymentService {

	@Autowired
	private PaymentVendorMasterRepository paymentVendorRepo;	

	@Autowired
	private PaymentDetailRepository paymentDetailRepo;

	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo;
	
	@Autowired
	private RSTAPushDetailsRepository rstaPushDetailsRepo;

	@Autowired
	private GeneratePolicyEwayServiceImpl paymentService;
	
	@Autowired
	private PaymentInfoRepository paymentinforepo;

	@Autowired
	private HomePositionMasterRepository homerepo;
	
	@Autowired
	private ListItemValueRepository itemValueRepo;

	private Logger log = LogManager.getLogger(SelcomPaymentImpl.class);

	private JsonArray payments;
	
	@Autowired
	private AuthendicationService authservice;
	@Autowired
	private  TiraIntegerationServiceImpl tiraService;

	
	@PersistenceContext
	private EntityManager em;
	
	@Value("${whatsapp.post.url}")
	private String whatsappUrl;
	
	@Value("${client_id}")
	private String clientId;
	
	@Value("${client_secret}")
	private String clientSecret;
	
	@Value("${token_url}")
	private String tokenUrl;
	
	@Autowired
	private MpesaPaymentService mpesaPaymentService;

	// Kill switches for the two WhatsApp-specific payment fixes below. Not declared in any
	// .properties file — default is the fixed behavior; override in a properties file or env var
	// only if a rollback is needed.
	@Value("${phoenix.whatsapp.cybersource.webhook.gracefulFallback.enabled:true}")
	private boolean cybersourceWebhookGracefulFallbackEnabled;

	@Value("${phoenix.whatsapp.mozambique.mpesa.liveGateway.enabled:true}")
	private boolean mozambiqueMpesaLiveGatewayEnabled;

	@Autowired
	private ReinsuranceService reinsuranceService;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@Autowired
	@Lazy
	private IntegrationController integrationController;

//	@Autowired
//	@Lazy
//	private SelcomPaymentImpl self;
	
	@Value("${mtp.base.url}")
	private String mtpBaseUrl;

	@Value("${mtp.sticker.download.path}")
	private String mtpStickerDownloadPath;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepo;
	
	@Autowired
	private MtpPartnerService mtpPartnerService;
	
	@Override
	public JsonObject createOrderForPayment(String merchantRefernceNo) {
		try {
			PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(merchantRefernceNo,"PENDING");
			if(payment!=null)
				return createOrderForPayment(payment);
			else {
				log.info(merchantRefernceNo +" No Record Found") ;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public JsonObject createOrderForPayment(PaymentDetail payment) {
		try {

			if(payment!=null ) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());

				String userytype="b2b";
				if(paymentInfo.getSubUserType().equalsIgnoreCase("b2c")) {
					userytype="b2c";
				}

				List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,paymentInfo.getProductId());
				PaymentVendorMaster vendor =null;
				if((paymentInfo.getProductId().equals(5) && payment.getCompanyId().equals("100019") &&  payment.getPaymentType().equals("6")) ||
						(paymentInfo.getProductId().equals(125) && payment.getCompanyId().equals("100019"))) {
					vendor =getMtpVendor(payment.getCompanyId());	
				}
				
				else if(paymentId!=null && paymentId.size()>0) {						 
					vendor = paymentId.get(0);
				}else {
					paymentId=paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,99999);
					vendor = paymentId.get(0);
				}
				
				JsonObject response = null;
 
//				if("lipila".equals(vendor.getVendorName())) {
//					return lipila(vendor,payment);
				if("lipila".equals(vendor.getVendorName())) {
				    String lipilaPaymentType = payment.getLipilaPaymentType();
				    String whatsappYn = payment.getWhatsappYn();
				    
				    if (StringUtils.isBlank(lipilaPaymentType)) {
				        lipilaPaymentType = "card";
				        log.warn("LipilaPaymentType not specified for QuoteNo: {}. Defaulting to 'card'", payment.getQuoteNo());
				    }
				    
				    log.info("Lipila Payment - QuoteNo: {}, Type: {}, WhatsApp: {}", 
				        payment.getQuoteNo(), lipilaPaymentType, whatsappYn);
				    
				    return lipila(vendor, payment, lipilaPaymentType, whatsappYn);
				    
				}else if("ipayafrica".equals(vendor.getVendorName())) {
					return ipayafrica(vendor,payment);
				}else if("pesapal".equals(vendor.getVendorName())){
					return pesapal(vendor,payment);
				}else if("peach".equals(vendor.getVendorName())){
					return peach(vendor,payment);
				}
				else if ("mtp".equals(vendor.getVendorName())) {
				    return mtpMobile(vendor, payment);
				}
				/**else if ("cybersource".equals(vendor.getVendorName())) {
					return cybersource(vendor, payment);
				}**/
				
				else if("cybersource".equals(vendor.getVendorName())){
					//return cybersource(vendor,payment);
					   return cyberSourcePaymentDetails(vendor, payment, paymentInfo);
				}
 
//				else if("mpesa".equals(vendor.getVendorName())){
//					return mpesa(vendor,payment);
//				}
				
				else if ("mpesa".equals(vendor.getVendorName())) {
					// Real Vodacom M-Pesa STK push (MpesaPaymentImpl) — no hosted payment_gateway_url is
					// returned, the customer confirms on-phone. Previously this branch called the mPesaPayment()
					// stub which faked "SUCCESS" with a literal "Dummy" URL and never contacted Vodacom.
					JsonObject responsempesa = new JsonObject();
					if (!mozambiqueMpesaLiveGatewayEnabled) {
						// Kill switch: rolled back to a real M-Pesa integration issue, don't fake success.
						log.warn("Mpesa live gateway disabled via phoenix.whatsapp.mozambique.mpesa.liveGateway.enabled=false; QuoteNo={}",
								payment.getQuoteNo());
						responsempesa.addProperty("result", "FAILED");
						responsempesa.addProperty("Message", "Mpesa payment is temporarily unavailable");
						return responsempesa;
					}
					JsonObject mpesaResult = mpesaPaymentService.payment(payment, vendor);
					String resultValue = mpesaResult != null && mpesaResult.has("result")
							? mpesaResult.get("result").getAsString() : "Fail";
					responsempesa.addProperty("result", "Success".equalsIgnoreCase(resultValue) ? "SUCCESS" : "FAILED");
					responsempesa.addProperty("Message", mpesaResult != null && mpesaResult.has("Message")
							? mpesaResult.get("Message").getAsString() : "Mpesa payment request failed");
					return responsempesa;
				}
				else if("cybersource".equals(vendor.getVendorName())){
					//return cybersource(vendor,payment);
					return cyberSourcePaymentDetails(vendor, payment, paymentInfo);
				}else if("tingg".equals(vendor.getVendorName())){
					return tingg(vendor,payment);
				}else if("smartpay".equals(vendor.getVendorName())){
					JsonObject responsempesa = new JsonObject();
					responsempesa.addProperty("result", "OMAN");
					return responsempesa;
				}else {
					return selcomPayment(vendor,payment);
				}
				
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private JsonObject mtpMobile(PaymentVendorMaster vendor, PaymentDetail payment) {
		try {
			JsonObject resp =new JsonObject();
			resp.addProperty("result", "SUCCESS");
			JsonObject innerResponse=new JsonObject();
			innerResponse.addProperty("payment_gateway_url", "www.dummyurl.com");
			JsonArray asJsonArray =new JsonArray(1);
			asJsonArray.add(innerResponse);
			resp.add("data", asJsonArray);
			return resp;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private JsonObject tingg(PaymentVendorMaster vendor, PaymentDetail payment) {
		
		try {

			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;
			String orderPath =null;
			String vendorCode=null;
			String redirect_url=null;
			String cancel_url=null;
			String webHookUrl=null;
			String signedFields="";
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				orderPath=vendor.getPaymentUrlLink();
				vendorCode=vendor.getVendorCode();
				redirect_url=vendor.getReturnUrlLink();
				cancel_url=vendor.getCancelUrlLink();
				webHookUrl=vendor.getWebhookUrlLink();

				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				cancel_url=cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				signedFields=vendor.getSignedFields();
			}


			// data
			//JsonObject orderDict = new JsonObject();
			Map<String, Object> orderDict = new HashMap<>();
			orderDict.put("service_code",vendorCode);
			orderDict.put("merchant_transaction_id",payment.getMerchantReference());
			orderDict.put("account_number", "123456" );
			orderDict.put("customer_first_name", payment.getCustomerName());
			orderDict.put("customer_last_name", payment.getCustomerName());
			orderDict.put("msisdn", payment.getReqBillToPhone());
			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				orderDict.put("request_amount",  payment.getPremiumLc().toPlainString()); //payment.getPremiumLc().toPlainString()
			else
				orderDict.put("request_amount",  payment.getPremiumLc().toPlainString());//payment.getPremiumFc().toPlainString()

			orderDict.put("currency_code",payment.getCurrencyId());
			orderDict.put("country_code",StringUtils.isNotBlank(insInfo.get(0).getCountryId())?insInfo.get(0).getCountryId():"UGA");

			//orderDict.addProperty("amount",100);					 
			//4orderDict.addProperty("currency","TZS");
			//orderDict.addProperty("payment_methods","ALL");
			orderDict.put("success_redirect_url",StringUtils.isNotBlank(redirect_url)?redirect_url:"");
			orderDict.put("fail_redirect_url",StringUtils.isNotBlank(cancel_url)?cancel_url:"");
			orderDict.put("callback_url",StringUtils.isNotBlank(webHookUrl)?webHookUrl:"");
			 
			
			
			//Token creation
			Map<String,Object> tokenReq = new HashMap<>();
			tokenReq.put("client_id", clientId);
			tokenReq.put("client_secret", clientSecret);
			tokenReq.put("grant_type", "client_credentials");
			
			ResponseEntity<String> response = null;
			ObjectMapper objectMapper = new ObjectMapper();
            String reqString = objectMapper.writeValueAsString(tokenReq);
            RestTemplate restTemp = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			header.add("apiKey", apiKey);
			HttpEntity<?> requestent = new HttpEntity<>(reqString, header);
			response = restTemp.exchange(tokenUrl, HttpMethod.POST, requestent, String.class);
			Map<String,Object> resp = null;
			if(response.getBody() != null && !response.getBody().isEmpty()) {
				resp = objectMapper.readValue(response.getBody(), Map.class);
			}
			String bearerToken = resp.get("access_token") == null? null : resp.get("access_token").toString();
			JsonObject finalResponse = new JsonObject();
			if(StringUtils.isNotBlank(bearerToken)) {
				ResponseEntity<String> response2 = null;
				String checkoutReq = objectMapper.writeValueAsString(orderDict);
				RestTemplate restTemp1 = new RestTemplate();
	            HttpHeaders header1 = new HttpHeaders();
				header1.setContentType(MediaType.APPLICATION_JSON);
				header1.add("apiKey", apiKey);
				header1.add("Authorization","Bearer "+ bearerToken);
				HttpEntity<?> requestent1 = new HttpEntity<>(checkoutReq, header1);
				response2 = restTemp1.exchange(baseUrl+orderPath, HttpMethod.POST, requestent1, String.class);
				JsonObject vendorJson = null;
				
				if(response2.getBody() != null && !response2.getBody().isEmpty()) {
					 vendorJson = JsonParser.parseString(response2.getBody()).getAsJsonObject();
					 JsonElement statusElement = vendorJson.get("status");
					 
					 JsonArray dataArray = new JsonArray();
					 JsonObject dataObj = new JsonObject();
					    
					    if (statusElement != null && statusElement.isJsonObject()) {
					    	JsonObject statusObj = statusElement.getAsJsonObject();

					        String statusCode = statusObj.get("status_code").getAsString();
					        String statusDesc = statusObj.get("status_description").getAsString();


					        finalResponse.addProperty("result",
					                "success".equalsIgnoreCase(statusDesc) ? "SUCCESS" : "FAILED");

					        if ("200".equals(statusCode)) {
					            String shortUrl = vendorJson
					                    .getAsJsonObject("results")
					                    .get("short_url")
					                    .getAsString();

					            dataObj.addProperty("payment_gateway_url", shortUrl);
					        }

					        dataArray.add(dataObj);
					        finalResponse.add("data", dataArray);

					    }
					    // FAILURE SCENARIO
					    else if (statusElement != null && statusElement.isJsonPrimitive()) {

					        String statusCode = statusElement.getAsString();
					        String message = vendorJson.has("message")
					                ? vendorJson.get("message").getAsString()
					                : "Payment gateway error";

					        finalResponse.addProperty("result", "FAILED");
					        finalResponse.addProperty("error_code", statusCode);
					        finalResponse.addProperty("error_message", message);

					        dataArray.add(dataObj);
					        finalResponse.add("data", dataArray);
					    }

					    }

				/*	 if("200".contentEquals(status)) {
						 String statusDesc = vendorJson
							        .getAsJsonObject("status")
							        .get("status_description")
							        .getAsString();

							finalResponse.addProperty("result",
							        "success".equalsIgnoreCase(statusDesc) ? "SUCCESS" : "FAILED");
							
							JsonArray dataArray = new JsonArray();
							JsonObject dataObj = new JsonObject();

							String shortUrl = vendorJson
							        .getAsJsonObject("results")
							        .get("short_url")
							        .getAsString();

							dataObj.addProperty("payment_gateway_url", shortUrl);
							dataArray.add(dataObj);

							finalResponse.add("data", dataArray);
					 }else {
						 String statusDesc = vendorJson
							       // .getAsJsonObject("status")
							        .get("message")
							        .getAsString();
						 
						 finalResponse.addProperty("result",
							        "success".equalsIgnoreCase(statusDesc) ? "SUCCESS" : "FAILED");
							
							JsonArray dataArray = new JsonArray();
							JsonObject dataObj = new JsonObject();

							dataArray.add(dataObj);

							finalResponse.add("data", dataArray);
					 } */
					
				log.info("TINGG PAYMENTGATEWAY RESPONSE" + finalResponse) ;
				System.out.println(finalResponse);
			}
			
			/*List<String> signRemove=new LinkedList<String>();
			for (Entry<String, JsonElement> entry : orderDict.entrySet()) {
				if(!signedFields.contains(entry.getKey())) {
					signRemove.add(entry.getKey());
				} 
			}					
			for(String key:signRemove) {
				orderDict.remove(key);
			} */
			// initalize a new Client instace with values of the base url, api key and api secret
			//ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
			//post data
			//JsonObject response = client.postFunc(orderPath ,orderDict);
			
			return finalResponse;
		}catch(Exception e) {
			e.printStackTrace();
			log.info("TINGG PAYMENTGATEWAY ERROR RESPONSE FOR QUOTENO "+payment.getQuoteNo()+"is " + e.getMessage()) ;
		}
		return null;

	}

	
	private JsonObject cybersource(PaymentVendorMaster vendor, PaymentDetail payment) {
		List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
		String castAmountValue;
		if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
			castAmountValue =  payment.getPremiumLc().toPlainString();
		else
			castAmountValue=  payment.getPremiumFc().toPlainString();
		return new CyberSouceIntegration().createPay(vendor,payment,castAmountValue) ;
	}
//	private JsonObject mpesa(PaymentVendorMaster vendor, PaymentDetail payment) {
//		try {
//			JsonObject resp =new JsonObject();
//			resp.addProperty("result", "SUCCESS");
//			JsonObject innerResponse=new JsonObject();
//			innerResponse.addProperty("payment_gateway_url", "www.dummyurl.com");
//			JsonArray asJsonArray =new JsonArray(1);
//			asJsonArray.add(innerResponse);
//			resp.add("data", asJsonArray);
//			return resp;
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	private JsonObject peach(PaymentVendorMaster vendor, PaymentDetail payment) {
		DecimalFormat df = new DecimalFormat("#####");
		String signature ="";
		String castAmountValue="0";
		List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
		if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
			castAmountValue = df.format( payment.getPremiumLc().doubleValue());
		else
			castAmountValue=  df.format( payment.getPremiumLc().doubleValue());
		 
		/*try {
			Map<String, String> params = new HashMap<>();
			params.put("authentication.entityId", vendor.getApiKey());
			params.put("amount", castAmountValue);
			params.put("currency", "ZAR");
			params.put("merchantTransactionId",payment.getMerchantReference());
			params.put("nonce", payment.getMerchantReference());
			params.put("paymentType", "DB");
			params.put("shopperResultUrl",vendor.getReturnUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));
					signature = peachGenerateSignature(params, vendor.getApiSecretKey());	
		}catch (Exception e) {
			e.printStackTrace();
		}*/
		JsonObject jsonResponse = new JsonObject();
		try {
			Map<String, String> params = new LinkedHashMap<>();			 
			params.put("amount", castAmountValue);
			params.put("authentication.entityId", vendor.getApiKey());
			params.put("currency", payment.getCurrencyId());//payment.getCurrencyId());
			params.put("merchantTransactionId", payment.getMerchantReference());
			params.put("nonce", payment.getMerchantReference());
			params.put("paymentType", "DB");
			params.put("shopperResultUrl", vendor.getReturnUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));//URLEncoder.encode(, StandardCharsets.UTF_8.toString()) );			 
			
			params.put("customer.merchantCustomerId", payment.getCustomerId());
			params.put("customer.givenName", payment.getCustomerName());			
			params.put("customer.mobile", payment.getReqBillToPhone());
			params.put("customer.email",StringUtils.isBlank(payment.getCustomerEmail())?"":payment.getCustomerEmail());			
			params.put("customer.phone", payment.getReqBillToPhone());
		 
			params.put("billing.city", payment.getReqBillToAddressCity());
			params.put("billing.company", payment.getReqBillToCompanyName());
			params.put("billing.country","SZL".equals(payment.getReqBillToCountry())?"SZ":payment.getReqBillToCountry());			
			params.put("billing.state", payment.getReqBillToAddressState());
			params.put("billing.postcode", payment.getReqBillToAddrPostalCode());
			params.put("cancelUrl", vendor.getCancelUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));
			params.put("notificationUrl", vendor.getWebhookUrlLink());
			//params.put("forceDefaultMethod","true");
			//params.put("defaultPaymentMethod","CARD");
			signature = peachGenerateSignature(params, vendor.getApiSecretKey());	
			params.put("signature", signature);
			params.put("shopperResultUrl",vendor.getReturnUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));//URLEncoder.encode(, StandardCharsets.UTF_8.toString()) );	
			params.put("cancelUrl", vendor.getCancelUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));
			params.put("notificationUrl", vendor.getWebhookUrlLink());
			/*String requestBody = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
					.collect(Collectors.joining("&"));
			System.out.println(" checkOut Request Body: " + requestBody);
			 */
			try (CloseableHttpClient client = HttpClients.createDefault()) {
				HttpPost httpPost = new HttpPost(vendor.getPaymentUrlLink());
				httpPost.setHeader("Content-Type", "application/json");
				ObjectMapper objectMapper = new ObjectMapper();
	            String json = objectMapper.writeValueAsString(params);
				httpPost.setEntity(new StringEntity(json));

				try (CloseableHttpResponse response = client.execute(httpPost)) {
					org.apache.http.HttpEntity entity = response.getEntity();
					String responseString = EntityUtils.toString(entity);
					System.out.println("Response: " + responseString);
					JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
					if (responseJson.has("redirectUrl")) {
	                    jsonResponse.addProperty("redirectUrl", responseJson.get("redirectUrl").getAsString());
	                    
	                    jsonResponse.addProperty("result", "SUCCESS");				
	    				JsonObject innerResponse=new JsonObject();
	    				innerResponse.addProperty("payment_gateway_url",responseJson.get("redirectUrl").getAsString());
	    				JsonArray asJsonArray =new JsonArray(1);
	    				asJsonArray.add(innerResponse);
	    				jsonResponse.add("data", asJsonArray);
	                    
	                } else {
	                    jsonResponse.addProperty("status", "error");
	                    jsonResponse.addProperty("message", "redirectUrl not found in response");
	                    jsonResponse.addProperty("result", "ERROR");
	                }					
						
				}
			}catch (Exception e) {
				 e.printStackTrace();
				 
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonResponse.addProperty("status", "error");
			jsonResponse.addProperty("message", "Failed to initiate checkout");
		}
		return jsonResponse;
	}
	
	public static String peachGenerateSignature(Map<String, String> body, String secret) {
		Map<String, String> sortedParams = new TreeMap<>(body);
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
			String key = entry.getKey().trim();
			String value = entry.getValue() != null ? entry.getValue().trim() : "";

			if ("signature".equals(key)) {
				continue;
			}

			result.append(key).append(value);
		}
		 
		System.out.println("Signature String (before hashing): [" + result.toString() + "]");
		return pesapalHmacSha256(result.toString(), secret);
	}
	
	private static String pesapalHmacSha256(String data, String secret) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			mac.init(secretKey);
			byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				hexString.append(String.format("%02x", b));

			}

			return hexString.toString();
		} catch (Exception e) {
			return "Error generating HMAC-SHA256 signature";
		}
	}
	
//////	private JsonObject lipila(PaymentVendorMaster vendor, PaymentDetail payment) {
//////		try {
//////			String apisecrectkey=null;
//////			String apibaseURL = null;
//////			String redirect_url=null;
//////			String apiKey=null;
//////			CloseableHttpClient httpClient = null;
//////			if(vendor!=null) {
//////				apiKey = vendor.getApiKey();
//////				 if("mobile-money".equals(apiKey)) {
////////		                apibaseURL = vendor.getApiBaseUrl();
//////		                apibaseURL = "https://api.lipila.dev/api/v1/collections/mobile-money";
//////		                System.out.println("URL: "+vendor.getApiBaseUrl());
//////		            } else {
//////		                apibaseURL = vendor.getRemarks();
//////		            }
////////				apisecrectkey=vendor.getApiSecretKey();
//////					apisecrectkey="lsk_019d9a8f-4bb6-7dc6-90c4-edac2baeae4f";
//////
//////				redirect_url=vendor.getReturnUrlLink();
//////				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
//////			}
//////			try {
//////				JsonObject request=new JsonObject();
//////				request.addProperty("currency",payment.getCurrencyId());
//////				request.addProperty("amount",payment.getPremium()); 
//////				
////////			    request.addProperty("accountNumber", payment.getReqBillToPhone());
////////			    request.addProperty("fullName", payment.getCustomerName());
////////				request.addProperty("paymentType", "MtnMoney");
////////				request.addProperty("email", payment.getCustomerEmail());
////////				request.addProperty("phoneNumber", payment.getReqBillToPhone());
////////				request.addProperty("customerFirstName", payment.getCustomerName());
////////				request.addProperty("customerLastName", "NA");
////////				request.addProperty("customerCity", payment.getReqBillToAddressCity());
////////				request.addProperty("customerCountry", payment.getReqBillToCountry());
////////				request.addProperty("customerAddress", payment.getReqBillToAddressLine1()+payment.getReqBillToAddressLine2());
////////				request.addProperty("customerZip", 0);
////////				request.addProperty("externalId", payment.getMerchantReference());
////////				request.addProperty("narration", payment.getMerchantReference());
////////				request.addProperty("redirectUrl", redirect_url);
////////				request.addProperty("clientRedirectUrl", redirect_url);
//////				
//////				 if("mobile-money".equals(apiKey)) {
//////					 
//////		                String formattedPhone = formatPhoneNumber(payment.getReqBillToPhone());
//////		                System.out.println("Formatted Phone: "+formattedPhone);
//////
//////
////////		                request.addProperty("accountNumber", payment.getReqBillToPhone());
////////		                request.addProperty("accountNumber", "260-948-598-498");
//////		                request.addProperty("accountNumber", formattedPhone);
//////
//////
//////
//////		                request.addProperty("fullName", payment.getCustomerName());
//////		                request.addProperty("phoneNumber", formattedPhone);
//////		                request.addProperty("email", payment.getCustomerEmail());
//////		                request.addProperty("externalId", payment.getMerchantReference());
//////		                request.addProperty("narration", payment.getMerchantReference());
//////		                request.addProperty("redirectUrl", redirect_url);
//////		                request.addProperty("clientRedirectUrl", redirect_url);
//////		                
//////		            } else {
//////		                request.addProperty("email", payment.getCustomerEmail());
//////		                request.addProperty("phoneNumber", payment.getReqBillToPhone());
//////		                request.addProperty("customerFirstName", payment.getCustomerName());
//////		                request.addProperty("customerLastName", "NA");
//////		                request.addProperty("customerCity", payment.getReqBillToAddressCity());
//////		                request.addProperty("customerCountry", payment.getReqBillToCountry());
//////		                request.addProperty("customerAddress", payment.getReqBillToAddressLine1() + payment.getReqBillToAddressLine2());
//////		                request.addProperty("customerZip", 0);
//////		                request.addProperty("externalId", payment.getMerchantReference());
//////		                request.addProperty("narration", payment.getMerchantReference());
//////		                request.addProperty("redirectUrl", redirect_url);
//////		                request.addProperty("clientRedirectUrl", redirect_url);
//////		            }
//////
//////				httpClient= HttpClientBuilder.create().build();
//////				HttpPost postRequest = new HttpPost(apibaseURL);
//////				postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
//////				postRequest.setHeader("Accept",MediaType.APPLICATION_JSON_VALUE);														
//////				postRequest.setHeader("Authorization","Bearer "+ apisecrectkey);
//////
//////				StringEntity params = new StringEntity(request.toString());
//////	            System.out.println("Lipila Request Body: " + request.toString());				
//////
//////				postRequest.setEntity(params);
//////				HttpResponse hresp  = httpClient.execute(postRequest);
//////				
//////	            int statusCode = hresp.getStatusLine().getStatusCode();
//////	            
//////				org.apache.http.HttpEntity httpEntity = hresp.getEntity();
//////				String apiOutput = EntityUtils.toString(httpEntity);
//////				System.out.println("Lipila Response Status: " + statusCode);
//////	            System.out.println("Lipila Response Body: " + apiOutput);				
////////	            JsonObject resp = new Gson().fromJson(apiOutput, JsonObject.class);
////////				
////////	            resp.addProperty("result", "SUCCESS");
////////				JsonObject innerResponse=new JsonObject();
////////				innerResponse.addProperty("payment_gateway_url", resp.get("redirectUrl").getAsString());
////////				JsonArray asJsonArray =new JsonArray(1);
////////				asJsonArray.add(innerResponse);
////////				resp.add("data", asJsonArray);
////////				return resp;
////////			}catch(Exception e) {
////////				e.printStackTrace();
////////			}
////////		}catch(Exception e) {
////////			e.printStackTrace();
////////		}
////////		return null;
////////	}
//////	            
//////	            // ✓ CHECK IF RESPONSE IS NULL OR EMPTY
//////	            if(apiOutput == null || apiOutput.trim().isEmpty()) {
//////	                JsonObject errorResp = new JsonObject();
//////	                errorResp.addProperty("result", "ERROR");
//////	                errorResp.addProperty("message", "Empty response from Lipila API");
//////	                errorResp.addProperty("statusCode", statusCode);
//////	                return errorResp;
//////	            }
//////	            
//////	            // ✓ TRY TO PARSE JSON
//////	            JsonObject resp = null;
//////	            try {
//////	                resp = new Gson().fromJson(apiOutput, JsonObject.class);
//////	            } catch(Exception jsonEx) {
//////	                System.err.println("Failed to parse JSON response: " + jsonEx.getMessage());
//////	                JsonObject errorResp = new JsonObject();
//////	                errorResp.addProperty("result", "ERROR");
//////	                errorResp.addProperty("message", "Invalid JSON response from Lipila: " + apiOutput);
//////	                errorResp.addProperty("statusCode", statusCode);
//////	                return errorResp;
//////	            }
//////	            
//////	            // ✓ CHECK IF RESP IS NULL
//////	            if(resp == null) {
//////	                JsonObject errorResp = new JsonObject();
//////	                errorResp.addProperty("result", "ERROR");
//////	                errorResp.addProperty("message", "Null response object from Lipila");
//////	                errorResp.addProperty("rawResponse", apiOutput);
//////	                errorResp.addProperty("statusCode", statusCode);
//////	                return errorResp;
//////	            }
//////	            
//////	            // ✓ CHECK FOR ERROR RESPONSE
//////	            if(resp.has("error") || resp.has("message") && statusCode >= 400) {
//////	                resp.addProperty("result", "ERROR");
//////	                resp.addProperty("statusCode", statusCode);
//////	                return resp;
//////	            }
//////	            
//////	            // ✓ SUCCESS RESPONSE
//////	            resp.addProperty("result", "SUCCESS");
//////	            resp.addProperty("statusCode", statusCode);
//////	            
//////	            // Check if redirectUrl exists before accessing
//////	            if(resp.has("redirectUrl") && !resp.get("redirectUrl").isJsonNull()) {
//////	                JsonObject innerResponse = new JsonObject();
//////	                innerResponse.addProperty("payment_gateway_url", resp.get("redirectUrl").getAsString());
//////	                JsonArray asJsonArray = new JsonArray(1);
//////	                asJsonArray.add(innerResponse);
//////	                resp.add("data", asJsonArray);
//////	            }
//////	            
//////	            return resp;
//////	            
//////	        } catch(Exception e) {
//////	            e.printStackTrace();
//////	            JsonObject errorResp = new JsonObject();
//////	            errorResp.addProperty("result", "ERROR");
//////	            errorResp.addProperty("message", "Exception: " + e.getMessage());
//////	            return errorResp;
//////	        } finally {
//////	            if(httpClient != null) {
//////	                try {
//////	                    httpClient.close();
//////	                } catch(Exception e) {
//////	                    e.printStackTrace();
//////	                }
//////	            }
//////	        }
//////	    } catch(Exception e) {
//////	        e.printStackTrace();
//////	        JsonObject errorResp = new JsonObject();
//////	        errorResp.addProperty("result", "ERROR");
//////	        errorResp.addProperty("message", "Outer exception: " + e.getMessage());
//////	        return errorResp;
//////	    }
//////	}
//////
//////	private String formatPhoneNumber(String phoneNumber) {
//////	    if(phoneNumber == null || phoneNumber.isEmpty()) {
//////	        return phoneNumber;
//////	    }
//////	    
//////	    // Remove any existing hyphens, spaces, or special characters
//////	    String cleaned = phoneNumber.replaceAll("[^0-9]", "");
//////	    
//////	    System.out.println("Cleaned phone: " + cleaned + " (length: " + cleaned.length() + ")");
//////	    
//////	    // ✓ If 13 digits starting with 260, trim to 12 digits (remove last digit)
//////	    if(cleaned.startsWith("260") && cleaned.length() == 13) {
//////	        System.out.println("WARNING: Phone number has 13 digits, trimming to 12 (Lipila requires 260-XXX-XXX-XXX format)");
//////	        cleaned = cleaned.substring(0, 12); // Trim to 12 digits
//////	    }
//////	    
//////	    // Handle 12 digits: 260 + 9 digits (260-XXX-XXX-XXX)
//////	    if(cleaned.startsWith("260") && cleaned.length() == 12) {
//////	        String formatted = cleaned.substring(0, 3) + "-" + 
//////	                          cleaned.substring(3, 6) + "-" + 
//////	                          cleaned.substring(6, 9) + "-" + 
//////	                          cleaned.substring(9, 12);
//////	        System.out.println("Formatted phone: " + formatted);
//////	        return formatted;
//////	    }
//////	    
//////	    // If it's 10 digits without country code, trim to 9 and add 260
//////	    if(!cleaned.startsWith("260") && cleaned.length() == 10) {
//////	        System.out.println("WARNING: 10 digits without country code, trimming to 9 and adding 260");
//////	        cleaned = "260" + cleaned.substring(0, 9);
//////	        return cleaned.substring(0, 3) + "-" + 
//////	               cleaned.substring(3, 6) + "-" + 
//////	               cleaned.substring(6, 9) + "-" + 
//////	               cleaned.substring(9, 12);
//////	    }
//////	    
//////	    // If it's 9 digits without country code, add 260
//////	    if(!cleaned.startsWith("260") && cleaned.length() == 9) {
//////	        cleaned = "260" + cleaned;
//////	        return cleaned.substring(0, 3) + "-" + 
//////	               cleaned.substring(3, 6) + "-" + 
//////	               cleaned.substring(6, 9) + "-" + 
//////	               cleaned.substring(9, 12);
//////	    }
//////	    
//////	    // Return as-is if format is unexpected
//////	    System.out.println("WARNING: Unexpected phone format (length: " + cleaned.length() + "), returning as-is");
//////	    return phoneNumber;
//////	}
////	
////	private JsonObject lipila(PaymentVendorMaster vendor, PaymentDetail payment) {
////	    try {
////	        String apiSecretKey = null;
////	        String apibaseURL = null;
////	        String apiKey = null;
////	        String callbackUrl = null;
////	        CloseableHttpClient httpClient = null;
////	        
////	        if(vendor != null) {
////	            apiKey = vendor.getApiKey();
//////	            apiSecretKey = vendor.getApiSecretKey();
////	            apiSecretKey = "lsk_019d9a8f-4bb6-7dc6-90c4-edac2baeae4f";
////
////	            callbackUrl = vendor.getWebhookUrlLink();
////	            
////	            if("mobile-money".equals(apiKey)) {
////	                apibaseURL = "https://api.lipila.dev/api/v1/collections/mobile-money";
////	                System.out.println("Lipila API: " + apibaseURL);
////	            } else {
////	                apibaseURL = vendor.getRemarks();
////	                System.out.println("Using Card API: " + apibaseURL);
////	            }
////	        }
////	        
////	        try {
////	            JsonObject request = new JsonObject();
////	            
////	            if("mobile-money".equals(apiKey)) {
////	 
////	                String cleanPhone = cleanPhoneNumber("260976008698");
//////	                String cleanPhone = cleanPhoneNumber(payment.getReqBillToPhone());
////
////	                System.out.println("Clean Phone (no hyphens): " + cleanPhone);
////	                
////	                request.addProperty("referenceId", payment.getMerchantReference());
////	                request.addProperty("amount", payment.getPremium());
////	                request.addProperty("narration", payment.getMerchantReference());
////	                request.addProperty("accountNumber", cleanPhone);
////	                request.addProperty("phoneNumber", cleanPhone);
////	                request.addProperty("currency", payment.getCurrencyId());
////	                request.addProperty("email", payment.getCustomerEmail());
////	                request.addProperty("customerFirstName", payment.getCustomerName());
////	                request.addProperty("customerLastName", "NA");
////	                request.addProperty("customerCity", payment.getReqBillToAddressCity());
////	                request.addProperty("customerCountry", payment.getReqBillToCountry());
////	                request.addProperty("customerAddress", payment.getReqBillToAddressLine1() + payment.getReqBillToAddressLine2());
////	                request.addProperty("customerZip", 0);
////	                request.addProperty("externalId", payment.getMerchantReference());
////	                
////	                String redirect_url = vendor.getReturnUrlLink();
////	                redirect_url = redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
////	                request.addProperty("redirectUrl", redirect_url);
////	                request.addProperty("clientRedirectUrl", redirect_url);
////	                
////	            } else {
////
////	                request.addProperty("currency", payment.getCurrencyId());
////	                request.addProperty("amount", payment.getPremium());
////	                request.addProperty("email", payment.getCustomerEmail());
////	                request.addProperty("phoneNumber", payment.getReqBillToPhone());
////	                request.addProperty("customerFirstName", payment.getCustomerName());
////	                request.addProperty("customerLastName", "NA");
////	                request.addProperty("customerCity", payment.getReqBillToAddressCity());
////	                request.addProperty("customerCountry", payment.getReqBillToCountry());
////	                request.addProperty("customerAddress", payment.getReqBillToAddressLine1() + payment.getReqBillToAddressLine2());
////	                request.addProperty("customerZip", 0);
////	                request.addProperty("externalId", payment.getMerchantReference());
////	                request.addProperty("narration", payment.getMerchantReference());
////	                
////	                String redirect_url = vendor.getReturnUrlLink();
////	                redirect_url = redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
////	                request.addProperty("redirectUrl", redirect_url);
////	                request.addProperty("clientRedirectUrl", redirect_url);
////	            }
////
////	            httpClient = HttpClientBuilder.create().build();
////	            HttpPost postRequest = new HttpPost(apibaseURL);
////	            
////	            if("mobile-money".equals(apiKey)) {
////
////	                postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
////	                postRequest.setHeader("accept", MediaType.APPLICATION_JSON_VALUE);
////	                postRequest.setHeader("x-api-key", apiSecretKey); 
////	                if(callbackUrl != null && !callbackUrl.isEmpty()) {
////	                    postRequest.setHeader("callbackUrl", callbackUrl); 
////	                }
////	            } else {
////
////	                postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
////	                postRequest.setHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
////	                postRequest.setHeader("Authorization", "Bearer " + apiSecretKey);
////	            }
////
////	            StringEntity params = new StringEntity(request.toString());
////	            System.out.println("==============================================");
////	            System.out.println("Lipila Request URL: " + apibaseURL);
////	            System.out.println("Lipila Request Body: " + request.toString());
////	            System.out.println("==============================================");
////
////	            postRequest.setEntity(params);
////	            HttpResponse hresp = httpClient.execute(postRequest);
////	            
////	            int statusCode = hresp.getStatusLine().getStatusCode();
////	            
////	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
////	            String apiOutput = EntityUtils.toString(httpEntity);
////	            
////	            System.out.println("==============================================");
////	            System.out.println("Lipila Response Status: " + statusCode);
////	            System.out.println("Lipila Response Body: " + apiOutput);
////	            System.out.println("==============================================");
////	            
////	            // CHECK IF RESPONSE IS NULL OR EMPTY
////	            if(apiOutput == null || apiOutput.trim().isEmpty()) {
////	                JsonObject errorResp = new JsonObject();
////	                errorResp.addProperty("result", "ERROR");
////	                errorResp.addProperty("message", "Empty response from Lipila API");
////	                errorResp.addProperty("statusCode", statusCode);
////	                return errorResp;
////	            }
////	            
////	            // TRY TO PARSE JSON
////	            JsonObject resp = null;
////	            try {
////	                resp = new Gson().fromJson(apiOutput, JsonObject.class);
////	            } catch(Exception jsonEx) {
////	                System.err.println("Failed to parse JSON response: " + jsonEx.getMessage());
////	                JsonObject errorResp = new JsonObject();
////	                errorResp.addProperty("result", "ERROR");
////	                errorResp.addProperty("message", "Invalid JSON response from Lipila: " + apiOutput);
////	                errorResp.addProperty("statusCode", statusCode);
////	                return errorResp;
////	            }
////	            
////	            // CHECK IF RESP IS NULL
////	            if(resp == null) {
////	                JsonObject errorResp = new JsonObject();
////	                errorResp.addProperty("result", "ERROR");
////	                errorResp.addProperty("message", "Null response object from Lipila");
////	                errorResp.addProperty("rawResponse", apiOutput);
////	                errorResp.addProperty("statusCode", statusCode);
////	                return errorResp;
////	            }
////	            
////	            // CHECK FOR ERROR RESPONSE
////	            if(resp.has("error") || (resp.has("message") && statusCode >= 400)) {
////	                resp.addProperty("result", "ERROR");
////	                resp.addProperty("statusCode", statusCode);
////	                return resp;
////	            }
////	            
////	            // ✅ SUCCESS RESPONSE
////	            resp.addProperty("result", "SUCCESS");
////	            resp.addProperty("statusCode", statusCode);
////	            
////	            // ✅ ALWAYS CREATE DATA ARRAY (FIX FOR MOBILE MONEY)
////	            JsonObject innerResponse = new JsonObject();
////	            
////	            if(resp.has("redirectUrl") && !resp.get("redirectUrl").isJsonNull()) {
////	                // Card payment - has redirect URL
////	                innerResponse.addProperty("payment_gateway_url", resp.get("redirectUrl").getAsString());
////	                
////	            } else if(resp.has("cardRedirectionUrl") && !resp.get("cardRedirectionUrl").isJsonNull()) {
////	                // Card payment - alternative field name
////	                innerResponse.addProperty("payment_gateway_url", resp.get("cardRedirectionUrl").getAsString());
////	                
////	            } else {
////	                // ✅ MOBILE MONEY - No redirect URL, payment happens via phone prompt
////	                innerResponse.addProperty("payment_gateway_url", "MOBILE_MONEY_INITIATED");
////	                
////	                // Add transaction details
////	                if(resp.has("identifier")) {
////	                    innerResponse.addProperty("transaction_id", resp.get("identifier").getAsString());
////	                }
////	                if(resp.has("paymentType")) {
////	                    innerResponse.addProperty("payment_type", resp.get("paymentType").getAsString());
////	                }
////	                if(resp.has("status")) {
////	                    innerResponse.addProperty("status", resp.get("status").getAsString());
////	                }
////	            }
////	            
////	            // ✅ ALWAYS ADD DATA ARRAY
////	            JsonArray asJsonArray = new JsonArray(1);
////	            asJsonArray.add(innerResponse);
////	            resp.add("data", asJsonArray);
////	            
////	            return resp;
////	            
////	        } catch(Exception e) {
////	            e.printStackTrace();
////	            JsonObject errorResp = new JsonObject();
////	            errorResp.addProperty("result", "ERROR");
////	            errorResp.addProperty("message", "Exception: " + e.getMessage());
////	            return errorResp;
////	        } finally {
////	            if(httpClient != null) {
////	                try {
////	                    httpClient.close();
////	                } catch(Exception e) {
////	                    e.printStackTrace();
////	                }
////	            }
////	        }
////	    } catch(Exception e) {
////	        e.printStackTrace();
////	        JsonObject errorResp = new JsonObject();
////	        errorResp.addProperty("result", "ERROR");
////	        errorResp.addProperty("message", "Outer exception: " + e.getMessage());
////	        return errorResp;
////	    }
////	}
////
////	private String cleanPhoneNumber(String phoneNumber) {
////	    if(phoneNumber == null || phoneNumber.isEmpty()) {
////	        return phoneNumber;
////	    }
////	    
////	    // Remove all non-numeric characters
////	    String cleaned = phoneNumber.replaceAll("[^0-9]", "");
////	    
////	    System.out.println("Original phone: " + phoneNumber);
////	    System.out.println("Cleaned phone: " + cleaned + " (length: " + cleaned.length() + ")");
////	    
////	    // Add country code if missing
////	    if(!cleaned.startsWith("260")) {
////	        if(cleaned.length() == 9 || cleaned.length() == 10) {
////	            cleaned = "260" + cleaned;
////	            System.out.println("Added country code: " + cleaned);
////	        }
////	    }
////	    
////	    // Trim to 12 digits if too long
////	    if(cleaned.length() > 12) {
////	        System.out.println("WARNING: Phone too long (" + cleaned.length() + " digits), trimming to 12");
////	        cleaned = cleaned.substring(0, 12);
////	    }
////	    
////	    // Validation
////	    if(cleaned.length() != 12) {
////	        System.err.println("ERROR: Invalid phone length after cleaning: " + cleaned.length() + " (expected 12)");
////	    }
////	    
////	    System.out.println("Final clean phone: " + cleaned);
////	    return cleaned; // Return WITHOUT hyphens
////	}
//
//	private JsonObject lipila(PaymentVendorMaster vendor, PaymentDetail payment, String lipilaPaymentType, String momoProvider) {
//	    CloseableHttpClient httpClient = null;
//	    try {
//	        if (vendor == null) {
//	            return buildErrorResponse("Vendor details are missing", 500);
//	        }
//
//	        // 1. Get Configurations from Vendor Master
//	        // String apiSecretKey = vendor.getApiSecretKey(); 
//	        // String baseUrl = vendor.getApiBaseUrl();
//	    	String apiSecretKey = "lsk_019d9a8f-4bb6-7dc6-90c4-edac2baeae4f";
//	    	String baseUrl = "https://api.lipila.dev/api/v1";
//	        String callbackUrl = vendor.getWebhookUrlLink();
//	        
//	        // Validate payment type
//	        boolean isMobileMoney = "mobile-money".equalsIgnoreCase(lipilaPaymentType);
//	        
//	        if (!isMobileMoney && !"card".equalsIgnoreCase(lipilaPaymentType)) {
//	            return buildErrorResponse("Invalid lipilaPaymentType. Must be 'card' or 'mobile-money'", 400);
//	        }
//	        
//	        // 2. Dynamic API URL Resolution
//	        String endpoint = isMobileMoney ? "/collections/mobile-money" : "/collections/card";
//	        String apibaseURL = baseUrl + endpoint;
//
//	        // 3. Phone Number Formatting
////	        String cleanPhone = cleanPhoneNumber(payment.getReqBillToPhone());
//	        String cleanPhone = "260976008698";
//
//	        
//	        // 4. Get redirect URLs
//	        String redirect_url = vendor.getReturnUrlLink();
//	        if (redirect_url != null) {
//	            redirect_url = redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
//	        }
//	        
//	        String cancel_url = vendor.getCancelUrlLink();
//	        if (cancel_url != null) {
//	            cancel_url = cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
//	        }
//
//	        // 5. Get Insurance Company Info for Currency
//	        List<InsuranceCompanyMaster> insInfo = insuranceRepo
//	            .findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(
//	                payment.getCompanyId(), "Y", new Date(), new Date());
//	        
//	        // Determine correct amount based on currency
//	        BigDecimal amount;
//	        if (!insInfo.isEmpty() && insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId())) {
//	            amount = payment.getPremiumLc();
//	        } else {
//	            amount = payment.getPremiumFc();
//	        }
//
//	        // 6. Construct Payload based on Payment Type
//	        JsonObject requestPayload = new JsonObject();
//	        
//	        if (isMobileMoney) {
//	            // ✅ FIXED: Strictly adhere to Lipila Docs for Mobile Money
//	            // Only referenceId, amount, narration, accountNumber, currency, and email are allowed
//	            requestPayload.addProperty("referenceId", payment.getMerchantReference());
//	            requestPayload.addProperty("amount", amount.toPlainString());
//	            requestPayload.addProperty("narration", payment.getQuoteNo() + " - Insurance Payment");
//	            requestPayload.addProperty("accountNumber", cleanPhone);
//	            requestPayload.addProperty("currency", payment.getCurrencyId());
//	            
//	            if (StringUtils.isNotBlank(payment.getCustomerEmail())) {
//	                requestPayload.addProperty("email", payment.getCustomerEmail());
//	            }
//	            
//	        } else {
//	            // ✅ CARD PAYMENT: Retained Nested JSON Structure
//	            JsonObject customerInfo = new JsonObject();
//	            customerInfo.addProperty("firstName", payment.getCustomerName());
//	            customerInfo.addProperty("lastName", StringUtils.isNotBlank(payment.getReqBillToSurname()) 
//	                ? payment.getReqBillToSurname() : "NA");
//	            customerInfo.addProperty("phoneNumber", cleanPhone);
//	            customerInfo.addProperty("email", StringUtils.isNotBlank(payment.getCustomerEmail()) 
//	                ? payment.getCustomerEmail() : "noreply@example.com");
//	            customerInfo.addProperty("city", StringUtils.isNotBlank(payment.getReqBillToAddressCity()) 
//	                ? payment.getReqBillToAddressCity() : "Lusaka");
//	            customerInfo.addProperty("country", StringUtils.isNotBlank(payment.getReqBillToCountry()) 
//	                ? payment.getReqBillToCountry() : "ZM");
//	            customerInfo.addProperty("address", 
//	                StringUtils.isNotBlank(payment.getReqBillToAddressLine1()) 
//	                ? (payment.getReqBillToAddressLine1() + 
//	                   (StringUtils.isNotBlank(payment.getReqBillToAddressLine2()) ? " " + payment.getReqBillToAddressLine2() : ""))
//	                : "Zambia");
//	            customerInfo.addProperty("zip", 
//	                StringUtils.isNotBlank(payment.getReqBillToAddrPostalCode()) 
//	                ? payment.getReqBillToAddrPostalCode() : "10101");
//
//	            JsonObject collectionRequest = new JsonObject();
//	            collectionRequest.addProperty("referenceId", payment.getMerchantReference());
//	            collectionRequest.addProperty("amount", amount.toPlainString());
//	            collectionRequest.addProperty("currency", payment.getCurrencyId());
//	            collectionRequest.addProperty("narration", payment.getQuoteNo() + " - Insurance Payment");
//	            collectionRequest.addProperty("accountNumber", cleanPhone);
//	            collectionRequest.addProperty("externalId", payment.getMerchantReference());	            
//	            collectionRequest.addProperty("redirectUrl", redirect_url);
//	            collectionRequest.addProperty("backUrl", cancel_url != null ? cancel_url : redirect_url);
//
//	            requestPayload.add("customerInfo", customerInfo);
//	            requestPayload.add("collectionRequest", collectionRequest);
//	        }
//
//	        // 7. Execute HTTP Request
//	        httpClient = HttpClientBuilder.create().build();
//	        HttpPost postRequest = new HttpPost(apibaseURL);
//	        
//	        // Both Card and MoMo use x-api-key header
//	        postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
//	        postRequest.setHeader("accept", MediaType.APPLICATION_JSON_VALUE);
//	        postRequest.setHeader("x-api-key", apiSecretKey); 
//	        
//	        if (callbackUrl != null && !callbackUrl.isEmpty()) {
//	            postRequest.setHeader("callbackUrl", callbackUrl);
//	        }
//
//	        StringEntity params = new StringEntity(requestPayload.toString(), StandardCharsets.UTF_8);
//	        postRequest.setEntity(params);
//	        
//	        log.info("==============================================");
//	        log.info("Lipila Target URL: {}", apibaseURL);
//	        log.info("Lipila Payment Type: {}", isMobileMoney ? "Mobile Money" : "Card");
//	        log.info("Lipila Request Payload: {}", requestPayload.toString());
//	        log.info("==============================================");
//
//	        HttpResponse hresp = httpClient.execute(postRequest);
//	        int statusCode = hresp.getStatusLine().getStatusCode();
//	        String apiOutput = EntityUtils.toString(hresp.getEntity(), StandardCharsets.UTF_8);
//	        
//	        log.info("==============================================");
//	        log.info("Lipila Response Status: {}", statusCode);
//	        log.info("Lipila Response Body: {}", apiOutput);
//	        log.info("==============================================");
//	        
//	        // 8. Handle Response and Errors
//	        if (apiOutput == null || apiOutput.trim().isEmpty()) {
//	            return buildErrorResponse("Empty response from Lipila API", statusCode);
//	        }
//	        
//	        JsonObject resp;
//	        try {
//	            resp = new Gson().fromJson(apiOutput, JsonObject.class);
//	        } catch (Exception e) {
//	            log.error("Failed to parse Lipila response: {}", apiOutput, e);
//	            return buildErrorResponse("Invalid JSON returned from Lipila: " + apiOutput, statusCode);
//	        }
//	        
//	        // Check if Lipila threw an error
//	        boolean isFailedStatus = resp.has("status") && "Failed".equalsIgnoreCase(resp.get("status").getAsString());
//	        
//	        if (statusCode >= 400 || isFailedStatus) {
//	            String errorMessage = extractErrorMessage(resp, statusCode);
//	            return buildErrorResponse(errorMessage, statusCode);
//	        }
//	        
//	        // 9. Success Path
//	        resp.addProperty("result", "SUCCESS");
//	        resp.addProperty("statusCode", statusCode);
//	        
//	        JsonObject innerResponse = new JsonObject();
//	        
//	        if (isMobileMoney) {
//	            // ✅ FIXED: Dynamically capture the provider Lipila detected
//	            innerResponse.addProperty("payment_gateway_url", "MOBILE_MONEY_INITIATED");
//	            
//	            String detectedProvider = resp.has("paymentType") && !resp.get("paymentType").isJsonNull() 
//	                ? resp.get("paymentType").getAsString() 
//	                : "Mobile Money";
//	                
//	            innerResponse.addProperty("message", detectedProvider + " prompt sent to " + cleanPhone + ". Please enter your PIN on your phone.");
//	            innerResponse.addProperty("payment_method", "MOBILE_MONEY");
//	            innerResponse.addProperty("provider", detectedProvider);
//	            innerResponse.addProperty("lipila_payment_type", detectedProvider);
//	        } else {
//	            // Card: Get redirection URL
//	            if (resp.has("cardRedirectionUrl") && !resp.get("cardRedirectionUrl").isJsonNull()) {
//	                innerResponse.addProperty("payment_gateway_url", resp.get("cardRedirectionUrl").getAsString());
//	            } else if (resp.has("redirectUrl") && !resp.get("redirectUrl").isJsonNull()) {
//	                innerResponse.addProperty("payment_gateway_url", resp.get("redirectUrl").getAsString());
//	            } else {
//	                return buildErrorResponse("Missing redirection URL in Lipila card response", statusCode);
//	            }
//	            innerResponse.addProperty("payment_method", "CARD");
//	        }
//	        
//	        // Add transaction details
//	        if (resp.has("identifier")) {
//	            innerResponse.addProperty("transaction_id", resp.get("identifier").getAsString());
//	        }
//	        if (resp.has("status")) {
//	            innerResponse.addProperty("status", resp.get("status").getAsString());
//	        }
//	        if (resp.has("paymentType") && !isMobileMoney) { // We already added it for MoMo above
//	            innerResponse.addProperty("lipila_payment_type", resp.get("paymentType").getAsString());
//	        }
//	        
//	        JsonArray dataArray = new JsonArray(1);
//	        dataArray.add(innerResponse);
//	        resp.add("data", dataArray);
//	        
//	        return resp;
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        log.error("Exception during Lipila call for QuoteNo: {}", payment.getQuoteNo(), e);
//	        return buildErrorResponse("Exception during Lipila call: " + e.getMessage(), 500);
//	    } finally {
//	        if (httpClient != null) {
//	            try { 
//	                httpClient.close(); 
//	            } catch (Exception e) { 
//	                e.printStackTrace(); 
//	            }
//	        }
//	    }
//	}
//
//	// Helper: Extract error message from response
//	private String extractErrorMessage(JsonObject resp, int statusCode) {
//	    String errorMessage = "Unknown Lipila Error";
//	    
//	    if (resp.has("message") && !resp.get("message").isJsonNull()) {
//	        errorMessage = resp.get("message").getAsString();
//	    } else if (resp.has("errors") && !resp.get("errors").isJsonNull()) {
//	        errorMessage = resp.get("errors").toString(); 
//	    } else {
//	        switch (statusCode) {
//	            case 400: errorMessage = "Bad Request: Missing or invalid parameters"; break;
//	            case 401: errorMessage = "Unauthorized: Missing or invalid API authentication"; break;
//	            case 403: errorMessage = "Forbidden: Access denied to this resource"; break;
//	            case 429: errorMessage = "Too Many Requests: Rate limit exceeded"; break;
//	            case 500: errorMessage = "Lipila Internal Server Error"; break;
//	            case 502: errorMessage = "Bad Gateway: Lipila service unavailable"; break;
//	            case 503: errorMessage = "Service Unavailable: Lipila temporarily down"; break;
//	            case 504: errorMessage = "Gateway Timeout: Lipila response timeout"; break;
//	        }
//	    }
//	    
//	    return errorMessage;
//	}
//
//	// Helper to build standardized error responses
//	private JsonObject buildErrorResponse(String message, int statusCode) {
//	    JsonObject errorResp = new JsonObject();
//	    errorResp.addProperty("result", "ERROR");
//	    errorResp.addProperty("message", message);
//	    errorResp.addProperty("statusCode", statusCode);
//	    
//	    // Add empty data array for consistency
//	    JsonArray dataArray = new JsonArray();
//	    errorResp.add("data", dataArray);
//	    
//	    return errorResp;
//	}
//
//	// Helper to ensure phone number matches Lipila's 260XXXXXXXXX format
//	private String cleanPhoneNumber(String phoneNumber) {
//	    if (phoneNumber == null || phoneNumber.isEmpty()) {
//	        log.warn("Phone number is null or empty");
//	        return "260000000000"; // Default fallback
//	    }
//	    
//	    log.info("Original phone: {}", phoneNumber);
//	    
//	    // Remove all non-numeric characters
//	    String cleaned = phoneNumber.replaceAll("[^0-9]", "");
//	    
//	    // Add country code if missing
//	    if (!cleaned.startsWith("260")) {
//	        if (cleaned.length() == 9) {
//	            cleaned = "260" + cleaned;
//	        } else if (cleaned.length() == 10 && cleaned.startsWith("0")) {
//	            cleaned = "260" + cleaned.substring(1);
//	        }
//	    }
//	    
//	    // Trim to 12 digits if too long
//	    if (cleaned.length() > 12) {
//	        log.warn("Phone too long ({} digits), trimming to 12", cleaned.length());
//	        cleaned = cleaned.substring(0, 12);
//	    }
//	    
//	    if (cleaned.length() != 12) {
//	        log.error("Invalid phone length after cleaning: {} (expected 12)", cleaned.length());
//	    }
//	    
//	    log.info("Final clean phone (Lipila format): {}", cleaned);
//	    return cleaned;
//	}
	
	/**
	 * Lipila Payment Initiation - Handles both Card and Mobile Money
	 * Called from createOrderForPayment when vendor is "lipila"
	 */
	private JsonObject lipila(PaymentVendorMaster vendor, PaymentDetail payment, String lipilaPaymentType, String momoProvider) {
	    CloseableHttpClient httpClient = null;
	    try {
	        if (vendor == null) {
	            return buildErrorResponse("Vendor details are missing", 500);
	        }

	        // 1. GET CONFIGURATION FROM VENDOR MASTER
//	        String apiSecretKey = vendor.getApiSecretKey();  // x-api-key header value
//	        String apiSecretKey = "lsk_019d9a8f-4bb6-7dc6-90c4-edac2baeae4f";
	        String apiSecretKey = "lsk_019dd872-90de-7eb8-b488-bfde9d0cd057";
	        
//	        String baseUrl = vendor.getApiBaseUrl();          // e.g., https://api.lipila.dev
//	        String baseUrl = "https://api.lipila.dev";
	    	String baseUrl = "https://blz.lipila.io";

	        String callbackUrl = vendor.getWebhookUrlLink();  // webhook URL for Lipila to call
	        
	        // Validate payment type
	        boolean isMobileMoney = "mobile-money".equalsIgnoreCase(lipilaPaymentType);
	        if (!isMobileMoney && !"card".equalsIgnoreCase(lipilaPaymentType)) {
	            log.error("Invalid lipilaPaymentType: {}. Must be 'card' or 'mobile-money'", lipilaPaymentType);
	            return buildErrorResponse("Invalid lipilaPaymentType. Must be 'card' or 'mobile-money'", 400);
	        }

	        // 2. CONSTRUCT API URL BASED ON PAYMENT TYPE
	        // Lipila Docs: /api/v1/collections/card OR /api/v1/collections/mobile-money
	        String endpoint = isMobileMoney ? "/api/v1/collections/mobile-money" : "/api/v1/collections/card";
	        String apiUrl = baseUrl + endpoint;

	        // 3. FORMAT + VALIDATE PHONE NUMBER - Lipila requires: 260XXXXXXXXX (12 digits, no hyphens)
	        String cleanPhone = cleanPhoneNumber(payment.getReqBillToPhone());
	        if (!isValidZambianNumber(cleanPhone)) {
	            log.error("Invalid REQ_BILL_TO_PHONE '{}' (normalized: '{}') for QuoteNo: {}. Lipila requires 260XXXXXXXXX (12 digits)",
	                    payment.getReqBillToPhone(), cleanPhone, payment.getQuoteNo());
	            return buildErrorResponse("REQ_BILL_TO_PHONE: Lipila requires: 260XXXXXXXXX (12 digits)", 400);
	        }
	        // For mobile money the number is the payment instrument itself, so it must be a Zambian
	        // mobile line (2609XXXXXXXX / 2607XXXXXXXX). Card only needs a structurally valid number.
	        if (isMobileMoney && !isZambianMobileNumber(cleanPhone)) {
	            log.error("REQ_BILL_TO_PHONE '{}' (normalized: '{}') is not a Zambian mobile number for QuoteNo: {}",
	                    payment.getReqBillToPhone(), cleanPhone, payment.getQuoteNo());
	            return buildErrorResponse(
	                    "REQ_BILL_TO_PHONE: mobile money requires a Zambian mobile number (2609XXXXXXXX or 2607XXXXXXXX)", 400);
	        }

	        // 4. GET REDIRECT URLs FROM VENDOR CONFIG (for Card payments only)
	        String successUrl = vendor.getReturnUrlLink();
	        String cancelUrl = vendor.getCancelUrlLink();
	        if (successUrl != null) {
	            successUrl = successUrl.replaceAll("<QuoteNo>", payment.getQuoteNo());
	        }
	        if (cancelUrl != null) {
	            cancelUrl = cancelUrl.replaceAll("<QuoteNo>", payment.getQuoteNo());
	        }

	        // 5. DETERMINE AMOUNT AND CURRENCY
	        List<InsuranceCompanyMaster> insInfo = insuranceRepo
	                .findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(
	                        payment.getCompanyId(), "Y", new Date(), new Date());
	        BigDecimal amount;
	        if (!insInfo.isEmpty() && insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId())) {
	            amount = payment.getPremiumLc();
	        } else {
	            amount = payment.getPremiumFc();
	        }

	        // 6. BUILD REQUEST PAYLOAD - STRICTLY PER LIPILA DOCUMENTATION
	        JsonObject requestPayload = new JsonObject();

	        if (isMobileMoney) {
	            // ===== MOBILE MONEY PAYLOAD (Lipila Docs: MoMo Collections) =====
	            // REQUIRED: referenceId, amount, narration, accountNumber, currency
	            // OPTIONAL: email
	            requestPayload.addProperty("referenceId", payment.getMerchantReference());
	            requestPayload.addProperty("amount", amount.doubleValue());  // Lipila expects number
	            // requestPayload.addProperty("amount", 1.0);  // Lipila expects number
	            requestPayload.addProperty("narration", "Insurance Payment - " + payment.getQuoteNo());
	            requestPayload.addProperty("accountNumber", cleanPhone);
	            requestPayload.addProperty("currency", payment.getCurrencyId());
	            
	            if (StringUtils.isNotBlank(payment.getCustomerEmail())) {
	                requestPayload.addProperty("email", payment.getCustomerEmail());
	            }
	            
	            log.info("Lipila Mobile Money - QuoteNo: {}, Amount: {}, Phone: {}", 
	                payment.getQuoteNo(), amount, cleanPhone);
	                
	        } else {
	            // ===== CARD PAYMENT PAYLOAD (Lipila Docs: Card Collections) =====
	            // Nested structure: customerInfo + collectionRequest
	            
	            JsonObject customerInfo = new JsonObject();
	            customerInfo.addProperty("firstName", 
	                StringUtils.defaultIfBlank(payment.getReqBillToForename(), 
	                    StringUtils.defaultIfBlank(payment.getCustomerName(), "Customer")));
	            customerInfo.addProperty("lastName", 
	                StringUtils.defaultIfBlank(payment.getReqBillToSurname(), "NA"));
	            customerInfo.addProperty("phoneNumber", cleanPhone);
//	            customerInfo.addProperty("email", 
//	                StringUtils.defaultIfBlank(payment.getCustomerEmail(), "noreply@insurance.com"));
	            customerInfo.addProperty("email", 
		                StringUtils.defaultIfBlank("noreply@insurance.com", "noreply@insurance.com"));
	            customerInfo.addProperty("city", 
	                StringUtils.defaultIfBlank(payment.getReqBillToAddressCity(), "Lusaka"));
	            customerInfo.addProperty("country", 
	                StringUtils.defaultIfBlank(payment.getReqBillToCountry(), "ZM"));
	            
	            String address = "";
	            if (StringUtils.isNotBlank(payment.getReqBillToAddressLine1())) {
	                address = payment.getReqBillToAddressLine1();
	                if (StringUtils.isNotBlank(payment.getReqBillToAddressLine2())) {
	                    address += " " + payment.getReqBillToAddressLine2();
	                }
	            }
	            customerInfo.addProperty("address", StringUtils.defaultIfBlank(address, "North"));
	            customerInfo.addProperty("zip", 
	                StringUtils.defaultIfBlank(payment.getReqBillToAddrPostalCode(), "10101"));

	            JsonObject collectionRequest = new JsonObject();
	            collectionRequest.addProperty("referenceId", payment.getMerchantReference());
	            collectionRequest.addProperty("amount", amount.doubleValue());
	            // collectionRequest.addProperty("amount", 1.0);
	            collectionRequest.addProperty("currency", payment.getCurrencyId());
	            collectionRequest.addProperty("narration", "Insurance Payment - " + payment.getQuoteNo());
	            collectionRequest.addProperty("accountNumber", cleanPhone);
	            collectionRequest.addProperty("redirectUrl", successUrl);
	            collectionRequest.addProperty("backUrl", cancelUrl != null ? cancelUrl : successUrl);

	            requestPayload.add("customerInfo", customerInfo);
	            requestPayload.add("collectionRequest", collectionRequest);
	            
	            log.info("Lipila Card - QuoteNo: {}, Amount: {}, RedirectUrl: {}", 
	                payment.getQuoteNo(), amount, successUrl);
	        }

	        // 7. EXECUTE HTTP REQUEST
	        httpClient = HttpClientBuilder.create().build();
	        HttpPost postRequest = new HttpPost(apiUrl);
	        
	        // Set headers as per Lipila documentation
	        postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
	        postRequest.setHeader("accept", MediaType.APPLICATION_JSON_VALUE);
	        postRequest.setHeader("x-api-key", apiSecretKey);  // Lipila uses x-api-key, NOT Bearer
	        
	        if (StringUtils.isNotBlank(callbackUrl)) {
	            postRequest.setHeader("callbackUrl", callbackUrl);
	        }

	        StringEntity params = new StringEntity(requestPayload.toString(), StandardCharsets.UTF_8);
	        postRequest.setEntity(params);

	        log.info("==============================================");
	        log.info("Lipila Request URL: {}", apiUrl);
	        log.info("Lipila Payment Type: {}", isMobileMoney ? "Mobile Money" : "Card");
	        log.info("Lipila Request Payload: {}", requestPayload.toString());
	        log.info("==============================================");

	        // 8. GET RESPONSE
	        HttpResponse hresp = httpClient.execute(postRequest);
	        int statusCode = hresp.getStatusLine().getStatusCode();
	        String apiOutput = EntityUtils.toString(hresp.getEntity(), StandardCharsets.UTF_8);

	        log.info("==============================================");
	        log.info("Lipila Response Status: {}", statusCode);
	        log.info("Lipila Response Body: {}", apiOutput);
	        log.info("==============================================");

	        if (apiOutput == null || apiOutput.trim().isEmpty()) {
	            return buildErrorResponse("Empty response from Lipila API", statusCode);
	        }

	        JsonObject resp;
	        try {
	            resp = new Gson().fromJson(apiOutput, JsonObject.class);
	        } catch (Exception e) {
	            log.error("Failed to parse Lipila response: {}", apiOutput, e);
	            return buildErrorResponse("Invalid JSON from Lipila: " + apiOutput, statusCode);
	        }

	        // 9. CHECK FOR ERRORS
	        boolean isFailedStatus = resp.has("status") && "Failed".equalsIgnoreCase(getAsStringSafe(resp, "status", ""));
	        if (statusCode >= 400 || isFailedStatus) {
	            String errorMsg = extractErrorMessage(resp, statusCode);
	            return buildErrorResponse(errorMsg, statusCode);
	        }

	        // 10. BUILD SUCCESS RESPONSE
	        resp.addProperty("result", "SUCCESS");
	        resp.addProperty("statusCode", statusCode);

	        JsonObject innerResponse = new JsonObject();

	        if (isMobileMoney) {
	            // Mobile Money: No redirect URL, user gets USSD/push prompt
	            innerResponse.addProperty("payment_gateway_url", "MOBILE_MONEY_INITIATED");
	            
	            String provider = getAsStringSafe(resp, "paymentType", "Mobile Money");
	                
	            innerResponse.addProperty("message", 
	                provider + " prompt sent to " + cleanPhone + ". Please enter your PIN on your phone.");
	            innerResponse.addProperty("payment_method", "MOBILE_MONEY");
	            innerResponse.addProperty("provider", provider);
	            
	        } else {
	            // Card: Extract redirection URL
	            String cardRedir = getAsStringSafe(resp, "cardRedirectionUrl", null);
	            String redir = getAsStringSafe(resp, "redirectUrl", null);
	            
	            if (cardRedir != null) {
	                innerResponse.addProperty("payment_gateway_url", cardRedir);
	            } else if (redir != null) {
	                innerResponse.addProperty("payment_gateway_url", redir);
	            } else {
	                return buildErrorResponse("Missing redirection URL in Lipila card response", statusCode);
	            }
	            innerResponse.addProperty("payment_method", "CARD");
	        }

	        // Add transaction identifiers
	        if (resp.has("identifier")) {
	            innerResponse.addProperty("transaction_id", getAsStringSafe(resp, "identifier", ""));
	        }
	        if (resp.has("referenceId")) {
	            innerResponse.addProperty("lipila_reference", getAsStringSafe(resp, "referenceId", ""));
	        }
	        if (resp.has("status")) {
	            innerResponse.addProperty("status", getAsStringSafe(resp, "status", ""));
	        }

	        JsonArray dataArray = new JsonArray(1);
	        dataArray.add(innerResponse);
	        resp.add("data", dataArray);

	        return resp;

	    } catch (Exception e) {
	        log.error("Exception during Lipila call for QuoteNo: {}", payment.getQuoteNo(), e);
	        e.printStackTrace();
	        return buildErrorResponse("Exception: " + e.getMessage(), 500);
	    } finally {
	        if (httpClient != null) {
	            try { httpClient.close(); } catch (Exception e) { /* ignore */ }
	        }
	    }
	}
	
	/**
	 * Clean phone number to Lipila format: 260XXXXXXXXX (12 digits, no hyphens).
	 * Returns an empty string when the input is blank - callers must validate the result with
	 * {@link #isValidZambianNumber(String)} instead of relying on a placeholder number.
	 */
	private String cleanPhoneNumber(String phoneNumber) {

		if (StringUtils.isBlank(phoneNumber)) {
			log.warn("Phone number is null/empty");
			return "";
		}

		// Remove spaces, hyphens, symbols (also drops a leading "+")
		String cleaned = phoneNumber.replaceAll("[^0-9]", "");

		// International access code
		// 00260976008698 -> 260976008698
		if (cleaned.startsWith("00")) {
			cleaned = cleaned.substring(2);
		}

		// CASE 1:
		// Already valid international format
		// 260976008698

		if (cleaned.startsWith("260") && cleaned.length() == 12) {
			// Keep as-is
		}

		// CASE 2:
		// International format with national trunk zero
		// 2600976008698 -> 260976008698

		else if (cleaned.startsWith("2600") && cleaned.length() == 13) {

			cleaned = "260" + cleaned.substring(4);
		}

		// CASE 3:
		// Local Zambia number
		// 0976008698 -> 260976008698

		else if (cleaned.startsWith("0") && cleaned.length() == 10) {

			cleaned = "260" + cleaned.substring(1);
		}

		// CASE 4:
		// Without leading zero
		// 976008698 -> 260976008698

		else if (cleaned.length() == 9) {

			cleaned = "260" + cleaned;
		}
		// Invalid format
		else {
			log.warn("Unexpected phone number format: {}", cleaned);
		}

		log.info("Phone cleaned: {} -> {}", phoneNumber, cleaned);
		return cleaned;
	}

	/**
	 * Structural check for a Zambian number in Lipila format: country code 260 + 9 digits.
	 */
	private boolean isValidZambianNumber(String cleanPhone) {
		return cleanPhone != null && cleanPhone.matches("260\\d{9}");
	}

	/**
	 * Mobile-money check: a Zambian mobile line - 09X/07X nationally, i.e. 2609XXXXXXXX or
	 * 2607XXXXXXXX (MTN, Airtel, Zamtel). Landlines cannot receive a mobile money prompt.
	 */
	private boolean isZambianMobileNumber(String cleanPhone) {
		return cleanPhone != null && cleanPhone.matches("260[79]\\d{8}");
	}

	/**
	 * Extract error message from Lipila response
	 */
	private String extractErrorMessage(JsonObject resp, int statusCode) {
	    String errorMessage = "Unknown Lipila error";
	    
	    // Check for message field
	    if (resp.has("message") && !resp.get("message").isJsonNull()) {
	        JsonElement msg = resp.get("message");
	        if (msg.isJsonPrimitive()) {
	            return msg.getAsString();
	        } else if (msg.isJsonArray()) {
	            return msg.getAsJsonArray().toString();
	        }
	    }
	    
	    // Check for errors object
	    if (resp.has("errors") && !resp.get("errors").isJsonNull()) {
	        return resp.get("errors").toString();
	    }
	    
	    // Fallback based on HTTP status code
	    switch (statusCode) {
	        case 400: return "Bad Request: Missing or invalid parameters";
	        case 401: return "Unauthorized: Invalid API key";
	        case 403: return "Forbidden: Access denied to this resource";
	        case 429: return "Too Many Requests: Rate limit exceeded";
	        case 500: return "Lipila internal server error";
	        case 502: return "Bad Gateway: Lipila service unavailable";
	        case 503: return "Service Unavailable: Lipila temporarily down";
	        case 504: return "Gateway Timeout: Lipila response timeout";
	        default: return "Lipila error (HTTP " + statusCode + ")";
	    }
	}

	/**
	 * Build standardized error response
	 */
	private JsonObject buildErrorResponse(String message, int statusCode) {
	    JsonObject errorResp = new JsonObject();
	    errorResp.addProperty("result", "ERROR");
	    errorResp.addProperty("message", message);
	    errorResp.addProperty("statusCode", statusCode);
	    
	    JsonArray dataArray = new JsonArray();
	    errorResp.add("data", dataArray);
	    
	    return errorResp;
	}
		
	
	private JsonObject pesapal(PaymentVendorMaster vendor, PaymentDetail payment) {
		try {
			String apiKey = null;
			String apiSecret = null;
			String authUrl = null;
			String orderPath =null;
			String paymentUrl=null;
			String redirect_url=null;
			String cancel_url=null;
			String webHookUrl=null;
			String signedFields="";
			String remarks="";
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				authUrl=vendor.getApiBaseUrl();
				paymentUrl=vendor.getPaymentUrlLink();
				//vendorCode=vendor.getVendorCode();
				redirect_url=vendor.getReturnUrlLink();
				cancel_url=vendor.getCancelUrlLink();
				webHookUrl=vendor.getWebhookUrlLink();
				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				cancel_url=cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				signedFields=vendor.getSignedFields();
				remarks=vendor.getRemarks();
			}
			
			//Create Auth
			String token="",notificationId="";
			CloseableHttpClient httpClient = null;
			try {
				JsonObject request=new JsonObject();
				request.addProperty("consumer_key",vendor.getApiKey().toString());
				request.addProperty("consumer_secret", vendor.getApiSecretKey().toString());
				
				httpClient= HttpClientBuilder.create().build();
				HttpPost postRequest = new HttpPost(authUrl);
				postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
				StringEntity params = new StringEntity(request.toString());
				postRequest.setEntity(params);
	            HttpResponse hresp  = httpClient.execute(postRequest);

	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
	            String apiOutput = EntityUtils.toString(httpEntity);
	            System.out.println("output"+ apiOutput);
	            JsonObject tokResponse = new Gson().fromJson(apiOutput, JsonObject.class);
	            token=tokResponse.get("token").getAsString();
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(httpClient!=null) {
					httpClient.close();
				}
			}
			if(StringUtils.isNotBlank(token)) {
				try {
					JsonObject request=new JsonObject();
					request.addProperty("url",webHookUrl);
					request.addProperty("ipn_notification_type","POST");

					httpClient= HttpClientBuilder.create().build();
					HttpPost postRequest = new HttpPost(remarks);
					postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
					postRequest.setHeader("Accept",MediaType.APPLICATION_JSON_VALUE);														
					postRequest.setHeader("Authorization","Bearer "+ token);

					StringEntity params = new StringEntity(request.toString());
					postRequest.setEntity(params);
					HttpResponse hresp  = httpClient.execute(postRequest);

					org.apache.http.HttpEntity httpEntity = hresp.getEntity();
					String apiOutput = EntityUtils.toString(httpEntity);
					System.out.println("output"+ apiOutput);
					JsonObject resp = new Gson().fromJson(apiOutput, JsonObject.class);
					notificationId=resp.get("ipn_id").getAsString();
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					if(httpClient!=null) {
						httpClient.close();
					}
				}



				try {
					JsonObject request=new JsonObject();
					request.addProperty("id",payment.getMerchantReference());
					request.addProperty("currency", payment.getCurrencyId());
					List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
					if (Integer.valueOf(4).equals(vendor.getProductId())  
				        && "100020".equals(insInfo.get(0).getCompanyId())) {
					request.addProperty("amount", payment.getPremiumLc().toPlainString());
					request.addProperty("currency", "KES");
				} else {
				    
				    if (insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId())) {
				    	request.addProperty("amount", payment.getPremiumLc().toPlainString());
				    } else {
				    	request.addProperty("amount", payment.getPremiumFc().toPlainString());
				    }
				}


					request.addProperty("description", payment.getQuoteNo()+" Payment Request");
					request.addProperty("redirect_mode", "PARENT_WINDOW");
					request.addProperty("callback_url", redirect_url);
					request.addProperty("cancellation_url",cancel_url);
					request.addProperty("notification_id", notificationId);
					request.addProperty("branch", "");

					JsonObject billingAddr=new JsonObject();
					billingAddr.addProperty("phone_number", payment.getReqBillToPhone());
					billingAddr.addProperty("email_address",payment.getReqBillToEmail());
					billingAddr.addProperty("country_code", payment.getReqBillToCountry());
					billingAddr.addProperty("first_name", payment.getReqBillToForename());
					billingAddr.addProperty("middle_name", "");
					billingAddr.addProperty("last_name", payment.getReqBillToSurname());				
					billingAddr.addProperty("line_1", payment.getReqBillToAddressLine1());
					billingAddr.addProperty("line_2", payment.getReqBillToAddressLine2());
					billingAddr.addProperty("city", payment.getReqBillToAddressCity());
					billingAddr.addProperty("state", payment.getReqBillToAddressState());
					billingAddr.addProperty("postal_code", payment.getReqBillToAddrPostalCode());
					billingAddr.addProperty("zip_code", payment.getReqBillToAddrPostalCode());
					request.add("billing_address", billingAddr);

					httpClient= HttpClientBuilder.create().build();
					HttpPost postRequest = new HttpPost(paymentUrl);
					postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
					postRequest.setHeader("Accept",MediaType.APPLICATION_JSON_VALUE);														
					postRequest.setHeader("Authorization","Bearer "+ token);
					System.out.println(payment.getMerchantReference()+"Payment Request "+ request.toString());
					StringEntity params = new StringEntity(request.toString());
					postRequest.setEntity(params);
					HttpResponse hresp  = httpClient.execute(postRequest);

					org.apache.http.HttpEntity httpEntity = hresp.getEntity();
					String apiOutput = EntityUtils.toString(httpEntity);
					System.out.println("output"+ apiOutput);
					JsonObject resp = new Gson().fromJson(apiOutput, JsonObject.class);



					resp.addProperty("result", "SUCCESS");

					JsonObject innerResponse=new JsonObject();
					innerResponse.addProperty("payment_gateway_url", resp.get("redirect_url").getAsString());
					innerResponse.addProperty("order_tracking_id", resp.get("order_tracking_id").getAsString());
					payment.setResSignature(resp.get("order_tracking_id").getAsString());	
					paymentDetailRepo.save(payment);
					JsonArray asJsonArray =new JsonArray(1);
					asJsonArray.add(innerResponse);
					resp.add("data", asJsonArray);
					return resp;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private JsonObject ipayafrica(PaymentVendorMaster vendor, PaymentDetail payment) {
		try {
			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;
			String orderPath =null;
			String vendorCode=null;
			String redirect_url=null;
			String cancel_url=null;
			String webHookUrl=null;
			String signedFields="";
			String remarks="";
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				orderPath=vendor.getPaymentUrlLink();
				vendorCode=vendor.getVendorCode();
				redirect_url=vendor.getReturnUrlLink();
				cancel_url=vendor.getCancelUrlLink();
				webHookUrl=vendor.getWebhookUrlLink();

				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				cancel_url=cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				signedFields=vendor.getSignedFields();
				remarks=vendor.getRemarks();
			}
			// Data needed by iPay
			LinkedHashMap<String, String> fields = new LinkedHashMap<>();
			String[] defaultfield = signedFields.split("&");
			for (int i = 0; i < defaultfield.length; i++) {
				String[] keyValue = defaultfield[i].split("=");
				fields.put(keyValue[0], keyValue[1]);
			}
			fields.put("oid", payment.getMerchantReference());
			fields.put("inv", payment.getQuoteNo());
			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				fields.put("ttl",  payment.getPremiumLc().toPlainString());
			else
				fields.put("ttl",  payment.getPremiumFc().toPlainString());
			fields.put("tel", payment.getReqBillToPhone());
			fields.put("eml", StringUtils.isBlank(payment.getCustomerEmail())?"hoinfo@firstassurance.co.ke":payment.getCustomerEmail());
			fields.put("vid", vendor.getApiKey());
			fields.put("curr", payment.getCurrencyId());
			/*fields.put("p1", "");
			fields.put("p2", "");
			fields.put("p3", "");
			fields.put("p4", "");*/
			fields.put("cbk", webHookUrl);
			//fields.put("lbk", "");
			fields.put("cst", "2");
			fields.put("crl", "0");
			StringBuilder datastring = new StringBuilder();
			fields.forEach((key, value) -> datastring.append(value));
			fields.put("hsh",  ipayafricaHash(datastring.toString().trim(),vendor.getApiSecretKey()));

			List<NameValuePair> nparms=new ArrayList<>();
			for( Entry<String, String> key:fields.entrySet()) {
				nparms.add(new BasicNameValuePair(key.getKey(), key.getValue()));
			}
			String []remarksArray=remarks.split("&");
			for (int i = 0; i < remarksArray.length; i++) {
				String[] keyValue = remarksArray[i].split("=");
				nparms.add(new BasicNameValuePair(keyValue[0], keyValue[1]));
			}
			String url=vendor.getApiBaseUrl()+vendor.getPaymentUrlLink();  
			try {
				URI uri = new URIBuilder(url).addParameters(nparms).build();
				String responseUrl=uri.toURL().toString();
				System.out.println( payment.getMerchantReference()+"-->"+responseUrl);
				JsonObject response = new JsonObject();
				response.addProperty("result", "SUCCESS");				
				JsonObject innerResponse=new JsonObject();
				innerResponse.addProperty("payment_gateway_url",responseUrl);
				JsonArray asJsonArray =new JsonArray(1);
				asJsonArray.add(innerResponse);
				response.add("data", asJsonArray);
				return response;
				
				/*try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
					String responseBody = EntityUtils.toString(response.getEntity());
					System.out.println(responseBody);
				}*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	 public static String ipayafricaHash(String data, String key) throws Exception {
	        Mac sha1Hmac = Mac.getInstance("HmacSHA1");
	        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
	        sha1Hmac.init(secretKey);
	        byte[] hashBytes = sha1Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
	        //return Base64.getEncoder().encodeToString(hashBytes);
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
	    }
	 
	private JsonObject selcomPayment(PaymentVendorMaster vendor,PaymentDetail payment) {
		try {

			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;
			String orderPath =null;
			String vendorCode=null;
			String redirect_url=null;
			String cancel_url=null;
			String webHookUrl=null;
			String signedFields="";
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				orderPath=vendor.getPaymentUrlLink();
				vendorCode=vendor.getVendorCode();
				redirect_url=vendor.getReturnUrlLink();
				cancel_url=vendor.getCancelUrlLink();
				webHookUrl=vendor.getWebhookUrlLink();

				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				cancel_url=cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				signedFields=vendor.getSignedFields();
			}


			// data
			JsonObject orderDict = new JsonObject();
			orderDict.addProperty("vendor",vendorCode);
			orderDict.addProperty("order_id",payment.getMerchantReference());
			orderDict.addProperty("buyer_email", StringUtils.isBlank(payment.getCustomerEmail())?"info@alliance.co.tz":payment.getCustomerEmail() );
			orderDict.addProperty("buyer_name", payment.getCustomerName());
			orderDict.addProperty("buyer_userid", "");
			orderDict.addProperty("buyer_phone", payment.getReqBillToPhone());
			orderDict.addProperty("gateway_buyer_uuid", "");
			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				orderDict.addProperty("amount",  payment.getPremiumLc().toPlainString());
			else
				orderDict.addProperty("amount",  payment.getPremiumFc().toPlainString());

			orderDict.addProperty("currency",payment.getCurrencyId());

			//orderDict.addProperty("amount",100);					 
			//4orderDict.addProperty("currency","TZS");
			orderDict.addProperty("payment_methods","ALL");
			orderDict.addProperty("redirect_url",StringUtils.isNotBlank(redirect_url)?Base64.getEncoder().encodeToString(redirect_url.getBytes("UTF-8")):"");
			orderDict.addProperty("cancel_url",StringUtils.isNotBlank(cancel_url)?Base64.getEncoder().encodeToString(cancel_url.getBytes("UTF-8")):"");
			orderDict.addProperty("webhook",StringUtils.isNotBlank(webHookUrl)?Base64.getEncoder().encodeToString(webHookUrl.getBytes("UTF-8")):"");
			orderDict.addProperty("billing.firstname" , payment.getReqBillToForename());
			orderDict.addProperty("billing.lastname" , payment.getReqBillToSurname());
			orderDict.addProperty("billing.address_1" , payment.getReqBillToAddressLine1()); 
			orderDict.addProperty("billing.address_2" ,payment.getReqBillToAddressLine2());		
			orderDict.addProperty("billing.city" , payment.getReqBillToAddressCity()); 
			orderDict.addProperty("billing.state_or_region" , payment.getReqBillToAddressState());  
			orderDict.addProperty("billing.postcode_or_pobox" ,StringUtils.isBlank(payment.getReqBillToAddrPostalCode())?"99999":payment.getReqBillToAddrPostalCode());  
			orderDict.addProperty("billing.country" , payment.getReqBillToCountry());  
			orderDict.addProperty("billing.phone" , payment.getReqBillToPhone());
			/*
					 orderDict.addProperty("shipping.firstname" ,  payment.getReqBillToForename());
					 orderDict.addProperty("shipping.lastname" ,  payment.getReqBillToSurname());
			 */
			//orderDict.addProperty("shipping.address_1" , payment.getReqBillToAddressLine1());
			orderDict.addProperty("shipping.address_2" , payment.getReqBillToAddressLine2());
			orderDict.addProperty("shipping.city" , payment.getReqBillToAddressCity());
			orderDict.addProperty("shipping.state_or_region" , payment.getReqBillToAddressState());  
			orderDict.addProperty("shipping.postcode_or_pobox" ,StringUtils.isBlank(payment.getReqBillToAddrPostalCode())?"99999":payment.getReqBillToAddrPostalCode());  
			orderDict.addProperty("shipping.country" ,  payment.getReqBillToCountry()); 
			//orderDict.addProperty("shipping.phone" , payment.getReqBillToPhone());
			orderDict.addProperty("buyer_remarks","None");
			orderDict.addProperty("merchant_remarks","None");
			orderDict.addProperty("no_of_items",  1);
			List<String> signRemove=new LinkedList<String>();
			for (Entry<String, JsonElement> entry : orderDict.entrySet()) {
				if(!signedFields.contains(entry.getKey())) {
					signRemove.add(entry.getKey());
				} 
			}					
			for(String key:signRemove) {
				orderDict.remove(key);
			}
			// initalize a new Client instace with values of the base url, api key and api secret
			ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
			//post data
			JsonObject response = client.postFunc(orderPath ,orderDict);
			return response;
		}catch(Exception e) {
			e.printStackTrace();			
		}
		return null;
	}
	
	@Override
	public JsonObject methodWebhook(Map<String,Object> jsonData) {
		try {
			log.info("WEBHOOK START"+jsonData);
			for (Entry<String, Object> key : jsonData.entrySet()) {
				 System.out.println(key.getKey() +"--"+key.getValue());

			}
			log.info("WEBHOOK END");
			String orderId=jsonData.get("order_id")==null?
					jsonData.get("externalId")==null?"":jsonData.get("externalId").toString()
							:jsonData.get("order_id").toString();
			PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(orderId,"PENDING");
			return orderStatus(payment.getQuoteNo(), "");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public JsonObject lipilamethodWebhook(Map<String, Object> jsonData) {
	    try {
	    	
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");

	        log.info("==============================================");
	        log.info("LIPILA WEBHOOK RECEIVED");
	        log.info("Callback Payload: {}", jsonData);
	        log.info("==============================================");

	        // 1. Extract reference ID from webhook payload
	        String referenceId = null;
	        String policyNo = ""; 
	        if (jsonData.containsKey("referenceId")) {
	            referenceId = jsonData.get("referenceId").toString();
	        } 

	        if (StringUtils.isBlank(referenceId)) {
	            log.error("Webhook missing referenceId/identifier");
	            JsonObject err = new JsonObject();
	            err.addProperty("result", "FAIL");
	            err.addProperty("message", "Missing transaction identifier");
	            return err;
	        }

	        log.info("Processing webhook for referenceId: {}", referenceId);

	        // 2. Find payment record
	        PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(referenceId, "PENDING");
	        if (payment == null) {
	            payment = paymentDetailRepo.findByMerchantReference(referenceId);
	            if (payment == null) {
	                log.warn("No payment found for reference: {}", referenceId);
	                JsonObject resp = new JsonObject();
	                resp.addProperty("result", "FAIL");
	                resp.addProperty("message", "Payment not found");
	                return resp;
	            }
	            
//	            if ("ACCEPTED".equals(payment.getPaymentStatus()) || "FAILED".equals(payment.getPaymentStatus())) {
//	                log.info("Payment already in final status: {}", payment.getPaymentStatus());
//	                JsonObject resp = new JsonObject();
//	                resp.addProperty("result", "COMPLETED");
//	                resp.addProperty("message", "Payment already processed");
//	                return resp;
//	            }
	        }

	        log.info("Found payment - QuoteNo: {}, Current Status: {}", payment.getQuoteNo(), payment.getPaymentStatus());

	        // 3. Parse status from webhook payload
	        String status = "";
	        if (jsonData.containsKey("status")) {
	            status = jsonData.get("status").toString();
	        } else if (jsonData.containsKey("payment_status")) {
	            status = jsonData.get("payment_status").toString();
	        }

	        // 4. Update PaymentDetail status from webhook data
	        if ("Successful".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status) || "Success".equalsIgnoreCase(status)) {
	            payment.setPaymentStatus("ACCEPTED");
	            payment.setAuthResponse(status);
	            payment.setResponseMessage("Payment confirmed via Lipila webhook");
	            if (jsonData.containsKey("identifier")) {
	                payment.setAuthTransRefNo(jsonData.get("identifier").toString());
	            }
	            payment.setResponseTime(new Date());
	            paymentDetailRepo.save(payment);
	            log.info("Webhook updated PaymentDetail to ACCEPTED for {}", payment.getQuoteNo());
	        } else if ("Failed".equalsIgnoreCase(status)) {
	            payment.setPaymentStatus("FAILED");
	            payment.setAuthResponse(status);
	            payment.setResponseMessage("Payment failed via Lipila webhook");
	            payment.setResponseTime(new Date());
	            paymentDetailRepo.save(payment);
	            log.info("Webhook updated PaymentDetail to FAILED for {}", payment.getQuoteNo());
	        }
	        
	        // 4b. Update PaymentInfo for both ACCEPTED and FAILED
	        PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(
	                payment.getQuoteNo(), payment.getPaymentId());
	        boolean proceedWithPolicyGeneration = false;
	        
	        if (paymentInfo != null) {
	            if (!"ACCEPTED".equals(paymentInfo.getPaymentStatus())
	                    && ("ACCEPTED".equals(payment.getPaymentStatus()) || "FAILED".equals(payment.getPaymentStatus()))) {
	                paymentInfo.setPaymentStatus(payment.getPaymentStatus());
	                paymentInfo.setUpdatedDate(new Date());
	                paymentInfo.setMerchantReference(payment.getMerchantReference());
	                paymentinforepo.save(paymentInfo);
	                log.info("PaymentInfo updated to {} for QuoteNo: {}", payment.getPaymentStatus(), payment.getQuoteNo());
	            }

	            // A UI order-status poll can already have flipped PaymentInfo to ACCEPTED (lipilaOrderStatus
	            // syncs it), so that transition can no longer be the trigger - it would leave a paid policy
	            // ungenerated. Duplicates are prevented by the policyNo check in 5c instead.
	            if ("ACCEPTED".equals(payment.getPaymentStatus())) {
	                proceedWithPolicyGeneration = true;
	            }
	        }

	        // 5. If ACCEPTED — acquire token and generate policy inline (NOT via orderStatus)
	        if ("ACCEPTED".equals(payment.getPaymentStatus())) {
	            try {
	                // 5a. Get vendor for this company
	                List<PaymentVendorMaster> vendorList = paymentVendorRepo
	                        .findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payment.getCompanyId(), "Y", "1");
	                PaymentVendorMaster vendor = (vendorList != null && !vendorList.isEmpty()) ? vendorList.get(0) : null;

	                // 5b. Call Lipila check-status to enrich payment fields (identifier, amount etc.)
	                if (vendor != null) {
	                    log.info("Calling lipilaOrderStatus for enrichment - QuoteNo: {}", payment.getQuoteNo());
	                    lipilaOrderStatus(payment, vendor);  // updates payment fields & saves
	                }

	                // 5c. Proceed with policy generation if PaymentInfo was just updated to ACCEPTED
	                if (proceedWithPolicyGeneration && paymentInfo != null) {
	                    HomePositionMaster home = homerepo.findByQuoteNo(payment.getQuoteNo());
	                    boolean policyAlreadyGenerated = home != null && StringUtils.isNotBlank(home.getPolicyNo());
	                    log.info("Policy generation gate - QuoteNo: {}, WhatsappPolicy: {}, LipilaPaymentType: {}, ExistingPolicyNo: {}",
	                            payment.getQuoteNo(), home != null ? home.getWhatsappPolicy() : null,
	                            payment.getLipilaPaymentType(), home != null ? home.getPolicyNo() : null);

	                    // Lipila is webhook-driven end to end: orderStatus() deliberately skips generation for
	                    // this vendor, so every lipila payment reaching this webhook - whatsapp, card or
	                    // mobile-money - is generated here, and only here.
	                    if (policyAlreadyGenerated) {
	                        log.info("Policy {} already exists for QuoteNo: {} - skipping duplicate generation",
	                                home.getPolicyNo(), payment.getQuoteNo());
	                    } else if (paymentInfo.getProductId() != 3 && paymentInfo.getProductId() != 11) {

	                        // 5d. Acquire login token for policy generation
	                        String token = "";
	                        try {
	                            LoginRequest mslogin = new LoginRequest();
	                            mslogin.setLoginId("guest");
	                            mslogin.setPassword("Admin@01");
	                            mslogin.setReLoginKey("Y");
	                            CommonLoginRes checkUserLogin = authservice.checkUserLogin(mslogin, null);
	                            ClaimLoginResponse commonResponse = (ClaimLoginResponse) checkUserLogin.getCommonResponse();
	                            if (commonResponse != null) {
	                                token = commonResponse.getToken();
	                                log.info("Auth token acquired for policy generation");

//	                                // 5e. Call TIRA integration
//	                                TiraFrameReqCall tira = new TiraFrameReqCall();
//	                                tira.setQuoteNo(payment.getQuoteNo());
//	                                tiraService.callTiraIntegeration(tira, token);
//	                                log.info("TIRA integration called for QuoteNo: {}", payment.getQuoteNo());
	                            }
	                        } catch (Exception e) {
	                            log.error("Error acquiring token or calling TIRA for QuoteNo: {}", payment.getQuoteNo(), e);
	                        }

	                        // 5f. Generate policy
	                        try {
	                            PaymentDetailsSaveReq req = new PaymentDetailsSaveReq();
	                            req.setQuoteNo(payment.getQuoteNo());
	                            req.setCreatedBy(payment.getUpdatedBy());
	                            req.setPaymentType(payment.getPaymentType());
	                            paymentService.generatePolicy(paymentInfo, req, payment, token);
	                            log.info("generatePolicy called for QuoteNo: {}", payment.getQuoteNo());
	                        } catch (Exception e) {
	                            log.error("Error calling generatePolicy for QuoteNo: {}", payment.getQuoteNo(), e);
	                        }

//	                        // 5g. Call RSTA if applicable
//	                        if (paymentInfo.getProductId() == 5 && Arrays.asList("100046").contains(paymentInfo.getCompanyId())) {
//	                            callRSTAIntegeration(payment.getQuoteNo());
//	                        }
	                    }
	                }

	                // 5h. Read policyNo from HomePositionMaster after generation
	                HomePositionMaster homePosition = homerepo.findByQuoteNo(payment.getQuoteNo());
	                if (homePosition != null && StringUtils.isNotBlank(homePosition.getPolicyNo())) {
	                    policyNo = homePosition.getPolicyNo();
	                    log.info("Policy number obtained from HomePositionMaster: {}", policyNo);
	                    
	                    // Check if bot notification is needed based on WhatsappPolicy flag
	                    if ("Y".equalsIgnoreCase(homePosition.getWhatsappPolicy())) {
	                        callAiLifeBotApi(payment, policyNo);
	                        
	                        // Trigger internal workflows asynchronously (Integration, Reinsurance, Email)
	                        log.info("Triggering internal async workflows for QuoteNo: {}", payment.getQuoteNo());
	                        triggerInternalAsyncWorkflows(payment, homePosition);
	                    }
	                } else {
	                    log.warn("Policy number not yet available, falling back to QuoteNo: {}", payment.getQuoteNo());
	                }

	            } catch (Exception e) {
	                log.error("Error during policy generation in webhook for QuoteNo: {}", payment.getQuoteNo(), e);
	            }
	        }



	        JsonObject result = new JsonObject();
	        result.addProperty("result", "COMPLETED");
	        result.addProperty("message", "Webhook processed successfully");
	        result.addProperty("policyNo", policyNo);
	        log.info("Webhook processing completed for QuoteNo: {}, PolicyNo: {}", payment.getQuoteNo(), policyNo);
	        System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	    	System.out.println("########################################################################");
	        return result;

	    } catch (Exception e) {
	        log.error("Error processing Lipila webhook", e);
	        e.printStackTrace();
	        JsonObject err = new JsonObject();
	        err.addProperty("result", "ERROR");
	        err.addProperty("message", e.getMessage());
	        return err;
	    }
	}
	
	@Override
	@Transactional
	public JsonObject orderStatus(String orderId,String token) {
		try {
			List<PaymentDetail> payments = paymentDetailRepo.findByQuoteNo(orderId);
			

			if(payments!=null  && !payments.isEmpty() ) {
				PaymentDetail paymentdetail =payments.get(0);
				List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payments.get(0).getCompanyId(),"Y","1");
				
				PaymentVendorMaster vendor = paymentId.get(0);		
				PaymentInfo paymentInfoold = paymentinforepo.findByQuoteNoAndPaymentId(orderId, paymentdetail.getPaymentId() );
				if((paymentInfoold.getProductId().equals(5) && paymentdetail.getCompanyId().equals("100019") &&  paymentdetail.getPaymentType().equals("6")) ||
						(paymentInfoold.getProductId().equals(125) && paymentdetail.getCompanyId().equals("100019"))) {
					vendor =getMtpVendor(paymentdetail.getCompanyId());	
				}else {
				 vendor = paymentId.get(0);
				}
				boolean isPaymentdone=false;
				JsonObject j=new JsonObject();
				for(PaymentDetail payment:payments) {

					JsonObject responses=null;

					if("lipila".equals(vendor.getVendorName())) {
						responses=lipilaOrderStatus(payment,vendor);
					}else if("pesapal".equals(vendor.getVendorName())) {
						responses=pesapalOrderStatus(payment,vendor);
					} else if("peach".equals(vendor.getVendorName())) {
						responses=peachOrderStatus(payment,vendor);
					}
//					else if("mpesa".equals(vendor.getVendorName())){
//						responses=mpesaOrderStatus(payment,vendor);
//					}
					else if ("mpesa".equals(vendor.getVendorName())) {
						System.out.println("Mpesa");
						responses = mpesaOrderStatus(payment, vendor);
					}else if("tingg".equals(vendor.getVendorName())) {
						responses = tinggOrderStatus(payment, vendor);
					}
					else if ("mtp".equals(vendor.getVendorName())) {
					    responses = mtpOrderStatus(payment, vendor);
					}else if ("smartpay".equals(vendor.getVendorName())) {
						responses = smartpayOrderStatus(payment, vendor);
					}
					else {
						responses=selcomOrderStatus(payment,vendor);
					}


					if("ACCEPTED".equals(payment.getPaymentStatus())|| "FAILED".equals(payment.getPaymentStatus())) { 
						PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());

						// Lipila policies are generated only by lipilamethodWebhook. The UI polls this API for
						// card payments, so generating here as well would issue the same policy twice.
						// lipilaOrderStatus above already synced PaymentInfo - this only reports status back.
						if("lipila".equals(vendor.getVendorName())) {
							isPaymentdone = paymentInfo != null && "ACCEPTED".equals(paymentInfo.getPaymentStatus());
							log.info("Lipila order-status: status check only for QuoteNo: {} (PaymentInfo={}), policy generation left to the webhook",
									payment.getQuoteNo(), paymentInfo != null ? paymentInfo.getPaymentStatus() : null);
						}
						else if(!"ACCEPTED".equals(paymentInfo.getPaymentStatus())) {
							paymentInfo.setPaymentStatus(payment.getPaymentStatus());
							paymentInfo.setUpdatedDate(new Date());
							paymentInfo.setMerchantReference(payment.getMerchantReference());
							paymentinforepo.save(paymentInfo);
							if(paymentInfo.getProductId()!=3 && paymentInfo.getProductId()!=11) {
							if("ACCEPTED".equals(payment.getPaymentStatus())) {
								try {
									LoginRequest mslogin=new LoginRequest();
									mslogin.setLoginId("guest");
									mslogin.setPassword("Admin@01");
									mslogin.setReLoginKey("Y");
									CommonLoginRes checkUserLogin = authservice.checkUserLogin(mslogin,null);
									ClaimLoginResponse commonResponse =(ClaimLoginResponse) checkUserLogin.getCommonResponse();
									if(commonResponse!=null) {
										String tokeen = commonResponse.getToken();
										TiraFrameReqCall tira=new TiraFrameReqCall();
										tira.setQuoteNo(orderId);
										tiraService.callTiraIntegeration(tira, tokeen);
										
									}
								}catch(Exception e) {
									e.printStackTrace();
								}
								PaymentDetailsSaveReq req=new PaymentDetailsSaveReq();
								req.setQuoteNo(payment.getQuoteNo());
								req.setCreatedBy(payment.getUpdatedBy());
								req.setPaymentType(payment.getPaymentType());

								// Safety net against double issuance: a policy number already on the quote means
								// another path (webhook, or a concurrent poll) has generated it already.
								HomePositionMaster existingPolicy = homerepo.findByQuoteNo(payment.getQuoteNo());
								if(existingPolicy != null && StringUtils.isNotBlank(existingPolicy.getPolicyNo())) {
									log.info("Policy {} already exists for QuoteNo: {} - skipping duplicate generation",
											existingPolicy.getPolicyNo(), payment.getQuoteNo());
								}else {
									paymentService.generatePolicy(paymentInfo,req,payment,token);
									if(paymentInfo.getProductId() == 5 && Arrays.asList("100046").contains(paymentInfo.getCompanyId())) {
										callRSTAIntegeration(payment.getQuoteNo());
									}

									// Mozambique (100048) WhatsApp-only post-policy workflow parity:
									// Trigger AiLifeBot + integration/push/quote + RI + mail only when WhatsappPolicy=Y.
									// This does not affect web UI because UI flow does not set WhatsappPolicy=Y.
									if ("100048".equals(paymentInfo.getCompanyId())) {
										try {
											HomePositionMaster homePos = homerepo.findByQuoteNo(payment.getQuoteNo());
											if (homePos != null && StringUtils.isNotBlank(homePos.getPolicyNo())
													&& "Y".equalsIgnoreCase(homePos.getWhatsappPolicy())) {
												callAiLifeBotApi(payment, homePos.getPolicyNo());
												triggerInternalAsyncWorkflows(payment, homePos);
											}
										} catch (Exception e) {
											log.error("Mozambique WhatsApp post-policy workflow failed QuoteNo={}",
													payment.getQuoteNo(), e);
										}
									}
								}
							}
							}
							isPaymentdone=true;
						}else if("ACCEPTED".equals(paymentInfo.getPaymentStatus())) {
							isPaymentdone=true;
						}


					}
					j.addProperty("result",isPaymentdone?"COMPLETED":"FAIL");
					j.addProperty("message",responses!=null ?responses.toString():"");
					
					if (isPaymentdone) {
						HomePositionMaster homePos = homerepo.findByQuoteNo(payment.getQuoteNo());
						if (homePos != null && StringUtils.isNotBlank(homePos.getPolicyNo())) {
							j.addProperty("policyNo", homePos.getPolicyNo());
						}
					}
					
					System.out.println("Push whatsapp call for "+payment.getMerchantReference()+"--"+payment.getPaymentStatus());
					if(payment.getCompanyId()!="100049") {
//						if("ACCEPTED".equals(payment.getPaymentStatus())|| "FAILED".equals(payment.getPaymentStatus()))
//							postCall(j,payment);
					}
				}
					
			 	

				return j;
			}else {
				JsonObject j=new JsonObject();
				j.addProperty("result","FAIL");
				j.addProperty("message","No Data found");
//				postCall(j,);
				return j;

			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	private JsonObject smartpayOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		JsonObject finalResponse = new JsonObject();
		
		if("Success".equalsIgnoreCase(payment.getAuthResponse())) {
			payment.setPaymentStatus("ACCEPTED");
			finalResponse.addProperty("message", payment.getAuthResponse());
		}else if("PAYMENT MISMATCH".equalsIgnoreCase(payment.getAuthResponse())) {
			payment.setPaymentStatus("FAILED");
			finalResponse.addProperty("message", payment.getAuthResponse());
		}else if("PAYMENT_MISMATCH".equalsIgnoreCase(payment.getAuthResponse())) {
			payment.setPaymentStatus("FAILED");
			finalResponse.addProperty("message", payment.getAuthResponse());
		}else if("FAILED".equalsIgnoreCase(payment.getAuthResponse())) {
			payment.setPaymentStatus("FAILED");
			finalResponse.addProperty("message", payment.getAuthResponse());
		}
		return finalResponse;
	}
	private JsonObject tinggOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		try {
			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;					
			String checkstatusLink=null;
			String serviceCode=null;
			String acklowlgeEndUrl=null;
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				checkstatusLink=vendor.getCheckStatusUrl();	
				serviceCode=vendor.getVendorCode();
				acklowlgeEndUrl=vendor.getUnsignedFields();
			}
			// initalize a new Client instace with values of the base url, api key and api secret
		/*	ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
			JsonObject orderStatusDict = new JsonObject();
			orderStatusDict.addProperty("order_id",payment.getMerchantReference());
			//get order status
			JsonObject	responses= client.getFunc(checkstatusLink ,orderStatusDict);
			*/
			
			checkstatusLink=checkstatusLink.replaceAll("<service_code>", serviceCode);
			checkstatusLink=checkstatusLink.replaceAll("<merchant_transaction_id>", payment.getMerchantReference());
			Map<String,Object> tokenReq = new HashMap<>();
			tokenReq.put("client_id", clientId);
			tokenReq.put("client_secret", clientSecret);
			tokenReq.put("grant_type", "client_credentials");
			
			ResponseEntity<String> response = null;
			ObjectMapper objectMapper = new ObjectMapper();
            String reqString = objectMapper.writeValueAsString(tokenReq);
            RestTemplate restTemp = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			header.add("apiKey", apiKey);
			HttpEntity<?> requestent = new HttpEntity<>(reqString, header);
			response = restTemp.exchange(tokenUrl, HttpMethod.POST, requestent, String.class);
			Map<String,Object> resp = null;
			if(response.getBody() != null && !response.getBody().isEmpty()) {
				resp = objectMapper.readValue(response.getBody(), Map.class);
			}
			String bearerToken = resp.get("access_token") == null? null : resp.get("access_token").toString();
			JsonObject finalResponse = new JsonObject();
			if(StringUtils.isNotBlank(bearerToken)) {
				ResponseEntity<String> response2 = null;
			//	String checkoutReq = objectMapper.writeValueAsString(orderDict);
				RestTemplate restTemp1 = new RestTemplate();
	            HttpHeaders header1 = new HttpHeaders();
				header1.setContentType(MediaType.APPLICATION_JSON);
				header1.add("apiKey", apiKey);
				header1.add("Authorization","Bearer "+ bearerToken);
				HttpEntity<?> requestent1 = new HttpEntity<>(header1);
				response2 = restTemp1.exchange(baseUrl+checkstatusLink, HttpMethod.GET, requestent1, String.class);
				JsonObject c = null;
				
				System.out.println("PAY ::"+payment.getMerchantReference()+" "+response2 );
				if(response2.getBody() != null && !response2.getBody().isEmpty()) {
					Map<String, Object> res =
					        objectMapper.readValue(response2.getBody(), Map.class);
					
					Map<String,Object> status = objectMapper.convertValue(res.get("status"), Map.class); 
					
					Map<String, Object> results =
					        objectMapper.convertValue(res.get("results"), Map.class);

					List<Map<String, Object>> payments = null;

					if (results != null && results.get("payments") != null) {
					    payments = objectMapper.convertValue(
					            results.get("payments"),
					            new com.fasterxml.jackson.core.type.TypeReference<List<Map<String,Object>>>() {}
					    );
					}
					
					String statusCode = status.get("status_code") == null ? null : status.get("status_code").toString();
					String statusDesc = status.get("status_description") == null ? null : status.get("status_description").toString();
					finalResponse.addProperty("message", statusDesc);
					if("200".contentEquals(statusCode)) {
						payment.setPaymentStatus("ACCEPTED");
						payment.setAuthTransRefNo(payments.get(0).get("payment_option_transaction_id").toString());
						payment.setChannel(payments.get(0).get("checkout_request_id").toString());
						payment.setReference(payments.get(0).get("gateway_transaction_id").toString());
						payment.setMsisdn(payments.get(0).get("msisdn").toString());
						payment.setAuthAmount(payments.get(0).get("amount_paid").toString());
						
                        System.out.println("ACKNOWLEGE CALL FOR  :: ==> MERCHANTNO :: "+payment.getMerchantReference()+", ==> QUOTENO :: "+payment.getQuoteNo() );
						
						Map<String,Object> acknowlegeReq = new HashMap<>();
						acknowlegeReq.put("acknowledgement_amount", payments.get(0).get("amount_paid").toString());
						acknowlegeReq.put("acknowledgement_type", "Full");
						acknowlegeReq.put("acknowledgement_narration", "Check acknowledgement");
						acknowlegeReq.put("acknowledgment_reference", "ACK-"+payment.getMerchantReference());
						acknowlegeReq.put("merchant_transaction_id", payment.getMerchantReference());
						acknowlegeReq.put("service_code", serviceCode);
						acknowlegeReq.put("status_code", "183");
						acknowlegeReq.put("currency_code", payment.getCurrencyId());
						
						ResponseEntity<String> response3 = null;
						String acktReq = objectMapper.writeValueAsString(acknowlegeReq);
						
						System.out.println("ACKNOWLEGE REQUEST  :: ==>"+acktReq  );
							RestTemplate restTemp2 = new RestTemplate();
				            HttpHeaders header2 = new HttpHeaders();
							header2.setContentType(MediaType.APPLICATION_JSON);
							header2.add("apiKey", apiKey);
							header2.add("Authorization","Bearer "+ bearerToken);
							//HttpEntity<?> requestent2 = new HttpEntity<>(header2);
							HttpEntity<?> requestent2 = new HttpEntity<>(acktReq, header2);
							response3 = restTemp2.exchange(baseUrl+acklowlgeEndUrl, HttpMethod.POST, requestent2, String.class);
							
							
							System.out.println("ACKNOWLEGE RESPONCE ::"+payment.getMerchantReference()+" "+response3 );
					}else if ("500".contentEquals(statusCode)) {
						payment.setPaymentStatus("FAILED");
					}else {
						payment.setPaymentStatus("PENDING");
					}
					
					payment.setAuthResponse(statusDesc);
					payment.setResponseMessage(statusDesc);
					payment.setResponseTime(new Date());
					paymentDetailRepo.save(payment);
				}
				
			/*	if("SUCCESS".equalsIgnoreCase(response2.get("result").getAsString()) ) {
					JsonArray array = response2.get("data").getAsJsonArray();
					JsonObject response = array.get(0).getAsJsonObject();

					if("COMPLETED".equals(response.get("payment_status").getAsString())) {
						payment.setPaymentStatus("ACCEPTED");
						payment.setAuthTransRefNo(response.get("transid").getAsString());
						payment.setChannel(response.get("channel").getAsString());
						payment.setReference(response.get("reference").getAsString());
						payment.setMsisdn(response.get("msisdn").getAsString());
						///isPaymentdone=true;
					}else if("PENDING".equals(response.get("payment_status").getAsString()))
						payment.setPaymentStatus("PENDING");
					else if("INPROGRESS".equals(response.get("payment_status").getAsString()))
						payment.setPaymentStatus("PENDING");
					else
						payment.setPaymentStatus("FAILED");

					payment.setAuthResponse(response.get("payment_status").getAsString());
					payment.setResponseMessage(responses.get("message").getAsString());
					payment.setResponseTime(new Date());
					payment.setAuthAmount(response.get("amount").getAsString());
					paymentDetailRepo.save(payment);
				} */
			}
			
			
			  
			
			return finalResponse;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Transactional
	public void callRSTAIntegeration(String quoteNo) {
		Gson gson =new Gson();
		String responseCode="";
		StringBuffer responseAsString = new StringBuffer();
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		List<Map<String,Object>> request_list = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<MotorDataDetails> mdd = cq.from(MotorDataDetails.class);
			Root<HomePositionMaster> hpm = cq.from(HomePositionMaster.class);
			
			cq.multiselect(mdd.get("registrationNumber").alias("registrationNumber"),mdd.get("chassisNumber").alias("chassisNumber"),
					hpm.get("policyNo").alias("policyNo"),hpm.get("effectiveDate").alias("effectiveDate"),hpm.get("expiryDate").alias("expiryDate"),
					cb.selectCase().when(cb.in(mdd.get("policyType")).value(Arrays.asList("1","2")), "2").otherwise("1").alias("insuranceType"),
					cb.selectCase().when(cb.between(cb.literal(new Date()), hpm.get("inceptionDate"), hpm.get("expiryDate")), "1").otherwise("0").alias("status"),
					hpm.get("companyId").alias("companyId"))
			.where(cb.equal(mdd.get("quoteNo"), quoteNo),cb.equal(mdd.get("quoteNo"), hpm.get("quoteNo")));
			
			TypedQuery<Tuple> query = em.createQuery(cq);
			
			List<Tuple> resultList = query.getResultList();
			
			resultList.forEach(k -> {
				Map<String,Object> request = new HashMap<String,Object>();
				request.put("insuranceType", k.get("insuranceType")==null?"":k.get("insuranceType").toString());
				request.put("status", k.get("status")==null?"":k.get("status").toString());
				request.put("registrationMark", k.get("registrationNumber")==null?"":k.get("registrationNumber").toString());
				request.put("dateFrom", k.get("effectiveDate")==null?"":sdf.format(k.get("effectiveDate")));
				request.put("dateTo",k.get("expiryDate")==null?"":sdf.format(k.get("expiryDate")));
				request.put("insurancePolicyNo", k.get("policyNo")==null?"":k.get("policyNo").toString());
				request.put("chassisNumber", k.get("chassisNumber")==null?"":k.get("chassisNumber").toString());
				request_list.add(request);
			});
			
			log.info("Policy push request :: "+gson.toJson(request_list));
			insertRSTA(quoteNo,resultList.get(0).get("policyNo").toString(),gson.toJson(request_list));
			List<ListItemValue> rstadetails = itemValueRepo.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeDesc("RSTA_PUSH", "Y", resultList.get(0).get("companyId").toString());
			String url = rstadetails.stream().filter(f -> f.getItemValue().equalsIgnoreCase("API_URL")).map(m -> m.getParam1()).findFirst().get();
			String authorization = rstadetails.stream().filter(f -> f.getItemValue().equalsIgnoreCase("API_PASSWORD")).map(m -> m.getParam1()).findFirst().get();
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url); 
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accept", "*/*");
			httpPost.setHeader("Authorization", authorization);
			StringEntity entity = new StringEntity(gson.toJson(request_list).replaceAll("\"\"", "null"),"UTF-8");
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpclient.execute(httpPost); 
			if(response.getStatusLine().getStatusCode()<=400 || response.getStatusLine().getStatusCode()==403) {
				BufferedReader rd1 = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
				String line = "";
				while((line = rd1.readLine()) != null) {
					responseAsString.append(line);
				}
				log.info("Policy push response :: "+gson.toJson(responseAsString));
			}
			responseCode=String.valueOf(response.getStatusLine().getStatusCode());
		}catch (Exception e) {
			e.printStackTrace();
			responseAsString.append(e.getLocalizedMessage());
		}
		updateRSTAResponse(gson.toJson(responseAsString),responseCode,quoteNo);
	}
	
	@Transactional
	private void updateRSTAResponse(String responseJson, String responseCode, String quoteNo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<RSTAPushDetails> cq = cb.createCriteriaUpdate(RSTAPushDetails.class);
			Root<RSTAPushDetails> rpd = cq.from(RSTAPushDetails.class);
			
			cq.set(rpd.get("rstaResponse"), responseJson)
				.set(rpd.get("rstaResponseCode"), responseCode)
				.set(rpd.get("responseTime"), Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.where(cb.equal(rpd.get("quoteNo"), quoteNo));
			em.createQuery(cq).executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insertRSTA(String quoteNo, String policyNo, String requestJson) {
		try {
			RSTAPushDetails m = RSTAPushDetails.builder()
				.sno(RSTAMaxSno())
				.quoteNo(quoteNo)
				.rstaRequest(requestJson)
				.requestTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.entryDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.build();
			rstaPushDetailsRepo.save(m);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private BigDecimal RSTAMaxSno() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);
			Root<RSTAPushDetails> lpRoot = cq.from(RSTAPushDetails.class);
			cq.select(cb.coalesce(cb.sum(cb.max(lpRoot.get("sno")),BigDecimal.ONE), BigDecimal.ONE));
			BigDecimal value = em.createQuery(cq).getSingleResult();
			return value;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	private JsonObject mpesaOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		return mpesaPaymentService.orderStatus(payment, vendor);
	}

//	private JsonObject mpesaOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
//		try { 
//			MPesaIntegration mpesaOrderStatus=new MPesaIntegration();
//			APIResponse jsonObject = mpesaOrderStatus.mpesaOrderStatus(payment, vendor);
//			
//				if("INS-0".equalsIgnoreCase(jsonObject.getParameter("output_ResponseCode"))) {
//					payment.setPaymentStatus("ACCEPTED");
//					payment.setAuthTransRefNo(jsonObject.getParameter("output_TransactionID"));
//					payment.setChannel(jsonObject.getParameter("output_ConversationID"));
//					
//					payment.setMsisdn(jsonObject.getParameter("output_ThirdPartyReference"));
//					///isPaymentdone=true;
//				}else if("INS-0".equalsIgnoreCase(jsonObject.getParameter("output_ResponseCode")) 
//						&& ("Cancelled".equals(jsonObject.getParameter("output_ResponseTransactionStatus"))
//								|| "Expired".equals(jsonObject.getParameter("output_ResponseTransactionStatus")) ))
//					payment.setPaymentStatus("FAILED");
//				else 
//					payment.setPaymentStatus("PENDING");
//				
//
//				payment.setAuthResponse(jsonObject.getParameter("output_ResponseCode"));
//				payment.setResponseMessage(jsonObject.getParameter("output_ResponseDesc"));
//				payment.setResponseTime(new Date());
//				paymentDetailRepo.save(payment);
//				JsonObject resp=new JsonObject();
//				for(Map.Entry<String, String> entry: jsonObject.getParameters().entrySet()){					
//					resp.addProperty(entry.getKey(), jsonObject.getParameter(entry.getKey()));
//				}
//			return resp;
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	private JsonObject peachOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		try (CloseableHttpClient client = HttpClients.createDefault()) {  
			Map<String, String> params = new HashMap<>();
			params.put("authentication.entityId", vendor.getApiKey());
			params.put("merchantTransactionId", payment.getMerchantReference());
			String signaturestatus = peachGenerateSignature(params, vendor.getApiSecretKey());
			params.put("signature", signaturestatus);
			System.out.println("status signaturestatus->"+signaturestatus); 
			String param = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
					.collect(Collectors.joining("&"));
			StringBuilder urlString = new StringBuilder(vendor.getCheckStatusUrl().concat("?"+param));

			HttpGet httpGet = new HttpGet(urlString.toString());
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("accept", "application/json");

			try (CloseableHttpResponse response = client.execute(httpGet)) {
				org.apache.http.HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity);
				System.out.println("Response: " + responseString);
				JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
				if( responseJson!=null ) {
					List<String> successCodes=new ArrayList<String>();
					successCodes.add("000.000.000");
					successCodes.add("000.000.100");
					successCodes.add("000.100.110");
					successCodes.add("000.100.111");
					successCodes.add("000.100.112");
					List<String> pendingCodes=new ArrayList<String>();
					pendingCodes.add("000.200.000");
					pendingCodes.add("000.200.001");
					pendingCodes.add("000.200.100");
					pendingCodes.add("000.200.101");
					pendingCodes.add("000.200.102");
					pendingCodes.add("000.200.103");
					pendingCodes.add("000.200.200");
					pendingCodes.add("000.200.201");
					pendingCodes.add("000.200.999");
					
					if(responseJson.get("result.code") !=null && successCodes.contains(responseJson.get("result.code").getAsString()) ) {
						JsonObject redirect_post_data = responseJson;//.get("redirect_post_data").getAsJsonObject(); 

						if(successCodes.contains(redirect_post_data.get("result.code").getAsString())) {
							String amountStr=redirect_post_data.get("amount").getAsString();
							BigDecimal OurPremium=BigDecimal.ZERO;
							List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
							if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId())) {
								OurPremium = payment.getPremiumLc();
							}else {
								OurPremium = payment.getPremiumFc();
							}

							if(OurPremium.setScale(0, RoundingMode.UP).compareTo(new BigDecimal(amountStr))>=0) {
								payment.setPaymentStatus("ACCEPTED");
								payment.setAuthTransRefNo(redirect_post_data.get("recon.rrn").getAsString());
								payment.setChannel(redirect_post_data.get("recon.authCode").getAsString());
								payment.setReference(redirect_post_data.get("recon.rrn").getAsString());
								payment.setMsisdn(redirect_post_data.get("recon.stan").getAsString());
								payment.setAccountNumber(redirect_post_data.get("card.last4Digits").getAsString());
								payment.setAuthAmount(redirect_post_data.get("amount").getAsString());
								payment.setAuthResponse(redirect_post_data.get("result.code").getAsString());
								payment.setAuthTime(redirect_post_data.get("timestamp").getAsString());
								payment.setResponseTime(new Date());
								payment.setResponseMessage(redirect_post_data.get("result.description").getAsString());
							}else {
								payment.setPaymentStatus("FAILED");
								payment.setAuthResponse(redirect_post_data.get("result.code") !=null?redirect_post_data.get("result.code").getAsString():"");
								payment.setResponseMessage("Premium Amount is Mismatch ,Customer Paid Only "+amountStr);
								payment.setResponseTime(new Date());
								payment.setAuthAmount(redirect_post_data.get("amount")!=null? redirect_post_data.get("amount").getAsString():"0");
							}
						}
					}else if(responseJson.get("result.code") !=null && pendingCodes.contains(responseJson.get("result.code").getAsString()))
						payment.setPaymentStatus("PENDING");
					else /*if(responseJson.get("status") !=null && ( "cancelled".equals(responseJson.get("status").getAsString()) 
							|| "uncertain".equals(responseJson.get("status").getAsString())  
							))*/							 
						payment.setPaymentStatus("FAILED");

					payment.setAuthResponse(responseJson.get("result.code") !=null?responseJson.get("result.code").getAsString():"");
					payment.setResponseMessage(responseJson.get("result.description")!=null?responseJson.get("result.description").getAsString():"");
					payment.setResponseTime(new Date());
					payment.setAuthAmount(responseJson.get("amount")!=null? responseJson.get("amount").getAsString():"0");

					paymentDetailRepo.save(payment);


				}
				return responseJson;
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	private JsonObject pesapalOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		CloseableHttpClient httpClient = null;
		String token="",notificationId="";
		
		try {
			JsonObject request=new JsonObject();
			request.addProperty("consumer_key",vendor.getApiKey().toString());
			request.addProperty("consumer_secret", vendor.getApiSecretKey().toString());
			
			httpClient= HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost(vendor.getApiBaseUrl());
			postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			StringEntity params = new StringEntity(request.toString());
			postRequest.setEntity(params);
            HttpResponse hresp  = httpClient.execute(postRequest);

            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
            String apiOutput = EntityUtils.toString(httpEntity);
            System.out.println("output"+ apiOutput);
            JsonObject tokResponse = new Gson().fromJson(apiOutput, JsonObject.class);
            token=tokResponse.get("token").getAsString();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		try {
			httpClient= HttpClientBuilder.create().build();
			
			String url=vendor.getCheckStatusUrl()+payment.getResSignature();
			
			HttpGet request = new HttpGet(url);
            System.out.println(url);
	        request.addHeader("Authorization", "Bearer "+token);
	        request.setHeader("Content-Type","application/json");
	        request.setHeader("Accept","application/json");
	            HttpResponse hresp  = httpClient.execute(request);

	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
	            String apiOutput = EntityUtils.toString(httpEntity);
	            System.out.println("output"+ apiOutput.toString());

	            JsonObject fromJson = new Gson().fromJson(apiOutput, JsonObject.class);
	            
	            if( fromJson!=null ) {
					
	            	
					if(fromJson.get("status_code") !=null && "1".equals(fromJson.get("status_code").getAsString()) ) {
						String amountStr=fromJson.get("amount").getAsString();
						BigDecimal OurPremium=BigDecimal.ZERO;
						List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
						List<PaymentInfo> product= paymentinforepo.findByQuoteNo(payment.getQuoteNo());
						String pro=product.get(0).getProductId().toString();
						
						if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId())) {
							OurPremium = payment.getPremiumLc();
						}else {
							OurPremium = payment.getPremiumFc();
						}
						if("4".equalsIgnoreCase(pro)) {
							OurPremium = payment.getPremiumLc();
						}
						BigDecimal paidAmount = new BigDecimal(amountStr);
						
						BigDecimal minAcceptable = OurPremium.subtract(BigDecimal.ONE);
						BigDecimal maxAcceptable = OurPremium.add(BigDecimal.ONE);
							
						if (paidAmount.compareTo(minAcceptable) >= 0 && paidAmount.compareTo(maxAcceptable) <= 0) {

							payment.setPaymentStatus("ACCEPTED");
							payment.setAuthTransRefNo(fromJson.get("confirmation_code").getAsString());
							payment.setChannel(fromJson.get("confirmation_code").getAsString());
							payment.setReference(fromJson.get("confirmation_code").getAsString());
							payment.setMsisdn(fromJson.get("confirmation_code").getAsString());
							payment.setAccountNumber(fromJson.get("payment_account").getAsString());
							payment.setAuthAmount(fromJson.get("amount").getAsString());
							payment.setAuthResponse(fromJson.get("payment_status_description").getAsString());
							payment.setAuthTime(fromJson.get("created_date").getAsString());
							payment.setResponseTime(new Date());
							payment.setResponseMessage(fromJson.get("description").getAsString());

						}else {
							payment.setPaymentStatus("FAILED");
							payment.setAuthResponse(fromJson.get("payment_status_description") !=null?fromJson.get("payment_status_description").getAsString():"");
							payment.setResponseMessage("Premium Amount is Mismatch ,Customer Paid Only "+amountStr);
							payment.setResponseTime(new Date());
							payment.setAuthAmount(fromJson.get("amount")!=null? fromJson.get("amount").getAsString():"0");
						}
						///isPaymentdone=true;
					}else if(fromJson.get("status_code") !=null &&  "0".equals(fromJson.get("status_code").getAsString()))
						payment.setPaymentStatus("PENDING");
					else if(fromJson.get("status_code") !=null &&  "3".equals(fromJson.get("status_code").getAsString()))
						payment.setPaymentStatus("PENDING");
					else
						payment.setPaymentStatus("FAILED");

					payment.setAuthResponse(fromJson.get("payment_status_description") !=null?fromJson.get("payment_status_description").getAsString():"");
					payment.setResponseMessage(fromJson.get("description")!=null?fromJson.get("description").getAsString():"");
					payment.setResponseTime(new Date());
					payment.setAuthAmount(fromJson.get("amount")!=null? fromJson.get("amount").getAsString():"0");
					
					paymentDetailRepo.save(payment);
				}
		
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
        	if(httpClient!=null)
				try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} 
	return null;
		
	}
	
//	private JsonObject lipilaOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
//		  CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//		try {
//			
//			String url=vendor.getCheckStatusUrl()+payment.getMerchantReference();
//			
//			HttpGet request = new HttpGet(url);
//            System.out.println(url);
//	        request.addHeader("Authorization", "Bearer "+vendor.getApiSecretKey());
//	        request.setHeader("Content-Type","application/json");
//
//	            HttpResponse hresp  = httpClient.execute(request);
//
//	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
//	            String apiOutput = EntityUtils.toString(httpEntity);
//	            System.out.println("output"+ apiOutput.toString());
//
//	            JsonObject fromJson = new Gson().fromJson(apiOutput, JsonObject.class);
//	            
//	            if( fromJson!=null ) {
//					
//					if(fromJson.get("status") !=null && "Successful".equals(fromJson.get("status").getAsString())) {
//						payment.setPaymentStatus("ACCEPTED");
//						payment.setAuthTransRefNo(fromJson.get("transactionId").getAsString());
//						payment.setChannel(fromJson.get("transactionId").getAsString());
//						payment.setReference(fromJson.get("transactionId").getAsString());
//						payment.setMsisdn(fromJson.get("externalId").getAsString());
//						
//						///isPaymentdone=true;
//					}else if(fromJson.get("status") !=null &&  "Pending".equals(fromJson.get("status").getAsString()))
//						payment.setPaymentStatus("PENDING");
//					else if(fromJson.get("status") !=null &&  "INPROGRESS".equals(fromJson.get("status").getAsString()))
//						payment.setPaymentStatus("PENDING");
//					else
//						payment.setPaymentStatus("FAILED");
//
//					payment.setAuthResponse(fromJson.get("status") !=null?fromJson.get("status").getAsString():"");
//					payment.setResponseMessage(fromJson.get("message")!=null?fromJson.get("message").getAsString():"");
//					payment.setResponseTime(new Date());
//					payment.setAuthAmount(fromJson.get("amount")!=null? fromJson.get("amount").getAsString():"0");
//					paymentDetailRepo.save(payment);
//				}
//		
//		}catch(Exception e) {
//			e.printStackTrace();
//		}finally {
//        	if(httpClient!=null)
//				try {
//					httpClient.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		} 
//	return null;
//	}
	
	/**
	 * Check Lipila transaction status using official check-status endpoint
	 * Lipila Docs: GET /api/v1/collections/check-status?referenceId={ref}
	 */
	
	private JsonObject lipilaOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
	    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	    try {

//	    	String baseUrl = vendor.getApiBaseUrl();  // e.g., https://api.lipila.dev
//	    	String baseUrl = "https://api.lipila.dev";
	    	String baseUrl = "https://blz.lipila.io";
	        String checkUrl = baseUrl + "/api/v1/collections/check-status?referenceId=" + payment.getMerchantReference();

	        HttpGet request = new HttpGet(checkUrl);
	        request.setHeader("accept", "application/json");
//	        request.setHeader("x-api-key", vendor.getApiSecretKey());  
//	        request.setHeader("x-api-key", "lsk_019d9a8f-4bb6-7dc6-90c4-edac2baeae4f");  
	        request.setHeader("x-api-key", "lsk_019dd872-90de-7eb8-b488-bfde9d0cd057");  // NOT Bearer token

	        log.info("Lipila Status Check URL: {}", checkUrl);

	        HttpResponse hresp = httpClient.execute(request);
	        int statusCode = hresp.getStatusLine().getStatusCode();
	        org.apache.http.HttpEntity httpEntity = hresp.getEntity();
	        String apiOutput = EntityUtils.toString(httpEntity);

	        log.info("Lipila Status Response Status: {}", statusCode);
	        log.info("Lipila Status Response Body: {}", apiOutput);

	        if (statusCode != 200 || StringUtils.isBlank(apiOutput)) {
	            log.warn("Lipila status check returned HTTP {} or empty response", statusCode);
	            return null;
	        }

	        JsonObject fromJson = new Gson().fromJson(apiOutput, JsonObject.class);
	        if (fromJson == null) {
	            return null;
	        }

	        String lipilaStatus = getAsStringSafe(fromJson, "status", "");
	        log.info("Lipila Status Check - Received status: {}", lipilaStatus);

	        boolean isSuccess = "Successful".equalsIgnoreCase(lipilaStatus) || "Success".equalsIgnoreCase(lipilaStatus)
	                || "Completed".equalsIgnoreCase(lipilaStatus);
	        boolean isPending = "Pending".equalsIgnoreCase(lipilaStatus) || "INPROGRESS".equalsIgnoreCase(lipilaStatus);

	        if (isSuccess) {
	            payment.setPaymentStatus("ACCEPTED");

	            if (fromJson.has("identifier")) {
	                payment.setAuthTransRefNo(getAsStringSafe(fromJson, "identifier", null));
	                payment.setChannel(getAsStringSafe(fromJson, "identifier", null));
	            }
	            if (fromJson.has("referenceId")) {
	                payment.setReference(getAsStringSafe(fromJson, "referenceId", null));
	            }
	            if (fromJson.has("externalId")) {
	                payment.setMsisdn(getAsStringSafe(fromJson, "externalId", null));
	            }

	            payment.setAuthResponse(lipilaStatus);
	            payment.setResponseMessage(getAsStringSafe(fromJson, "message", "Payment successful"));

	        } else if ("ACCEPTED".equals(payment.getPaymentStatus())) {
	            // This method is also called for enrichment right after the Lipila webhook has already
	            // confirmed the payment. check-status can lag behind the webhook and still report
	            // Pending/Failed at that moment, so a payment already ACCEPTED is never downgraded here.
	            log.warn("Lipila check-status returned '{}' for QuoteNo: {} but payment is already ACCEPTED - keeping ACCEPTED",
	                    lipilaStatus, payment.getQuoteNo());

	        } else if (isPending) {
	            payment.setPaymentStatus("PENDING");
	            payment.setAuthResponse(lipilaStatus);
	            payment.setResponseMessage(getAsStringSafe(fromJson, "message", "Transaction pending"));

	        } else if ("Failed".equalsIgnoreCase(lipilaStatus)) {
	            payment.setPaymentStatus("FAILED");
	            payment.setAuthResponse(lipilaStatus);
	            payment.setResponseMessage(getAsStringSafe(fromJson, "message", "Payment failed"));

	        } else {
	            payment.setPaymentStatus("FAILED");
	            payment.setAuthResponse(lipilaStatus);
	            payment.setResponseMessage("Unknown status: " + lipilaStatus);
	        }

	        payment.setResponseTime(new Date());
	        payment.setAuthAmount(getAsStringSafe(fromJson, "amount", "0"));
	        
	        // Store payment type if available
	        if (fromJson.has("paymentType")) {
	            payment.setChannel(getAsStringSafe(fromJson, "paymentType", null));
	        }
	        
	        paymentDetailRepo.save(payment);
	        
	        // Sync with PaymentInfo - an already ACCEPTED PaymentInfo is never overwritten, the policy
	        // has been issued against it (same rule as the webhook handler).
	        PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());
	        if (paymentInfo != null && !"ACCEPTED".equals(paymentInfo.getPaymentStatus())) {
	            paymentInfo.setPaymentStatus(payment.getPaymentStatus());
	            paymentInfo.setUpdatedDate(new Date());
	            paymentinforepo.save(paymentInfo);
	            log.info("PaymentInfo synced via lipilaOrderStatus to {} for QuoteNo: {}", payment.getPaymentStatus(), payment.getQuoteNo());
	        }
	        
	        return fromJson;

	    } catch (Exception e) {
	        log.error("Error checking Lipila order status for QuoteNo: {}", payment.getQuoteNo(), e);
	        e.printStackTrace();
	        return null;
	    } finally {
	        if (httpClient != null) {
	            try {
	                httpClient.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	
	private JsonObject selcomOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		try {
			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;					
			String checkstatusLink=null;
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				checkstatusLink=vendor.getCheckStatusUrl();					
			}
			// initalize a new Client instace with values of the base url, api key and api secret
			ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
			JsonObject orderStatusDict = new JsonObject();
			orderStatusDict.addProperty("order_id",payment.getMerchantReference());
			//get order status
			JsonObject	responses= client.getFunc(checkstatusLink ,orderStatusDict);
			
			
			System.out.println("PAY ::"+payment.getMerchantReference()+" "+responses );
			  
			if("SUCCESS".equalsIgnoreCase(responses.get("result").getAsString()) ) {
				JsonArray array = responses.get("data").getAsJsonArray();
				JsonObject response = array.get(0).getAsJsonObject();

				if("COMPLETED".equals(response.get("payment_status").getAsString())) {
					payment.setPaymentStatus("ACCEPTED");
					payment.setAuthTransRefNo(response.get("transid").getAsString());
					payment.setChannel(response.get("channel").getAsString());
					payment.setReference(response.get("reference").getAsString());
					payment.setMsisdn(response.get("msisdn").getAsString());
					///isPaymentdone=true;
				}else if("PENDING".equals(response.get("payment_status").getAsString()))
					payment.setPaymentStatus("PENDING");
				else if("INPROGRESS".equals(response.get("payment_status").getAsString()))
					payment.setPaymentStatus("PENDING");
				else
					payment.setPaymentStatus("FAILED");

				payment.setAuthResponse(response.get("payment_status").getAsString());
				payment.setResponseMessage(responses.get("message").getAsString());
				payment.setResponseTime(new Date());
				payment.setAuthAmount(response.get("amount").getAsString());
				paymentDetailRepo.save(payment);
			}
			return responses;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private void postCall(JsonObject j, PaymentDetail payment) {
		try {
			
			Map<String,Object> request=new HashMap<String, Object>();
			request.put("whatsapp_no",payment.getWhatsappNo() );
			request.put("message_type", "Text");
			request.put("whatsapp_code", payment.getWhatsappCode());
			request.put("payment_response", j.toString());
			
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			//headers.set("Authorization", "Basic dmlzaW9uOnZpc2lvbkAxMjMj");
			HttpEntity<Object> entityReq = new HttpEntity<>(request, headers);
			System.out.println(entityReq.getBody());
			 ResponseEntity<Object> response = restTemplate.postForEntity(whatsappUrl, entityReq, Object.class);
			System.out.println(response.getBody());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	@PersistenceContext
    private EntityManager em;

	private List<PaymentDetail> getPendingPaymentDetails(String quoteNo){
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
			Root<PaymentDetail> paymentDetailRoot = criteriaQuery.from(PaymentDetail.class);
			// SELECT clause
			criteriaQuery.multiselect(
			    paymentDetailRoot.get("QUOTE_NO"),
			    paymentDetailRoot.get("PAYMENT_ID"),
			    paymentDetailRoot.get("MERCHANT_REFERENCE"),
			    paymentDetailRoot.get("SHORTERN_URL"),
			    paymentDetailRoot.get("PREMIUM"),
			    paymentDetailRoot.get("CUSTOMER_NAME"),
			    paymentDetailRoot.get("CUSTOMER_EMAIL"),
			    paymentDetailRoot.get("REQ_BILL_TO_PHONE"),
			    paymentDetailRoot.get("REQ_BILL_TO_COMPANY_NAME"),
			    paymentDetailRoot.get("AUTH_TRANS_REF_NO"),
			    paymentDetailRoot.get("PAYMENT_STATUS")
			);

			// Define the subquery
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<PaymentDetail> subqueryRoot = subquery.from(PaymentDetail.class);
			Expression<Long> oneLiteral = criteriaBuilder.literal(1L);

			subquery.select(oneLiteral);
			subquery.where(
			    criteriaBuilder.equal(
			        criteriaBuilder.upper(subqueryRoot.get("PAYMENT_STATUS")),
			        criteriaBuilder.upper(criteriaBuilder.literal("ACCEPTED"))
			    )
			);

			// Main query WHERE clause
			Predicate mainWhereClause = criteriaBuilder.and(
			    criteriaBuilder.equal(paymentDetailRoot.get("QUOTE_NO"), "Q00222"),
			    criteriaBuilder.not(criteriaBuilder.exists(subquery)),
			    criteriaBuilder.lessThan(
			        criteriaBuilder.sum(
			            criteriaBuilder.function(
			                "interval",
			                Integer.class,
			                paymentDetailRoot.get("ENTRY_DATE"),
			                criteriaBuilder.parameter(Integer.class, "interval"),
			                criteriaBuilder.literal("SECOND")
			            ),
			            criteriaBuilder.literal(1)
			        ),
			        criteriaBuilder.currentTimestamp()
			    ),
			    criteriaBuilder.between(
			        criteriaBuilder.currentTimestamp(),
			        criteriaBuilder.function(
			            "interval",
			            java.sql.Timestamp.class,
			            paymentDetailRoot.get("ENTRY_DATE"),
			            criteriaBuilder.parameter(Integer.class, "displayTime"),
			            criteriaBuilder.literal("MINUTE")
			        ),
			        criteriaBuilder.currentTimestamp()
			    ),
			    criteriaBuilder.equal(
			        criteriaBuilder.upper(paymentDetailRoot.get("PAYMENT_STATUS")),
			        criteriaBuilder.upper(criteriaBuilder.literal("PENDING"))
			    )
			);

			criteriaQuery.where(mainWhereClause);
			criteriaQuery.orderBy(criteriaBuilder.desc(paymentDetailRoot.get("ENTRY_DATE")));

			// Execute the query
			TypedQuery<Object[]> typedQuery = em.createQuery(criteriaQuery);
			typedQuery.setParameter("interval", 1); // Set the interval parameter
			typedQuery.setParameter("displayTime", 1); // Set the displayTime parameter
			List<Object[]> result = typedQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}*/


	public JsonObject createOrderMinimal(PaymentDetail payment) {
		PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());
		String userType = "b2b";
		if (paymentInfo != null && paymentInfo.getSubUserType() != null
				&& paymentInfo.getSubUserType().equalsIgnoreCase("b2c")) {
			userType = "b2c";
		}
		int productId = paymentInfo != null && paymentInfo.getProductId() != null ? paymentInfo.getProductId() : 5;

		List<PaymentVendorMaster> paymentId = paymentVendorRepo
				.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(
						payment.getCompanyId(), "Y", "1", userType, productId);
		
		
		
		if (paymentId == null || paymentId.isEmpty()) {
			paymentId = paymentVendorRepo
					.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(
							payment.getCompanyId(), "Y", "1", userType, 99999);
		}
		if (paymentId == null || paymentId.isEmpty()) {
			log.info("No payment vendor configured for companyId={} userType={} productId={}",
					payment.getCompanyId(), userType, productId);
			return null;
		}
		
		
		PaymentVendorMaster vendor = paymentId.get(0);
		
		if((paymentInfo.getProductId().equals(5) && payment.getCompanyId().equals("100019") &&  payment.getPaymentType().equals("6")) ||
				(paymentInfo.getProductId().equals(125) && payment.getCompanyId().equals("100019"))) {
			vendor =getMtpVendor(payment.getCompanyId());	
		}
		
		if("lipila".equals(vendor.getVendorName())){
			return lipilaOrderMinimal(payment, vendor);
		}
//		else if("mpesa".equals(vendor.getVendorName())){
//			return mpesaOrderMinimal(payment,vendor);
//		}
		
		else if ("mpesa".equals(vendor.getVendorName())) {
			System.out.println("IN MPESA");
			return mPesaOrderMinimal(payment, vendor);
		}
		
		else if ("mtp".equals(vendor.getVendorName())) {
		    return mtp(vendor, payment, paymentInfo);
		}
		else {
			return selcomOrderMinimal(payment,vendor);
		}
			
	}
	
	public JsonObject order(PaymentDetail payment) {
		List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1");
		PaymentVendorMaster vendor = paymentId.get(0);
		
		if("lipila".equals(vendor.getVendorName())){
			return lipilaOrderMinimal(payment, vendor);
		}
//		else if("mpesa".equals(vendor.getVendorName())){
//			return mpesaOrderMinimal(payment,vendor);
//		}
		
		else if ("mpesa".equals(vendor.getVendorName())) {
			System.out.println("IN MPESA");
			return mPesaOrderMinimal(payment, vendor);
		}
		else {
			return selcomOrderMinimal(payment,vendor);
		}
			
	}
	
	private JsonObject mPesaOrderMinimal(PaymentDetail payment, PaymentVendorMaster vendor) {
		return mpesaPaymentService.payment(payment, vendor);
	}
	
//	private JsonObject mpesaOrderMinimal(PaymentDetail payment, PaymentVendorMaster vendor) {
//		try {
//			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
//			
//			MPesaIntegration mpesa=new MPesaIntegration();
//			APIResponse pushMobile = mpesa.pushMobile(payment,vendor,insInfo.get(0));
//			 JsonObject json=new JsonObject();
//			if(pushMobile != null) {	             
// 	            for(Map.Entry<String, String> entry: pushMobile.getParameters().entrySet()){	                
//	                json.addProperty(entry.getKey(), pushMobile.getParameter(entry.getKey()));
//	            }
// 	          payment.setReference(pushMobile.getParameter("output_TransactionID"));
// 	          paymentDetailRepo.save(payment);
// 	           return json;
// 	        }	
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	private JsonObject lipilaOrderMinimal(PaymentDetail payment, PaymentVendorMaster vendor) {
		try {

	        
	        String url = vendor.getApiBaseUrl();
	        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

	        try {
	        	JsonObject orderDict = new JsonObject();
				orderDict.addProperty("currency",payment.getCurrencyId());
				List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
				
				if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
					orderDict.addProperty("amount",  payment.getPremiumLc()); //payment.getPremiumLc()
				else
					orderDict.addProperty("amount",  payment.getPremiumFc()); //payment.getPremiumFc()
				
				orderDict.addProperty("accountNumber",payment.getReqBillToPhone());
				orderDict.addProperty("fullName",payment.getCustomerName());
				orderDict.addProperty("phoneNumber",payment.getReqBillToPhone());
				if(StringUtils.isNotBlank(payment.getReqBillToEmail()))
					orderDict.addProperty("email",payment.getReqBillToEmail());
				orderDict.addProperty("externalId",payment.getMerchantReference());
				orderDict.addProperty("narration",payment.getQuoteNo() +" Payment Request");
				
	            HttpPost request = new HttpPost(url);
	            StringEntity params = new StringEntity(orderDict.toString());

	            System.out.println(url);
	            System.out.println( orderDict.toString());
	            
	             request.addHeader("Authorization", "Bearer "+vendor.getApiSecretKey());
	             request.setHeader("Content-Type","application/json");

	            request.setEntity(params);
	            HttpResponse hresp  = httpClient.execute(request);

	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
	            String apiOutput = EntityUtils.toString(httpEntity);
	            System.out.println("output"+ apiOutput.toString());

	            return new Gson().fromJson(apiOutput, JsonObject.class);
	        } catch (Exception ex) {
	            JsonObject err = new JsonObject();
	            err.addProperty("error", ex.getMessage());
	            return err;
	        }finally {
	        	if(httpClient!=null)
					try {
						httpClient.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} 
	    
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private JsonObject selcomOrderCancel(PaymentDetail payment, PaymentVendorMaster vendor) {

		try {
			JsonObject response = null;
			boolean isPaymentdone = false;
			JsonObject j = new JsonObject();

			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;
			String checkstatusLink = null;
			if (vendor != null) {
				apiKey = vendor.getApiKey();
				apiSecret = vendor.getApiSecretKey();
				baseUrl = vendor.getApiBaseUrl();
				checkstatusLink = vendor.getCheckStatusUrl();
			}
			ApigwClient client = new ApigwClient(baseUrl, apiKey, apiSecret);
			String orderPath = "/v1/checkout/wallet-payment";
			JsonObject orderDict = new JsonObject();
			orderDict.addProperty("transid", "MOBI" + Instant.now().toEpochMilli());
			orderDict.addProperty("order_id", payment.getMerchantReference());
			orderDict.addProperty("msisdn", payment.getReqBillToPhone());
			JsonObject resp = client.postFunc(orderPath, orderDict);
			log.info("Mobile Payment Response:" + resp);
			System.out.println("Mobile Payment Response:" + resp);
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private JsonObject selcomOrderMinimal(PaymentDetail payment, PaymentVendorMaster vendor) {

		try {

					
			JsonObject response =null;
			boolean isPaymentdone=false;
			JsonObject j=new JsonObject();


			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;					
			String checkstatusLink=null;
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				checkstatusLink=vendor.getCheckStatusUrl();					
			}

			// initalize a new Client instace with values of the base url, api key and api secret
			ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
			// path relatiive to base url
			//String orderPath = "/v1/wallet/pushussd";//"/v1/checkout/create-order-minimal";
			String orderPath ="/v1/checkout/wallet-payment";
			// data
			JsonObject orderDict = new JsonObject();
			orderDict.addProperty("transid","MOBI"+Instant.now().toEpochMilli());			
			orderDict.addProperty("order_id",payment.getMerchantReference());
			//orderDict.addProperty("vendor","151662");//vendor.getVendorCode());
			orderDict.addProperty("msisdn",payment.getReqBillToPhone());
			/*orderDict.addProperty("vendor",vendor.getVendorCode());
			orderDict.addProperty("order_id",payment.getMerchantReference());
			orderDict.addProperty("buyer_email", payment.getReqBillToEmail());
			orderDict.addProperty("buyer_name", payment.getCustomerName());
			orderDict.addProperty("buyer_phone", payment.getReqBillToPhone());

			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				orderDict.addProperty("amount",  payment.getPremiumLc().toPlainString());
			else
				orderDict.addProperty("amount",  payment.getPremiumFc().toPlainString());


			orderDict.addProperty("currency",payment.getCurrencyId());
			orderDict.addProperty("buyer_remarks","None");
			orderDict.addProperty("merchant_remarks","None");
			orderDict.addProperty("no_of_items", 1 );
*/
			//post data
			JsonObject resp = client.postFunc(orderPath ,orderDict);
			log.info("Mobile Payment Response:"+resp);
			System.out.println("Mobile Payment Response:"+resp);
			return resp;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public JsonObject createOrderMinimal(String merchantRefernceNo) {
		try {
			PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(merchantRefernceNo,"PENDING");
			if(payment!=null) {
			 return createOrderMinimal(payment);				
			}else {
				log.info("No Records");
			} 
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	@Override
	public RedirectView handleCyberSourceRedirect(MultiValueMap<String, String> formData) {
	    Map<String, Object> jsObject = new HashMap<>(formData.toSingleValueMap());
	    
	    if (jsObject.get("req_reference_number") != null && 
	        StringUtils.isNotBlank(jsObject.get("req_reference_number").toString())) {
	        
	        String merchantRef = jsObject.get("req_reference_number").toString();
	        PaymentDetail payment = paymentDetailRepo.findByMerchantReference(merchantRef);
	        
	        if (payment != null) {
	            jsObject.put("order_id", merchantRef);
	            jsObject.put("payment_type", "cybersource");

	            PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(
	                payment.getQuoteNo(), payment.getPaymentId());

	            if (paymentInfo == null) {
	                return gracefulCybersourceFallback(
	                    "no PaymentInfo for QuoteNo=" + payment.getQuoteNo() + " PaymentId=" + payment.getPaymentId()
	                        + " (duplicate/late callback?)");
	            }

				String userType = paymentInfo.getSubUserType().equalsIgnoreCase("b2c") ? "b2c" : "b2b";
				// String userType = "b2b";
				List<PaymentVendorMaster> vendorList = paymentVendorRepo
						.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(
								payment.getCompanyId(), "Y", "1", userType, paymentInfo.getProductId());

				if (vendorList == null || vendorList.isEmpty()) {
					vendorList = paymentVendorRepo
							.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(
									payment.getCompanyId(), "Y", "1", userType, 99999);
				}

	            if (!vendorList.isEmpty()) {
	                PaymentVendorMaster vendor = vendorList.get(0);
	                return cybersourceOrderStatus(payment, vendor, jsObject);
	            }
	            return gracefulCybersourceFallback("no active vendor for CompanyId=" + payment.getCompanyId()
	                + " UserType=" + userType + " ProductId=" + paymentInfo.getProductId());
	        } else {
	            return gracefulCybersourceFallback(
	                "no PaymentDetail found for req_reference_number=" + merchantRef + " (likely a duplicate/replayed callback)");
	        }
	    } else {
	        return gracefulCybersourceFallback("callback missing req_reference_number: " + jsObject);
	    }
	}

	/**
	 * Kill switch: phoenix.whatsapp.cybersource.webhook.gracefulFallback.enabled (default true).
	 * When true (default), logs the reason and redirects to a blank page instead of crashing on a
	 * duplicate/malformed CyberSource callback. When false, rethrows loudly instead — useful only if
	 * you specifically want these edge cases to surface as 500s during active investigation.
	 */
	private RedirectView gracefulCybersourceFallback(String reason) {
	    log.warn("CyberSource webhook: {}", reason);
	    if (!cybersourceWebhookGracefulFallbackEnabled) {
	        throw new IllegalStateException("CyberSource webhook: " + reason);
	    }
	    return new RedirectView("about:blank");
	}

	private RedirectView cybersourceOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor, Map<String,Object> jsObject) {
	    log.info("Processing CyberSource Payment: {}", payment.getMerchantReference());
	    log.info("Vendor Details: {}", vendor.getVendorName());
	    log.info("Response Data: {}", jsObject);

	    try {
	    	
	        boolean valid = verifySignature(jsObject, vendor);
	        
	        if (!valid) {
	            payment.setPaymentStatus("FAILED");
	            payment.setAuthResponse("INVALID_SIGNATURE");
	            payment.setResponseMessage("Invalid signature verification");
	            payment.setResponseTime(new Date());
	            paymentDetailRepo.save(payment);
	            return new RedirectView(buildRedirectUrl("cancel", payment ,vendor.getCancelUrlLink() ));
	        }

	        String decision = (String) jsObject.get("decision");
	        String reasonCode = (String) jsObject.get("reason_code");
	        String transactionId = (String) jsObject.get("transaction_id");
	        String authAmount = (String) jsObject.get("auth_amount");
	        String requestId = (String) jsObject.get("req_reference_number");
	        
	        payment.setAuthTransRefNo(transactionId);
	        payment.setAuthResponse(reasonCode);
	        payment.setResponseTime(new Date());
	        if (authAmount != null) {
	            payment.setAuthAmount(authAmount);
	        }
	        
	        String redirectUrl = determineRedirectUrl(payment, decision, reasonCode,vendor);
	        
	        paymentDetailRepo.save(payment);
	        
	        if ("ACCEPTED".equals(payment.getPaymentStatus())) {
	            processSuccessfulPayment(payment);
	            triggerWhatsAppCybersourcePostPayment(payment);
	        }
	        
	        return new RedirectView(redirectUrl);
	        
	    } catch (Exception e) {
	        log.error("Error processing CyberSource payment status for {}", payment.getMerchantReference(), e);
	        payment.setPaymentStatus("FAILED");
	        payment.setAuthResponse("SYSTEM_ERROR");
	        payment.setResponseMessage("System error processing payment: " + e.getMessage());
	        payment.setResponseTime(new Date());
	        paymentDetailRepo.save(payment);
	        return new RedirectView(buildRedirectUrl("cancel", payment,vendor.getCancelUrlLink()));
	    }
	}

	private String buildRedirectUrl(String type, PaymentDetail payment, String baseUrl) {

		if (StringUtils.isBlank(baseUrl)) {
			return "about:blank";
		}
		String quoteNo = (payment != null && payment.getQuoteNo() != null) ? payment.getQuoteNo() : "";
		return baseUrl.replaceAll("<QuoteNo>", quoteNo);

		// String baseUrl = paymentRedirectBaseUrl;

//	    if (payment != null && payment.getQuoteNo() != null) {
//	        return baseUrl + "?QuoteNo=" + payment.getQuoteNo() + "&type=" + type;
//	    }
//	    return baseUrl + "?type=" + type;
	}

	private String determineRedirectUrl(PaymentDetail payment, String decision, String reasonCode,PaymentVendorMaster vendor) {
	    switch (reasonCode) {
	        case "100":
	            if ("ACCEPT".equalsIgnoreCase(decision)) {
	                payment.setPaymentStatus("ACCEPTED");
	                payment.setResponseMessage("Payment successful");
	                return buildRedirectUrl("success", payment,vendor.getReturnUrlLink());
	            } else {
	                payment.setPaymentStatus("FAILED");
	                payment.setResponseMessage("Transaction failed despite reason code 100");
	                return buildRedirectUrl("cancel", payment,vendor.getCancelUrlLink());
	            }
	                
	        case "101": case "102":
	            payment.setPaymentStatus("FAILED");
	            payment.setResponseMessage("Validation error - " + reasonCode);
	            return buildRedirectUrl("cancel",payment,vendor.getCancelUrlLink());
	                
	        case "150": case "151": case "152":
	            payment.setPaymentStatus("FAILED");
	            payment.setResponseMessage("System/timeout error - " + reasonCode);
	            return buildRedirectUrl("cancel", payment,vendor.getCancelUrlLink());
	                
	        case "234":
	            payment.setPaymentStatus("FAILED");
	            payment.setResponseMessage("Configuration error");
	            return buildRedirectUrl("cancel", payment,vendor.getCancelUrlLink());
	                
	        case "475":
	            payment.setPaymentStatus("PENDING");
	            payment.setResponseMessage("3D Secure authentication required");
	            return buildRedirectUrl("cancel", payment,vendor.getCancelUrlLink());
	                
	        case "476":
	            payment.setPaymentStatus("FAILED");
	            payment.setResponseMessage("3D Secure authentication failed");
	            return buildRedirectUrl("cancel",payment,vendor.getCancelUrlLink());
	                
	        default:
	            return handleDecisionBasedRedirect(payment, decision, reasonCode,vendor);
	    }
	}

	private String handleDecisionBasedRedirect(PaymentDetail payment, String decision, String reasonCode, PaymentVendorMaster vendor) {
	    if ("REVIEW".equalsIgnoreCase(decision)) {
	        payment.setPaymentStatus("PENDING");
	        payment.setResponseMessage("Payment under review");
	        return buildRedirectUrl("cancel",payment,vendor.getCancelUrlLink());
	    } else if ("DECLINE".equalsIgnoreCase(decision)) {
	        payment.setPaymentStatus("DECLINED");
	        payment.setResponseMessage("Payment declined");
	        return buildRedirectUrl("cancel", payment,vendor.getCancelUrlLink());
	    } else if ("ERROR".equalsIgnoreCase(decision)) {
	        payment.setPaymentStatus("FAILED");
	        payment.setResponseMessage("Payment error");
	        return buildRedirectUrl("cancel", payment,vendor.getCancelUrlLink());
	    } else {
	        payment.setPaymentStatus("FAILED");
	        payment.setResponseMessage("Unknown payment status");
	        return buildRedirectUrl("cancel", payment,vendor.getCancelUrlLink());
	    }
	}

	private void processSuccessfulPayment(PaymentDetail payment) {
	    try {
	        PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(
	            payment.getQuoteNo(), payment.getPaymentId());
	        
	        if (paymentInfo != null && !"ACCEPTED".equals(paymentInfo.getPaymentStatus())) {
	            paymentInfo.setPaymentStatus("ACCEPTED");
	            paymentInfo.setUpdatedDate(new Date());
	            paymentInfo.setMerchantReference(payment.getMerchantReference());
	            paymentinforepo.save(paymentInfo);

//	            if (paymentInfo.getProductId() != 3 && paymentInfo.getProductId() != 11) {
//	                processPolicyGeneration(payment, paymentInfo);
//	            }
	        }
	    } catch (Exception e) {
	        log.error("Error processing successful payment for {}", payment.getMerchantReference(), e);
	    }
	}

	/**
	 * WhatsApp B2C Cybersource only (WhatsappPolicy=Y). Web UI keeps separate flow.
	 * Webhook already set ACCEPTED — call cybersourceorderStatusminimal only (policy + bot).
	 * Do not call orderStatus() here: that method is quoteNo-based but routes non-lipila vendors
	 * to selcomOrderStatus, which is wrong for Cybersource and can corrupt payment state.
	 */
	@Async
	public void triggerWhatsAppCybersourcePostPayment(PaymentDetail payment) {
		if (payment == null) {
			return;
		}
		
		// Webhook callback object may occasionally be missing quoteNo depending on caller/state.
		// Always reload from DB so we have a reliable QuoteNo + PaymentId pair.
		PaymentDetail dbPayment = payment;
		try {
			if (StringUtils.isBlank(dbPayment.getQuoteNo())) {
				if (StringUtils.isNotBlank(dbPayment.getMerchantReference())) {
					dbPayment = paymentDetailRepo.findByMerchantReference(dbPayment.getMerchantReference());
				}
			}
		} catch (Exception e) {
			log.error("WhatsApp Cybersource post-payment: failed to reload payment from DB", e);
		}
		
		if (dbPayment == null || StringUtils.isBlank(dbPayment.getQuoteNo())) {
			log.error("WhatsApp Cybersource post-payment skipped — missing QuoteNo. merchantRef={} paymentId={}",
					payment.getMerchantReference(), payment.getPaymentId());
			return;
		}
		if (!"ACCEPTED".equalsIgnoreCase(dbPayment.getPaymentStatus())) {
			log.warn("WhatsApp Cybersource post-payment skipped — payment not ACCEPTED QuoteNo={} status={}",
					dbPayment.getQuoteNo(), dbPayment.getPaymentStatus());
			return;
		}
		try {
			HomePositionMaster home = homerepo.findByQuoteNo(dbPayment.getQuoteNo());
			if (home == null || !"Y".equalsIgnoreCase(home.getWhatsappPolicy())) {
				log.info("Cybersource webhook: skip post-payment (not WhatsApp B2C) QuoteNo={}",
						dbPayment.getQuoteNo());
				return;
			}

			String token = resolvePaymentFlowToken(dbPayment);
			log.info("WhatsApp Cybersource: internal orderStatusMinimal QuoteNo={} PaymentId={}",
					dbPayment.getQuoteNo(), dbPayment.getPaymentId());
			JsonObject minimalResult = cybersourceorderStatusminimal(dbPayment.getQuoteNo(), dbPayment.getPaymentId(),
					token);
			log.info("WhatsApp Cybersource orderStatusMinimal result: {}", minimalResult);
		} catch (Exception e) {
			log.error("WhatsApp Cybersource post-payment failed for QuoteNo={}", dbPayment.getQuoteNo(), e);
		}
	}

	private String resolvePaymentFlowToken(PaymentDetail payment) {
		try {
			LoginRequest mslogin = new LoginRequest();
			String loginId = StringUtils.defaultIfBlank(payment.getUpdatedBy(), "guest");
			if ("100047".equals(payment.getCompanyId())) {
				loginId = StringUtils.defaultIfBlank(payment.getUpdatedBy(), "guest_Botswana");
			}
			mslogin.setLoginId(loginId);
			mslogin.setPassword("Admin@03");
			mslogin.setReLoginKey("Y");
			CommonLoginRes checkUserLogin = authservice.checkUserLogin(mslogin, null);
			ClaimLoginResponse commonResponse = (ClaimLoginResponse) checkUserLogin.getCommonResponse();
			if (commonResponse != null && StringUtils.isNotBlank(commonResponse.getToken())) {
				return commonResponse.getToken();
			}
		} catch (Exception e) {
			log.error("Token acquisition failed for QuoteNo={}", payment.getQuoteNo(), e);
		}
		return "";
	}
	
	private void processPolicyGeneration(PaymentDetail payment, PaymentInfo paymentInfo) {
	    try {
	        LoginRequest mslogin = new LoginRequest();
	        mslogin.setLoginId("guest");
	        mslogin.setPassword("Admin@01");
	        mslogin.setReLoginKey("Y");
	        CommonLoginRes loginRes = authservice.checkUserLogin(mslogin, null);
	        ClaimLoginResponse commonResponse = (ClaimLoginResponse) loginRes.getCommonResponse();
	        
	        if (commonResponse == null) return;
	        
	        String token = commonResponse.getToken();
	        
	        TiraFrameReqCall tira = new TiraFrameReqCall();
	        tira.setQuoteNo(payment.getQuoteNo());
	        tiraService.callTiraIntegeration(tira, token);
	        
	       	        
	        PaymentDetailsSaveReq req = new PaymentDetailsSaveReq();
	        req.setQuoteNo(payment.getQuoteNo());
	        req.setCreatedBy(payment.getUpdatedBy());
	        req.setPaymentType(payment.getPaymentType());
	        
//	        HomePositionMaster home = homerepo.findByQuoteNo(payment.getQuoteNo());
			paymentService.generatePolicy(paymentInfo, req, payment, token);
	        
	    } catch (Exception e) {
	        log.error("Policy generation failed for quote {}", payment.getQuoteNo(), e);
	    }
	}

    private boolean verifySignature(Map<String, Object> jsObject, PaymentVendorMaster vendor) {
        try {
            String responseSignature = (String) jsObject.get("signature");
            String signedFields = (String) jsObject.get("signed_field_names");
            
            if (StringUtils.isBlank(responseSignature) || StringUtils.isBlank(signedFields)) {
                log.error("Missing signature or signed_field_names in response");
                return false;
            }

            String dataString = buildDataString(signedFields, jsObject);
            log.info("Data string for signature verification: {}", dataString);

            String sharedSecretKey = vendor.getApiSecretKey();
            if (StringUtils.isBlank(sharedSecretKey)) {
                log.error("Missing API secret key for signature verification");
                return false;
            }

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                sharedSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(dataString.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = Base64.getEncoder().encodeToString(rawHmac);

            log.info("Expected Signature: {}", responseSignature);
            log.info("Generated Signature: {}", generatedSignature);

            return generatedSignature.equals(responseSignature);

        } catch (Exception e) {
            log.error("Error during signature verification", e);
            return false;
        }
    }

    private String buildDataString(String signedFields, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        String[] fields = signedFields.split(",");
        
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim();
            if ("signature".equals(field)) continue;
            
            Object valueObj = params.get(field);
            String value = valueObj != null ? valueObj.toString() : "";
            builder.append(field).append("=").append(value);
            
            if (i < fields.length - 1) {
                builder.append(",");
            }
        }
        
        return builder.toString();
    }

	@Override
	public JsonObject cybersourceorderStatusminimal(String orderId, String paymentIds, String token) {
		List<PaymentDetail> paymentList = paymentDetailRepo.findByQuoteNoAndPaymentId(orderId, paymentIds);
		Optional<PaymentDetail> firstAcceptedPayment = paymentList.stream()
				.filter(a -> "ACCEPTED".equalsIgnoreCase(a.getPaymentStatus())).findFirst();

		if (firstAcceptedPayment.isPresent()) {
			PaymentDetail payment = firstAcceptedPayment.get();

			if (payment != null && payment.getPaymentStatus().equalsIgnoreCase("ACCEPTED")) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(),
						payment.getPaymentId());
				PaymentDetailsSaveReq req = new PaymentDetailsSaveReq();
				req.setQuoteNo(payment.getQuoteNo());
				req.setCreatedBy(payment.getUpdatedBy());
				req.setPaymentType(payment.getPaymentType());
				paymentService.generatePolicy(paymentInfo, req, payment, token);

				HomePositionMaster homePosition = homerepo.findByQuoteNo(payment.getQuoteNo());
				String policyNo = payment.getQuoteNo();
				if (homePosition != null && StringUtils.isNotBlank(homePosition.getPolicyNo())) {
					policyNo = homePosition.getPolicyNo();
					if ("Y".equalsIgnoreCase(homePosition.getWhatsappPolicy())) {
						callAiLifeBotApi(payment, policyNo);
						triggerInternalAsyncWorkflows(payment, homePosition);
					}
				}

				JsonObject response = new JsonObject();
				response.addProperty("result", "Success");
				response.addProperty("message", "Policy Id Generated");
				if (homePosition != null && StringUtils.isNotBlank(homePosition.getPolicyNo())) {
					response.addProperty("policyNo", homePosition.getPolicyNo());
				}
				return response;
			}
		}

		JsonObject failureResponse = new JsonObject();
		failureResponse.addProperty("result", "Failure");
		failureResponse.addProperty("message", "Payment not accepted or not found");
		return failureResponse;
	}

	@Override
	public JsonObject orderCancel(String merchantRefernceNo) {
		try {
			PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(merchantRefernceNo,
					"PENDING");
			if (payment != null) {
				List<PaymentVendorMaster> paymentId = paymentVendorRepo
						.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payment.getCompanyId(), "Y", "5");
				PaymentVendorMaster vendor = paymentId.get(0);
				return selcomOrderCancel(payment, vendor);
			} else {
				log.info("No Records");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public CommonRes checkPaymentDetails(PaymentDetailsGetReq req) {
		CommonRes res = new CommonRes();
		List<PaymentDetailGetRes> listRes = new ArrayList();
		try {
			if("QUOTENO".equalsIgnoreCase(req.getSearchKey())) {
				List<PaymentDetail> details = paymentDetailRepo.findByQuoteNoOrderByEntryDateDesc(req.getKeyValue());
				
				if(!details.isEmpty()) {
					for(PaymentDetail det : details) {
						PaymentDetailGetRes paymentRes = new PaymentDetailGetRes();
						paymentRes.setQuoteNo(det.getQuoteNo() == null ? null : det.getQuoteNo());
						paymentRes.setPaymentId(det.getPaymentId() == null ? null : det.getPaymentId());
						paymentRes.setPaymentReferenceNo(det.getMerchantReference() == null ? null : det.getMerchantReference());
						paymentRes.setPaymentTypeId(det.getPaymentType() == null ? null : det.getPaymentType());
						paymentRes.setPaymentTypeDesc(det.getPaymentTypedesc() == null ? null : det.getPaymentTypedesc());
						paymentRes.setPremium(det.getPremium() == null ? null : Double.valueOf(det.getPremium().toString()));
						paymentRes.setRequestTime(det.getRequestTime() == null ? null : det.getRequestTime());
						paymentRes.setResponseTime(det.getResponseTime() == null ? null : det.getResponseTime());
						paymentRes.setResponseMessage(det.getResponseMessage() == null ? null : det.getResponseMessage());
						paymentRes.setResponseStatus(det.getResponseStatus() == null ? null : det.getResponseStatus());
						paymentRes.setMerchantReference(det.getMerchantReference() == null ? null : det.getMerchantReference());
						paymentRes.setCustomerEmail(det.getCustomerEmail() == null ? null : det.getCustomerEmail());
						paymentRes.setCustomerName(det.getCustomerName() == null ? null : det.getCustomerName());
						paymentRes.setReqCardNumber(det.getReqCardNumber() == null ? null : det.getReqCardNumber());
						paymentRes.setReqSignature(det.getReqSignature() == null ? null : det.getReqSignature());
						paymentRes.setAuthTransRefNo(det.getAuthTransRefNo() == null ? null : det.getAuthTransRefNo());
						paymentRes.setReqBillToSurname(det.getReqBillToSurname() == null ? null : det.getReqBillToSurname());
						paymentRes.setReqBillToAddressCity(det.getReqBillToAddressCity() == null ? null : det.getReqBillToAddressCity());
						paymentRes.setReqCardExpiryDate(det.getReqCardExpiryDate() == null ? null : det.getReqCardExpiryDate());
						paymentRes.setReqBillToAddrPostalCode(det.getReqBillToAddrPostalCode() == null ? null : det.getReqBillToAddrPostalCode());
						paymentRes.setReqBillToPhone(det.getReqBillToPhone() == null ? null : det.getReqBillToPhone());
						paymentRes.setAuthAmount(det.getAuthAmount() == null ? null : det.getAuthAmount());
						paymentRes.setAuthResponse(det.getAuthResponse() == null ? null : det.getAuthResponse());
						paymentRes.setReqBillToForename(det.getReqBillToForename() == null ? null : det.getReqBillToForename());
						paymentRes.setRequestToken(det.getRequestToken() == null ? null : det.getRequestToken());
						paymentRes.setAuthTime(det.getAuthTime() == null ? null : det.getAuthTime());
						paymentRes.setReqBillToEmail(det.getReqBillToEmail() == null ? null : det.getReqBillToEmail());
						paymentRes.setReqBillToCompanyName(det.getReqBillToCompanyName() == null ? null : det.getReqBillToCompanyName());
						paymentRes.setReqTransactionType(det.getReqTransactionType() == null ? null : det.getReqTransactionType());
						paymentRes.setReqReferenceNumber(det.getReqReferenceNumber() == null ? null : det.getReqReferenceNumber());
						paymentRes.setReqBillToAddressState(det.getReqBillToAddressState() == null ? null : det.getReqBillToAddressState());
						paymentRes.setReqBillToAddressLine2(det.getReqBillToAddressLine2() == null ? null : det.getReqBillToAddressLine2());
						paymentRes.setReqBillToAddressLine1(det.getReqBillToAddressLine1() == null ? null : det.getReqBillToAddressLine1());
						paymentRes.setCustomerid(det.getCustomerId() == null ? null : det.getCustomerId());
						paymentRes.setBankName(det.getBankName() == null ? null : det.getBankName());
						paymentRes.setBranchName(det.getBranchName() == null ? null : det.getBranchName());
						paymentRes.setBranchCode(det.getBranchCode() == null ? null : det.getBranchCode());
						paymentRes.setChequeNo(det.getChequeNo() == null ? null : det.getChequeNo());
						paymentRes.setChequeDate(det.getChequeDate() == null ? null : det.getChequeDate());
						paymentRes.setResSignature(det.getResSignature() == null ? null : det.getResSignature());
						paymentRes.setHitCount(det.getHitCount() == null ? null : det.getHitCount());
						paymentRes.setEntryDate(det.getEntryDate() == null ? null : det.getEntryDate());
						paymentRes.setUpdatedDate(det.getUpdatedDate() == null ? null : det.getUpdatedDate());
						paymentRes.setCreatedBy(det.getCreatedBy() == null ? null : det.getCreatedBy());
						paymentRes.setUpdatedBy(det.getUpdatedBy() == null ? null : det.getUpdatedBy());
						paymentRes.setPaymentStatus(det.getPaymentStatus() == null ? null : det.getPaymentStatus());
						
						listRes.add(paymentRes);
						
					}
					res.setCommonResponse(listRes);
					res.setIsError(false);	
					res.setErrorMessage(Collections.emptyList());
					res.setMessage("Success");
					
				}else {
					res.setCommonResponse(null);
					res.setIsError(false);	
					res.setErrorMessage(Collections.emptyList());
					res.setMessage("Success");
				}
				
			}else if("MERCHANTNO".equalsIgnoreCase(req.getSearchKey())) {
				PaymentDetail det = paymentDetailRepo.findByMerchantReference(req.getKeyValue());
				 if(det != null) {
					 PaymentDetailGetRes paymentRes = new PaymentDetailGetRes();
						paymentRes.setQuoteNo(det.getQuoteNo() == null ? null : det.getQuoteNo());
						paymentRes.setPaymentId(det.getPaymentId() == null ? null : det.getPaymentId());
						paymentRes.setPaymentReferenceNo(det.getMerchantReference() == null ? null : det.getMerchantReference());
						paymentRes.setPaymentTypeId(det.getPaymentType() == null ? null : det.getPaymentType());
						paymentRes.setPaymentTypeDesc(det.getPaymentTypedesc() == null ? null : det.getPaymentTypedesc());
						paymentRes.setPremium(det.getPremium() == null ? null : Double.valueOf(det.getPremium().toString()));
						paymentRes.setRequestTime(det.getRequestTime() == null ? null : det.getRequestTime());
						paymentRes.setResponseTime(det.getResponseTime() == null ? null : det.getResponseTime());
						paymentRes.setResponseMessage(det.getResponseMessage() == null ? null : det.getResponseMessage());
						paymentRes.setResponseStatus(det.getResponseStatus() == null ? null : det.getResponseStatus());
						paymentRes.setMerchantReference(det.getMerchantReference() == null ? null : det.getMerchantReference());
						paymentRes.setCustomerEmail(det.getCustomerEmail() == null ? null : det.getCustomerEmail());
						paymentRes.setCustomerName(det.getCustomerName() == null ? null : det.getCustomerName());
						paymentRes.setReqCardNumber(det.getReqCardNumber() == null ? null : det.getReqCardNumber());
						paymentRes.setReqSignature(det.getReqSignature() == null ? null : det.getReqSignature());
						paymentRes.setAuthTransRefNo(det.getAuthTransRefNo() == null ? null : det.getAuthTransRefNo());
						paymentRes.setReqBillToSurname(det.getReqBillToSurname() == null ? null : det.getReqBillToSurname());
						paymentRes.setReqBillToAddressCity(det.getReqBillToAddressCity() == null ? null : det.getReqBillToAddressCity());
						paymentRes.setReqCardExpiryDate(det.getReqCardExpiryDate() == null ? null : det.getReqCardExpiryDate());
						paymentRes.setReqBillToAddrPostalCode(det.getReqBillToAddrPostalCode() == null ? null : det.getReqBillToAddrPostalCode());
						paymentRes.setReqBillToPhone(det.getReqBillToPhone() == null ? null : det.getReqBillToPhone());
						paymentRes.setAuthAmount(det.getAuthAmount() == null ? null : det.getAuthAmount());
						paymentRes.setAuthResponse(det.getAuthResponse() == null ? null : det.getAuthResponse());
						paymentRes.setReqBillToForename(det.getReqBillToForename() == null ? null : det.getReqBillToForename());
						paymentRes.setRequestToken(det.getRequestToken() == null ? null : det.getRequestToken());
						paymentRes.setAuthTime(det.getAuthTime() == null ? null : det.getAuthTime());
						paymentRes.setReqBillToEmail(det.getReqBillToEmail() == null ? null : det.getReqBillToEmail());
						paymentRes.setReqBillToCompanyName(det.getReqBillToCompanyName() == null ? null : det.getReqBillToCompanyName());
						paymentRes.setReqTransactionType(det.getReqTransactionType() == null ? null : det.getReqTransactionType());
						paymentRes.setReqReferenceNumber(det.getReqReferenceNumber() == null ? null : det.getReqReferenceNumber());
						paymentRes.setReqBillToAddressState(det.getReqBillToAddressState() == null ? null : det.getReqBillToAddressState());
						paymentRes.setReqBillToAddressLine2(det.getReqBillToAddressLine2() == null ? null : det.getReqBillToAddressLine2());
						paymentRes.setReqBillToAddressLine1(det.getReqBillToAddressLine1() == null ? null : det.getReqBillToAddressLine1());
						paymentRes.setCustomerid(det.getCustomerId() == null ? null : det.getCustomerId());
						paymentRes.setBankName(det.getBankName() == null ? null : det.getBankName());
						paymentRes.setBranchName(det.getBranchName() == null ? null : det.getBranchName());
						paymentRes.setBranchCode(det.getBranchCode() == null ? null : det.getBranchCode());
						paymentRes.setChequeNo(det.getChequeNo() == null ? null : det.getChequeNo());
						paymentRes.setChequeDate(det.getChequeDate() == null ? null : det.getChequeDate());
						paymentRes.setResSignature(det.getResSignature() == null ? null : det.getResSignature());
						paymentRes.setHitCount(det.getHitCount() == null ? null : det.getHitCount());
						paymentRes.setEntryDate(det.getEntryDate() == null ? null : det.getEntryDate());
						paymentRes.setUpdatedDate(det.getUpdatedDate() == null ? null : det.getUpdatedDate());
						paymentRes.setCreatedBy(det.getCreatedBy() == null ? null : det.getCreatedBy());
						paymentRes.setUpdatedBy(det.getUpdatedBy() == null ? null : det.getUpdatedBy());
						
						listRes.add(paymentRes);
						
						res.setCommonResponse(listRes);
						res.setIsError(false);	
						res.setErrorMessage(Collections.emptyList());
						res.setMessage("Success");
				 }else {
						res.setCommonResponse(null);
						res.setIsError(false);	
						res.setErrorMessage(Collections.emptyList());
						res.setMessage("Success");
					}
			}else {
				res.setCommonResponse(null);
				res.setIsError(false);	
				res.setErrorMessage(Collections.emptyList());
				res.setMessage("Success");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setIsError(true);	
			//res.setErrorMessage(Collections.EMPTY_LIST);
			res.setMessage(e.getMessage());
		}
		return res;
	}

	private String getAsStringSafe(JsonObject json, String key, String defaultValue) {
		if (json.has(key) && !json.get(key).isJsonNull()) {
			return json.get(key).getAsString();
		}
		return defaultValue;
	}
    
	private void callAiLifeBotApi(PaymentDetail payment, String policyNo) {
		try {
			// Build phone number for bot notification
			String phoneNo = "";
			if (StringUtils.isNotBlank(payment.getWhatsappCode()) && StringUtils.isNotBlank(payment.getWhatsappNo())) {
				phoneNo = payment.getWhatsappCode() + payment.getWhatsappNo();
			} else if (StringUtils.isNotBlank(payment.getWhatsappNo())) {
				phoneNo = payment.getWhatsappNo();
			} else if (StringUtils.isNotBlank(payment.getReqBillToPhone())) {
				phoneNo = payment.getReqBillToPhone();
			}
			if (phoneNo != null) {
				phoneNo = phoneNo.replaceAll("\\+", "");
			}

			String botApiUrl = "https://api.ailifebot.com/bot-api/v2.0/customer/121992/bot/5183332087764a72/flow/3159917417604B688331C3AA01D01F98?authorization=Basic%20b57fe5ad-9597-46ad-8d30-f88e3fd3accd-IvWZbqr";

			JsonObject botReq = new JsonObject();
			botReq.addProperty("user.channel", "whatsapp");
			botReq.addProperty("user.phone_no", StringUtils.isNotBlank(phoneNo) ? phoneNo : payment.getReqBillToPhone());
			botReq.addProperty("payment_status", "ACCEPTED".equalsIgnoreCase(payment.getPaymentStatus()) ? "Successful" : "Successful");
			botReq.addProperty("policy_no", policyNo);

			log.info("Calling AiLifeBot API - Phone: {}, PolicyNo: {}, Status: {}", phoneNo, policyNo, payment.getPaymentStatus());

			try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
				HttpPost httpPost = new HttpPost(botApiUrl);
				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setEntity(new StringEntity(botReq.toString(), StandardCharsets.UTF_8));
				log.info("AiLifeBot API Request: {}", botReq.toString());
				try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
					String responseBody = EntityUtils.toString(response.getEntity());
					log.info("AiLifeBot API Response: {}", responseBody);
				}
			}
		} catch (Exception e) {
			log.error("Error calling AiLifeBot API for QuoteNo: {}", payment.getQuoteNo(), e);
		}
	}

	
	@Async
	public void triggerInternalAsyncWorkflows(PaymentDetail payment, HomePositionMaster homePosition) {
		try {
			log.info("Starting internal async workflows for QuoteNo: {}", payment.getQuoteNo());

			// 1. Integration Quote API
			try {
				log.info("Calling Internal Push Integration for QuoteNo: {}", payment.getQuoteNo());
				PremiaRequest premiaReq = new PremiaRequest();
				premiaReq.setQuoteNo(payment.getQuoteNo());
				premiaReq.setCompanyId(payment.getCompanyId());
				
				integrationController.pushPremiaIntegeration(premiaReq);
				
				homePosition.setIntegrationStatus("S");
				homerepo.save(homePosition);
			} catch (Exception e) {
				log.error("Error in Internal Push Integration for QuoteNo: {}", payment.getQuoteNo(), e);
				homePosition.setIntegrationStatus("F");
				homePosition.setIntegrationError(e.getMessage());
				homerepo.save(homePosition);
			}

			// 2. Reinsurance API
			try {
				log.info("Calling Internal Reinsurance Integration for QuoteNo: {}", payment.getQuoteNo());
				ReInsuranceQuoteReq riReq = new ReInsuranceQuoteReq();
				riReq.setQuoteNo(payment.getQuoteNo());
//				riReq.setCreatedBy(payment.getCreatedBy());
//				riReq.setUserType(homePosition.getUserType());
				riReq.setCreatedBy(payment.getCreatedBy());
				riReq.setUserType("Broker");

				
				reinsuranceService.pushReInsuranceDetails(riReq);
				
				homePosition.setRiStatus("S");
				homerepo.save(homePosition);
			} catch (Exception e) {
				log.error("Error in Internal Reinsurance Integration for QuoteNo: {}", payment.getQuoteNo(), e);
				homePosition.setRiStatus("F");
				homerepo.save(homePosition);
			}

			// 3. Send Mail API
			try {
				log.info("Calling Internal Email Notification for QuoteNo: {}", payment.getQuoteNo());
				Map<String, String> placeholders = new HashMap<>();
				placeholders.put("{POLICY_NUMBER}", homePosition.getPolicyNo());
				placeholders.put("{QUOTE_NO}", payment.getQuoteNo());
				placeholders.put("{CLIENT_NAME}", payment.getCustomerName());
				placeholders.put("{AMOUNT_DUE}", payment.getPremium() != null ? payment.getPremium().toString() : "0.00");

				MailRequestDTO mailReq = MailRequestDTO.builder()
						.companyId(payment.getCompanyId())
						.productId(99999L)
						.templateName("Whatsapp Motor with pdf")
						.toEmail(payment.getCustomerEmail())
						.quoteNo(payment.getQuoteNo())
						.docType("ALL")
						.placeholders(placeholders)
						.build();

				emailNotificationService.processAndSendMail(mailReq); 
			} catch (Exception e) {
				log.error("Error in Internal Email Notification for QuoteNo: {}", payment.getQuoteNo(), e);
			}

		} catch (Exception e) {
			log.error("Global error in triggerInternalAsyncWorkflows for QuoteNo: {}", payment.getQuoteNo(), e);
		}
	}
	private JsonObject mtp(PaymentVendorMaster vendor, PaymentDetail payment, PaymentInfo paymentInfo) {

	    JsonObject jsonResponse = new JsonObject();

	    try {

	        String quoteNo = paymentInfo.getQuoteNo();
	        log.info("===== MTP Payment Initiation Started for QuoteNo : {} =====", quoteNo);

	        MotorDataDetails motorData = motorDataDetailsRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);

	        if (motorData == null) {
	            log.error("MTP: No motor data found for QuoteNo : {}", quoteNo);
	            jsonResponse.addProperty("result", "ERROR");
	            jsonResponse.addProperty("message", "Motor data not found for QuoteNo : " + quoteNo);
	            return jsonResponse;
	        }

	        String numberPlate = motorData.getRegistrationNumber();
	        String insuranceType = motorData.getInsuranceType();
	        String assessmentType = "1".equals(insuranceType) ? "M" : "E";

	        log.info("Registration Number : {}", numberPlate);
	        log.info("Insurance Type      : {}", insuranceType);
	        log.info("Assessment Type     : {}", assessmentType);

	        // Get OAuth Token
	        String accessToken = getMtpAccessToken(
	                vendor.getCheckStatusUrl().trim(),
	                vendor.getVendorCode(),
	                vendor.getApiSecretKey());

	        if (accessToken == null) {
	            log.error("Failed to get MTP OAuth Token.");
	            jsonResponse.addProperty("result", "ERROR");
	            jsonResponse.addProperty("message", "MTP OAuth token fetch failed");
	            return jsonResponse;
	        }

	        // Format Mobile Number
	        String mobileNo = payment.getReqBillToPhone();

	        log.info("Original Mobile Number : {}", mobileNo);

	        if (mobileNo != null) {
	            mobileNo = mobileNo.trim();

	            if (mobileNo.startsWith("+256")) {
	                mobileNo = mobileNo.substring(4);
	            } else if (mobileNo.startsWith("256")) {
	                mobileNo = mobileNo.substring(3);
	            }

	            if (!mobileNo.startsWith("0")) {
	                mobileNo = "0" + mobileNo;
	            }
	        }

	        log.info("Formatted Mobile Number : {}", mobileNo);

	        JsonObject requestPayload = new JsonObject();
	        requestPayload.addProperty("numberPlate", numberPlate);
	        requestPayload.addProperty("msisdn", mobileNo);
	        requestPayload.addProperty("assessmentType", assessmentType);
	        requestPayload.addProperty("partnerIdentifier", "ALLIANCE");

	        log.info("MTP Request Payload for QuoteNo {} : {}", quoteNo, requestPayload);

	        try (CloseableHttpClient client = HttpClients.createDefault()) {

	            String paymentUrl = vendor.getPaymentUrlLink().trim();

	            HttpPost httpPost = new HttpPost(paymentUrl);
	            httpPost.setHeader("Authorization", "Bearer " + accessToken);
	            httpPost.setHeader("Content-Type", "application/json");
	            httpPost.setEntity(new StringEntity(requestPayload.toString(), "UTF-8"));

	            try (CloseableHttpResponse httpResponse = client.execute(httpPost)) {

	                String responseString = EntityUtils.toString(httpResponse.getEntity());

	                log.info("MTP Response for QuoteNo {} : {}", quoteNo, responseString);
	                

	                JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();

	                int returnCode = responseJson.has("returnCode")
	                        ? responseJson.get("returnCode").getAsInt()
	                        : -1;

	                String returnMessage = responseJson.has("returnMessage")
	                        ? responseJson.get("returnMessage").getAsString()
	                        : "Unknown error from MTP";

	                log.info("MTP Return Code    : {}", returnCode);
	                log.info("MTP Return Message : {}", returnMessage);

	                // Error Response
	                if (returnCode != 0) {

	                    log.error("MTP Payment Failed for QuoteNo {} : {}", quoteNo, returnMessage);

	                    payment.setResponseMessage(returnMessage);
	                    paymentDetailRepo.saveAndFlush(payment);

	                    jsonResponse.addProperty("result", "ERROR");
	                    jsonResponse.addProperty("returnCode", returnCode);
	                    jsonResponse.addProperty("message", returnMessage);

	                    return jsonResponse;
	                }

	                // Success Response
	                String mtpPaymentRequestId = responseJson.has("paymentRequestId")
	                        ? responseJson.get("paymentRequestId").getAsString()
	                        : "";

	                String paymentChannel = responseJson.has("paymentChannel")
	                        ? responseJson.get("paymentChannel").getAsString()
	                        : "";

	                log.info("MTP Payment Success");
	                log.info("Payment Request Id : {}", mtpPaymentRequestId);
	                log.info("Payment Channel    : {}", paymentChannel);

	                JsonObject innerResponse = new JsonObject();
	                innerResponse.addProperty("payment_gateway_url", "");
	                innerResponse.addProperty("paymentRequestId", mtpPaymentRequestId);
	                innerResponse.addProperty("paymentChannel", paymentChannel);
	                innerResponse.addProperty("returnMessage", returnMessage);

	                JsonArray dataArray = new JsonArray();
	                dataArray.add(innerResponse);

	                jsonResponse.addProperty("result", "SUCCESS");
	                jsonResponse.addProperty("message", returnMessage);
	                jsonResponse.add("data", dataArray);

	                payment.setReference(mtpPaymentRequestId);
	                payment.setChannel(paymentChannel);
	                payment.setResponseMessage(innerResponse.toString());

	                paymentDetailRepo.saveAndFlush(payment);

	                log.info("Payment Details Updated Successfully for QuoteNo : {}", quoteNo);
	            }
	        }

	    } catch (Exception e) {

	        log.error("Exception while initiating MTP payment for QuoteNo {}",
	                paymentInfo.getQuoteNo(), e);

	        jsonResponse.addProperty("result", "ERROR");
	        jsonResponse.addProperty("message", e.getMessage());
	    }

	    log.info("===== MTP Payment Initiation Completed =====");

	    return jsonResponse;
	}
	private String getMtpAccessToken(String tokenUrl, String clientId, String clientSecret) {
	    try (CloseableHttpClient client = HttpClients.createDefault()) {
	        HttpPost post = new HttpPost(tokenUrl);
	        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

	        List<org.apache.http.NameValuePair> params = new java.util.ArrayList<>();
	        params.add(new org.apache.http.message.BasicNameValuePair("grant_type",    "client_credentials"));
	        params.add(new org.apache.http.message.BasicNameValuePair("client_id",     clientId));
	        params.add(new org.apache.http.message.BasicNameValuePair("client_secret", clientSecret));
	        post.setEntity(new org.apache.http.client.entity.UrlEncodedFormEntity(params, "UTF-8"));

	        try (CloseableHttpResponse response = client.execute(post)) {
	            String body = EntityUtils.toString(response.getEntity());
	            log.info("MTP token response: {}", body);
	            JsonObject tokenJson = JsonParser.parseString(body).getAsJsonObject();
	            if (tokenJson.has("access_token")) {
	                return tokenJson.get("access_token").getAsString();
	            }
	            log.error("MTP token response missing access_token: {}", body);
	            return null;
	        }
	    } catch (Exception e) {
	        log.error("MTP OAuth token fetch failed: {}", e.getMessage(), e);
	        return null;
	    }
	}
	
	private JsonObject mtpOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
	    JsonObject jsonResponse = new JsonObject();
	    try {
	        
	        PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(
	                payment.getQuoteNo(), payment.getPaymentId());

	        if (paymentInfo == null) {
	            log.error("MTP status: paymentInfo not found for quoteNo={} paymentId={}",
	                    payment.getQuoteNo(), payment.getPaymentId());
	            payment.setPaymentStatus("FAILED");
	            payment.setResponseMessage("PaymentInfo not found");
	            payment.setResponseTime(new Date());
	            paymentDetailRepo.save(payment);
	            return jsonResponse;
	        }

	        String mtpPaymentRequestId =payment.getReference() ;  //payment.getReference() "9392"
	        String paymentChannel      = payment.getChannel()   ;  // payment.getChannel() "AIRTELMONEY"

	        if (StringUtils.isBlank(mtpPaymentRequestId)) {
	            log.error("MTP status: no paymentRequestId (shorternUrl) found for quoteNo={}", payment.getQuoteNo());
	            payment.setPaymentStatus("FAILED");
	            payment.setResponseMessage("MTP paymentRequestId not found");
	            payment.setResponseTime(new Date());
	            paymentDetailRepo.save(payment);
	            return jsonResponse;
	        }

	       
	        String accessToken = getMtpAccessToken(
	                vendor.getCheckStatusUrl(),
	                vendor.getVendorCode(),
	                vendor.getApiSecretKey()
	        );
	        if (accessToken == null) {
	            payment.setPaymentStatus("PENDING");
	            payment.setResponseMessage("MTP token fetch failed during status check");
	            payment.setResponseTime(new Date());
	            paymentDetailRepo.save(payment);
	            return jsonResponse;
	        }

	       
	        String statusUrl = vendor.getReturnUrlLink();

	        JsonObject requestPayload = new JsonObject();
	        requestPayload.addProperty("paymentRequestId",mtpPaymentRequestId  ); //"9392"
	        requestPayload.addProperty("paymentChannel",  paymentChannel ); //"AIRTELMONEY"

	        try (CloseableHttpClient client = HttpClients.createDefault()) {
	            HttpPost httpPost = new HttpPost(statusUrl);
	            httpPost.setHeader("Authorization", "Bearer " + accessToken);
	            httpPost.setHeader("Content-Type", "application/json");
	            httpPost.setEntity(new StringEntity(requestPayload.toString(), "UTF-8"));

	            try (CloseableHttpResponse httpResponse = client.execute(httpPost)) {
	                String responseString = EntityUtils.toString(httpResponse.getEntity());
	                log.info("MTP payment-status response for quoteNo {}: {}", payment.getQuoteNo(), responseString);

	                JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
	                String paymentStatus = responseJson.has("paymentStatus")
	                        ? responseJson.get("paymentStatus").getAsString() : "FAILED";
	                String returnMessage = responseJson.has("returnMessage")
	                        ? responseJson.get("returnMessage").getAsString() : "";

	                switch (paymentStatus) {
	                    case "COMPLETED":
	                    case "APPROVED":
	                        payment.setPaymentStatus("ACCEPTED");
	                        if (responseJson.has("transactionId")) {
	                            payment.setAuthTransRefNo(responseJson.get("transactionId").getAsString());	                        }
	                        if (responseJson.has("stickerReference")) {
	                            payment.setAccountNumber(responseJson.get("stickerReference").getAsString());
	                        }
	                        if (responseJson.has("downloadLink")) {
	                            payment.setResSignature(responseJson.get("downloadLink").getAsString());
	                        }
	                        
	                        payment.setAuthResponse(paymentStatus);
	                        payment.setResponseMessage(returnMessage);
	                        payment.setResponseTime(new Date());
	                        
	                        String stickerNo = responseJson.get("stickerReference").getAsString();

	                     try {
	                         HomePositionMaster homePosition =homerepo.findByQuoteNo(payment.getQuoteNo());
	                         if (homePosition != null) {
	                             homePosition.setStickerNumber(stickerNo);
	                             homerepo.save(homePosition);
	                         } else {
	                             log.warn("HomePositionMaster not found for quoteNo={}, cannot save stickerNumber", payment.getQuoteNo());
	                         }
	                     } catch (Exception e) {
	                         log.error("Failed to save stickerNumber in HomePositionMaster for quoteNo={}", payment.getQuoteNo(), e);
	                         // do not fail the payment flow because of this
	                     }
	                        

	                        try {
	                            MtpPolicyReq policyReq = new MtpPolicyReq();
	                            String quoteNo = paymentInfo.getQuoteNo();
	                	        MotorDataDetails motorData = motorDataDetailsRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);
	                            policyReq.setVrn(motorData.getRegistrationNumber()); 
	                            
//	                            policyReq.setVrn("UFB164F"); 
	                            policyReq.setPartnerIdentifier("ALLIANCE");

	                          
	                            PolicyResponse policyResponse = mtpPartnerService.policyDetails(policyReq);

	                            if (policyResponse != null && policyResponse.getReturnObject() != null) {
	                                mtpPartnerService.updatePolicyCoverAndFactorRate(
	                                        payment.getQuoteNo(),
	                                        motorData.getRequestReferenceNo(), 
	                                        policyResponse.getReturnObject()
	                                );
	                            } else {
	                                log.warn("Policy details returned empty for quoteNo={} after successful payment", payment.getQuoteNo());
	                            }
	                        } catch (Exception e) {
	                            log.error("Failed to fetch/update policy details post-payment for quoteNo={}", payment.getQuoteNo(), e);
	                            // do not fail the payment flow because of this enrichment step
	                        }
	                        
	                        break;

	                    case "PENDING":
	                        payment.setPaymentStatus("PENDING");
	                        payment.setResponseMessage(returnMessage);
	                        payment.setResponseTime(new Date());
	                        break;

	                    case "FAILED":
	                    default:
	                        payment.setPaymentStatus("FAILED");
	                        payment.setResponseMessage(returnMessage);
	                        payment.setResponseTime(new Date());
	                        break;
	                }

	                paymentDetailRepo.save(payment);
	                jsonResponse.addProperty("paymentStatus", paymentStatus);
	                jsonResponse.addProperty("returnMessage", returnMessage);
	            }
	        }

	    } catch (Exception e) {
	        log.error("MTP order status exception for quoteNo {}: {}", payment.getQuoteNo(), e.getMessage(), e);
	        payment.setPaymentStatus("FAILED");
	        payment.setResponseMessage("MTP status check failed: " + e.getMessage());
	        payment.setResponseTime(new Date());
	        paymentDetailRepo.save(payment);
	    }
	    return jsonResponse;
	}
	
	private String downloadAndSaveMtpSticker(String stickerReference, String vehicleRegNo, String downloadLink) {
	    
	    try (CloseableHttpClient client = HttpClients.createDefault()) {
	    	
	    	downloadLink = "https://uiapaymentstest.servicecops.com/test/Rest/stk/"
                    + "697563771" + "/" + "UBP373R";
	        HttpGet httpGet = new HttpGet(downloadLink);

	        try (CloseableHttpResponse response = client.execute(httpGet)) {
	            int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode != 200) {
	                log.error("MTP sticker download failed with HTTP {}: {}", statusCode, downloadLink);
	                return null;
	            }

	            org.apache.http.HttpEntity entity = response.getEntity();
	            byte[] stickerBytes = EntityUtils.toByteArray(entity);

	            String contentType = entity.getContentType() != null
	                    ? entity.getContentType().getValue() : "application/pdf";
	            String extension = contentType.contains("pdf") ? ".pdf"
	                    : contentType.contains("png") ? ".png"
	                    : contentType.contains("jpeg") ? ".jpg" : ".pdf";
	            
	            String fileName = vehicleRegNo + "_" + stickerReference + extension;
	            java.io.File dir = new java.io.File(mtpStickerDownloadPath);
	            if (!dir.exists()) {
	                dir.mkdirs();
	            }
	            java.io.File stickerFile = new java.io.File(dir, fileName);
	            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(stickerFile)) {
	                fos.write(stickerBytes);
	            }

	            log.info("MTP sticker downloaded: {}", stickerFile.getAbsolutePath());
	            return stickerFile.getAbsolutePath();
	        }
	    } catch (Exception e) {
	        log.error("MTP sticker download exception for stickerRef {}: {}", stickerReference, e.getMessage(), e);
	        return null;
	    }
	}
	
	public PaymentVendorMaster getMtpVendor(String companyId) {
        List<PaymentVendorMaster> vendors = paymentVendorRepo
            .findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(companyId, "Y", "5");
        return vendors.stream()
            .filter(v -> "mtp".equals(v.getVendorName()))
            .findFirst().orElse(null);
    }
	
	@Override
	public JsonObject createOrderForPaymentMTP(PaymentDetail payment) {
		try {

			if(payment!=null ) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());

				String userytype="b2b";
				if(paymentInfo.getSubUserType().equalsIgnoreCase("b2c")) {
					userytype="b2c";
				}

				List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,paymentInfo.getProductId());
				PaymentVendorMaster vendor =null;
				if((paymentInfo.getProductId().equals(5) && payment.getCompanyId().equals("100019") &&  payment.getPaymentType().equals("6")) ||
						(paymentInfo.getProductId().equals(125) && payment.getCompanyId().equals("100019"))) {
					vendor =getMtpVendor(payment.getCompanyId());	
				}
				
				else if(paymentId!=null && paymentId.size()>0) {						 
					vendor = paymentId.get(0);
				}else {
					paymentId=paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,99999);
					vendor = paymentId.get(0);
				}
				
				JsonObject response = null;
 
//				if("lipila".equals(vendor.getVendorName())) {
//					return lipila(vendor,payment);
				if("lipila".equals(vendor.getVendorName())) {
				    String lipilaPaymentType = payment.getLipilaPaymentType();
				    String whatsappYn = payment.getWhatsappYn();
				    
				    if (StringUtils.isBlank(lipilaPaymentType)) {
				        lipilaPaymentType = "card";
				        log.warn("LipilaPaymentType not specified for QuoteNo: {}. Defaulting to 'card'", payment.getQuoteNo());
				    }
				    
				    log.info("Lipila Payment - QuoteNo: {}, Type: {}, WhatsApp: {}", 
				        payment.getQuoteNo(), lipilaPaymentType, whatsappYn);
				    
				    return lipila(vendor, payment, lipilaPaymentType, whatsappYn);
				    
				}else if("ipayafrica".equals(vendor.getVendorName())) {
					return ipayafrica(vendor,payment);
				}else if("pesapal".equals(vendor.getVendorName())){
					return pesapal(vendor,payment);
				}else if("peach".equals(vendor.getVendorName())){
					return peach(vendor,payment);
				}
				else if ("mtp".equals(vendor.getVendorName())) {
				    return mtpForMot(vendor, payment, paymentInfo);
				}
				/**else if ("cybersource".equals(vendor.getVendorName())) {
					return cybersource(vendor, payment);
				}**/
				
				else if("cybersource".equals(vendor.getVendorName())){
					//return cybersource(vendor,payment);
					   return cyberSourcePaymentDetails(vendor, payment, paymentInfo);
				}
 
//				else if("mpesa".equals(vendor.getVendorName())){
//					return mpesa(vendor,payment);
//				}
				
				else if ("mpesa".equals(vendor.getVendorName())) {
					// Real Vodacom M-Pesa STK push (MpesaPaymentImpl) — no hosted payment_gateway_url is
					// returned, the customer confirms on-phone. Previously this branch called the mPesaPayment()
					// stub which faked "SUCCESS" with a literal "Dummy" URL and never contacted Vodacom.
					JsonObject responsempesa = new JsonObject();
					if (!mozambiqueMpesaLiveGatewayEnabled) {
						// Kill switch: rolled back to a real M-Pesa integration issue, don't fake success.
						log.warn("Mpesa live gateway disabled via phoenix.whatsapp.mozambique.mpesa.liveGateway.enabled=false; QuoteNo={}",
								payment.getQuoteNo());
						responsempesa.addProperty("result", "FAILED");
						responsempesa.addProperty("Message", "Mpesa payment is temporarily unavailable");
						return responsempesa;
					}
					JsonObject mpesaResult = mpesaPaymentService.payment(payment, vendor);
					String resultValue = mpesaResult != null && mpesaResult.has("result")
							? mpesaResult.get("result").getAsString() : "Fail";
					responsempesa.addProperty("result", "Success".equalsIgnoreCase(resultValue) ? "SUCCESS" : "FAILED");
					responsempesa.addProperty("Message", mpesaResult != null && mpesaResult.has("Message")
							? mpesaResult.get("Message").getAsString() : "Mpesa payment request failed");
					return responsempesa;
				}
				else if("cybersource".equals(vendor.getVendorName())){
					//return cybersource(vendor,payment);
					return cyberSourcePaymentDetails(vendor, payment, paymentInfo);
				}else if("tingg".equals(vendor.getVendorName())){
					return tingg(vendor,payment);
				}else {
					return selcomPayment(vendor,payment);
				}
				
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private JsonObject mtpForMot(PaymentVendorMaster vendor, PaymentDetail payment, PaymentInfo paymentInfo) {
	    JsonObject jsonResponse = new JsonObject();
	    try {
	        
	        String quoteNo = paymentInfo.getQuoteNo();
	        MotorDataDetails motorData = motorDataDetailsRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);

	        if (motorData == null) {
	            log.error("MTP: No motor data found for quoteNo {}", quoteNo);
	            jsonResponse.addProperty("result", "ERROR");
	            jsonResponse.addProperty("message", "Motor data not found for quote: " + quoteNo);
	            return jsonResponse;
	        }

	        String numberPlate   = motorData.getRegistrationNumber();
	        String insuranceType = motorData.getInsuranceType(); 
	        String assessmentType = "1".equals(insuranceType) ? "M" : "E";

	        String accessToken = getMtpAccessToken(
	        		vendor.getCheckStatusUrl().trim(),   
	            vendor.getVendorCode(),          
	            vendor.getApiSecretKey()    
	        );
	        if (accessToken == null) {
	            jsonResponse.addProperty("result", "ERROR");
	            jsonResponse.addProperty("message", "MTP OAuth token fetch failed");
	            return jsonResponse;
	        }

	        
	        JsonObject requestPayload = new JsonObject();
	        requestPayload.addProperty("numberPlate",        numberPlate);
	        requestPayload.addProperty("msisdn",             "0"+payment.getReqBillToPhone());
	        requestPayload.addProperty("assessmentType",     assessmentType);
	        requestPayload.addProperty("partnerIdentifier",  "ALLIANCE");  

	        
	        try (CloseableHttpClient client = HttpClients.createDefault()) {
	        	String paymentUrl = vendor.getPaymentUrlLink().trim();
	            HttpPost httpPost = new HttpPost(paymentUrl);
	            httpPost.setHeader("Authorization", "Bearer " + accessToken);
	            httpPost.setHeader("Content-Type", "application/json");
	            httpPost.setEntity(new StringEntity(requestPayload.toString(), "UTF-8"));

	            try (CloseableHttpResponse httpResponse = client.execute(httpPost)) {
	                String responseString = EntityUtils.toString(httpResponse.getEntity());
	                log.info("MTP initiate-payment response for quoteNo {}: {}", quoteNo, responseString);

	                JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
	                int returnCode = responseJson.has("returnCode")
	                        ? responseJson.get("returnCode").getAsInt() : -1;

	                if (returnCode == 0) {
	                    String mtpPaymentRequestId = responseJson.get("paymentRequestId").getAsString();
	                    String paymentChannel      = responseJson.has("paymentChannel")
	                            ? responseJson.get("paymentChannel").getAsString() : "";
	                    String returnMessage       = responseJson.get("returnMessage").getAsString();

//	                    payment.setReference(mtpPaymentRequestId);
//	                    payment.setChannel(paymentChannel);
//	                    paymentinforepo.save(paymentInfo);
	                    

	                    log.info("MTP paymentRequestId {} and channel {} saved to paymentInfo for quoteNo {}",
	                            mtpPaymentRequestId, paymentChannel, quoteNo);

	                   
	                    jsonResponse.addProperty("result", "SUCCESS");
	                    jsonResponse.addProperty("message", returnMessage);

	                    JsonObject innerResponse = new JsonObject();
	                    innerResponse.addProperty("payment_gateway_url", "");  
	                    innerResponse.addProperty("paymentRequestId", mtpPaymentRequestId);
	                    innerResponse.addProperty("paymentChannel", paymentChannel);
	                    innerResponse.addProperty("returnMessage", returnMessage);
	                    
	                    payment.setReference(mtpPaymentRequestId);
	                    payment.setChannel(paymentChannel);
	                    payment.setResponseMessage(innerResponse.toString() );
	                    JsonArray dataArray = new JsonArray();
	                    dataArray.add(innerResponse);
	                    jsonResponse.add("data", dataArray);

	                } else {
	                    String errorMsg = responseJson.has("returnMessage")
	                            ? responseJson.get("returnMessage").getAsString()
	                            : "Unknown error from MTP";
	                    log.error("MTP payment failed for quoteNo {}: returnCode={}, message={}",
	                            quoteNo, returnCode, errorMsg);
	                    jsonResponse.addProperty("result", "ERROR");
	                    jsonResponse.addProperty("message", errorMsg);
	                    jsonResponse.addProperty("returnCode", returnCode);
	                    payment.setResponseMessage(errorMsg);
	                }
	            }
	        }

	    } catch (Exception e) {
	        log.error("MTP payment exception for quoteNo {}: {}", paymentInfo.getQuoteNo(), e.getMessage(), e);
	        jsonResponse.addProperty("result", "ERROR");
	        jsonResponse.addProperty("message", "MTP payment initiation failed: " + e.getMessage());
	    }
	    return jsonResponse;
	}
	
	@Override
	public Object handleSmartPaySourceWebhook(Object request, String tokens) {
		Map<String,Object> apiResp = new HashMap<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String,Object> data = mapper.convertValue(request, Map.class);
			String quoteNo = data.get("QuoteNo") == null ? "" : data.get("QuoteNo").toString();
			String paymentId = data.get("PaymentId") == null ? "" : data.get("PaymentId").toString();
			String paymentType = data.get("PaymentType") == null ? "" : data.get("PaymentType").toString();
			String customerIp = data.get("CustomerIp") == null ? "" : data.get("CustomerIp").toString();
			String merchRefNo = data.get("MerchantReferenceNo") == null ? "" : data.get("MerchantReferenceNo").toString();
			PaymentDetail payment = paymentDetailRepo.findByQuoteNoAndPaymentIdAndMerchantReferenceAndPaymentStatusAndPaymentType(quoteNo,paymentId,
					merchRefNo,"PENDING",paymentType);
			
			if(payment != null) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());

				String userytype="b2b";
				if(paymentInfo.getSubUserType().equalsIgnoreCase("b2c")) {
					userytype="b2c";
				}

				List<PaymentVendorMaster> paymentvendor= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,paymentInfo.getProductId());
				PaymentVendorMaster vendor =null;
				if(paymentvendor!=null && paymentvendor.size()>0) {						 
					vendor = paymentvendor.get(0);
				}else {
					paymentvendor=paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,99999);
					vendor = paymentvendor.get(0);
				}
				
				
				String apiKey = null;
				String apiSecret = null;
				String baseUrl = null;
				String orderPath =null;
				String vendorCode=null;
				String redirect_url=null;
				String cancel_url=null;
				String webHookUrl=null;
				String signedFields="";
				if(vendor!=null) {
					apiKey=vendor.getApiKey();
					apiSecret=vendor.getApiSecretKey();
					baseUrl=vendor.getApiBaseUrl();
					orderPath=vendor.getPaymentUrlLink();
					vendorCode=vendor.getVendorCode();
					redirect_url=vendor.getReturnUrlLink();
					cancel_url=vendor.getCancelUrlLink();
					webHookUrl=vendor.getWebhookUrlLink();

					redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
					cancel_url=cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
					signedFields=vendor.getSignedFields();
				}
				
				Map<String, Object> orderDict = new LinkedHashMap<>();
				
				orderDict.put("merchant_id",vendorCode);
				
				String orderId = payment.getMerchantReference()
				        .replaceAll("[^A-Za-z0-9]", "");

				orderDict.put("order_id",orderId);
				orderDict.put("amount", payment.getPremiumLc().toPlainString() );
				orderDict.put("currency", payment.getCurrencyId());
				orderDict.put("cancel_url", cancel_url);
				orderDict.put("redirect_url", redirect_url);
				orderDict.put("customer_identifier",payment.getCustomerId());
				orderDict.put("si_type",null);
				orderDict.put("si_mer_ref_no",null);
				orderDict.put("billing_name",payment.getCustomerName());
				orderDict.put("billing_last_name","");
				orderDict.put("billing_address",payment.getReqBillToAddressLine1());
				orderDict.put("billing_city",payment.getReqBillToAddressCity());
				orderDict.put("billing_state",payment.getReqBillToAddressState());
				orderDict.put("billing_zip",payment.getReqBillToAddrPostalCode());
				orderDict.put("billing_country",payment.getReqBillToCountry());
				orderDict.put("billing_email",payment.getReqBillToEmail());
				orderDict.put("merchant_param1",null);
				orderDict.put("merchant_param2",null);
				orderDict.put("merchant_param3",null);
				orderDict.put("merchant_param4",null);
				orderDict.put("merchant_param5",null);
				orderDict.put("card_number",null);
				orderDict.put("expiry_month",null);
				orderDict.put("expiry_year",null);
				orderDict.put("cvv_number",null);
				
				//singleRequestMap
				
				StringBuilder requestString = new StringBuilder();

				for (Map.Entry<String, Object> entry : orderDict.entrySet()) {

				    if (requestString.length() > 0) {
				        requestString.append("&");
				    }

				    requestString.append(entry.getKey())
				                 .append("=")
				                 .append(entry.getValue() == null ? "" : entry.getValue());
				}

				String plainRequest = requestString.toString();
				
				System.out.println("SmartPay Plain Request: " + plainRequest);
				
				String encRequest =
				        SmartPayEncryptionUtil.encrypt(
				                plainRequest,
				                apiSecret
				        );

				System.out.println("SmartPay encRequest: " + encRequest);
				
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

				RestTemplate restTemplate = new RestTemplate();
				MultiValueMap<String, String> formData =
				        new LinkedMultiValueMap<>();

				formData.add("encRequest", encRequest);
				formData.add("access_code", apiKey);
				
				HttpEntity<MultiValueMap<String, String>> requests =
				        new HttpEntity<>(formData, headers);

				ResponseEntity<String> response =
				        restTemplate.postForEntity(
				        		baseUrl+orderPath,
				                requests,
				                String.class
				        );
				
				System.out.println("SmartPay Response: " + response.getBody());

				Map<String,Object> requestmapping = new HashMap<>();

				requestmapping.put("encRequest", encRequest);
				requestmapping.put("access_code", apiKey);
				requestmapping.put("apiUrl", baseUrl+orderPath);
				requestmapping.put("HTMLContent", response.getBody());
			        
			      
			        apiResp.put("Result", requestmapping);
			        apiResp.put("Message", "Success");
			        apiResp.put("IsError", false);
			        apiResp.put("ErrorMessage", null);
			        apiResp.put("ErrorMessage", 0);
			                
			}else {
				log.info(paymentId +" No Record Found") ;
		        apiResp.put("Result", null);
		        apiResp.put("Message", "Success");
		        apiResp.put("IsError", false);
		        apiResp.put("ErrorMessage", null);
		        apiResp.put("ErrorMessage", 0);
			}
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return apiResp;
	}
	@Override
	public Map<String,Object>  checkOmanPayment(MultiValueMap<String, String> req) {
		Map<String,Object> res = new HashMap();
		boolean resCheck = false;
		try {
			
			ObjectMapper mapper = new ObjectMapper();
		    List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc("100055","Y","1");
		    PaymentVendorMaster vendor = null;
		    
		    System.out.println("Payment Vendor Size "+ paymentId.size());
		    if(!paymentId.isEmpty()) {
		    	vendor = paymentId.get(0);	
		    }
		    
		    String quoteNo= "";
		    String merchantNo = req.getFirst("orderNo");
		    String status = req.getFirst("orderStatus");
		    String encResponse = req.getFirst("encResp");
		    String apiSecret = vendor.getApiSecretKey();
			String returnUrl = vendor.getCancelUrlLink();
			 
		    String decResponse =
			        SmartPayEncryptionUtil.decrypt(
			        		encResponse,
			                apiSecret
			        );
		    
		    System.out.println("OMAN DECRYPT RESPONSE "+ decResponse);
		    
		    if(StringUtils.isNotBlank(decResponse)){
		    	Map<String, String> payRes = new HashMap<>();

		    	for (String pair : decResponse.split("&")) {

		    	    String[] keyValue = pair.split("=", 2);

		    	    String key = URLDecoder.decode(
		    	            keyValue[0],
		    	            StandardCharsets.UTF_8
		    	    );

		    	    String value = keyValue.length > 1
		    	            ? URLDecoder.decode(
		    	                    keyValue[1],
		    	                    StandardCharsets.UTF_8
		    	              )
		    	            : "";

		    	    payRes.put(key, value);
		    	}
		    	
		    	if(payRes != null) {
		    		System.out.println("OMAN RESPONSE "+ payRes);
		    		String merchant = payRes.get("order_id") == null ? "" : payRes.get("order_id").toString();
		    		merchant = merchant.replaceFirst("(?=\\d)", "-");
		    		
		    		System.out.println("CONVERTED MERCHANT "+ merchant);
		    		String tracking_id = payRes.get("tracking_id") == null ? "" : payRes.get("tracking_id").toString();
		    		String bank_ref_no = payRes.get("bank_ref_no") == null ? "" : payRes.get("bank_ref_no").toString();
		    		String order_status = payRes.get("order_status") == null ? "" : payRes.get("order_status").toString();
		    		String failure_message = payRes.get("failure_message") == null ? "" : payRes.get("failure_message").toString();
		    		String payment_mode = payRes.get("payment_mode") == null ? "" : payRes.get("payment_mode").toString();
		    		String card_name = payRes.get("card_name") == null ? "" : payRes.get("card_name").toString();
		    		String status_code = payRes.get("status_code") == null ? "" : payRes.get("status_code").toString();
		    		String status_message = payRes.get("status_message") == null ? "" : payRes.get("status_message").toString();
		    		String currency = payRes.get("currency") == null ? "" : payRes.get("currency").toString();
		    		String amount = payRes.get("amount") == null ? "" : payRes.get("amount").toString();
		    		String billing_name = payRes.get("billing_name") == null ? "" : payRes.get("billing_name").toString();
		    		String billing_address = payRes.get("billing_address") == null ? "" : payRes.get("billing_address").toString();
		    		String billing_city = payRes.get("billing_city") == null ? "" : payRes.get("billing_city").toString();
		    		String billing_state = payRes.get("billing_state") == null ? "" : payRes.get("billing_state").toString();
		    		String billing_zip = payRes.get("billing_zip") == null ? "" : payRes.get("billing_zip").toString();
		    		String billing_country = payRes.get("billing_country") == null ? "" : payRes.get("billing_country").toString();
		    		String billing_tel = payRes.get("billing_tel") == null ? "" : payRes.get("billing_tel").toString();
		    		String billing_email = payRes.get("billing_city") == null ? "" : payRes.get("billing_email").toString();
		    		String vault = payRes.get("vault") == null ? "" : payRes.get("vault").toString();
		    		String mer_amount = payRes.get("mer_amount") == null ? "" : payRes.get("mer_amount").toString();
		    		String trans_date = payRes.get("trans_date") == null ? "" : payRes.get("trans_date").toString();
		    		String customer_card_id = payRes.get("customer_card_id") == null ? "" : payRes.get("customer_card_id").toString();
		    		String customer_identifier = payRes.get("customer_identifier") == null ? "" : payRes.get("customer_identifier").toString();
		    		String merchant_param6 = payRes.get("merchant_param6") == null ? "" : payRes.get("merchant_param6").toString();
		    		String merchant_param7 = payRes.get("merchant_param7") == null ? "" : payRes.get("merchant_param7").toString();
		    		
		    		PaymentDetail payDet = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(merchant, "PENDING");
		    		
		    		if(payDet != null) {
		    			 quoteNo = payDet.getQuoteNo();
		    			payDet.setReqReferenceNumber(tracking_id);
		    			payDet.setBankCode(bank_ref_no);
		    			payDet.setChannel(card_name);
		    			payDet.setReference(trans_date);
		    			payDet.setPayeeName(billing_name);
		    			payDet.setCbcNo(customer_card_id);
		    			payDet.setReqCardNumber(merchant_param6);
		    			payDet.setReqCardExpiryDate(merchant_param7);
		    			
		    			if("Approved".equalsIgnoreCase(status_message) && "Success".equalsIgnoreCase(order_status)) {
		    				
		    				Double vendorAmount = StringUtils.isNotBlank(mer_amount) ? Double.valueOf(mer_amount) : null;
						
		    				Double actualAmount  = Double.valueOf(payDet.getPremium().toString());
		    				
		    				if(vendorAmount.equals(actualAmount)) {
		    					payDet.setAuthResponse("Success");
		    					
		    					resCheck = true;
		    				}else if(!vendorAmount.equals(actualAmount)) {
		    					payDet.setAuthResponse("PAYMENT_MISMATCH");
		    					resCheck = true;
		    				}else {
		    					payDet.setPaymentStatus("PAYMENT_MISMATCH");
		    					resCheck = true;
		    				}
		    				
		    				
		    			}else if(StringUtils.isNotBlank(failure_message)) {
		    				payDet.setAuthResponse(failure_message);
		    				resCheck= false;
		    			}else {
		    				payDet.setAuthResponse("FAILED");
		    				resCheck = false;
		    			}
		    			
		    			paymentDetailRepo.save(payDet);
		    		}else {
		    			System.out.println("MERCHANT NO NOT FOUND "+ merchant);
		    			
		    			resCheck = false;
		    		}
		    		
		    		
		    				
		    	}else {
		    		System.out.println("ENCRYPTED RES NOT FOUND ");
	    			resCheck = false;
		    	}
		    }else {
		    	System.out.println("ENCRYPTED RES NOT FOUND ");
    			resCheck = false;
		    }
		    
		    res.put("QuoteNo", quoteNo);
		    res.put("ReturnUrl", returnUrl);
		    res.put("Check", resCheck);
		    
		    return res;
		}catch(Exception e) {
			e.printStackTrace();
			resCheck = false;
			res.put("QuoteNo", null);
		    res.put("ReturnUrl", null);
		    res.put("Check", resCheck);
			
		}
		return res;
	}
	
	public JsonObject cyberSourcePaymentDetails(PaymentVendorMaster vendor, PaymentDetail payment, PaymentInfo paymentInfo) {
	    JsonObject jsonResponse = new JsonObject();

	    try {
	    	
	        String signature;
	        List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
			String castAmountValue;
			
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				castAmountValue =  payment.getPremiumLc().toPlainString();
			
			else
				castAmountValue=  payment.getPremiumFc().toPlainString();						        	        
	        
	        if (payment != null) {
	            paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());

	            String userType = paymentInfo.getSubUserType().equalsIgnoreCase("b2c") ? "b2c" : "b2b";

	        if ("cybersource".equalsIgnoreCase(vendor.getVendorName())) {

	                Map<String, String> params = new LinkedHashMap<>();

	                params.put("access_key", vendor.getApiKey());
	                params.put("amount", castAmountValue);
	                params.put("currency",  payment.getCurrencyId());
//	                params.put("currency",  "BWP");
	                params.put("locale", "en");
	                params.put("profile_id", vendor.getRemarks());
	                params.put("reference_number", payment.getMerchantReference());
	                
	                ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	                String signedDateTime = utcTime.format(formatter);
	                params.put("signed_date_time", signedDateTime);
	                
	                String signedFields = "access_key,amount,currency,locale,profile_id,reference_number,signed_date_time,signed_field_names,transaction_type,transaction_uuid,unsigned_field_names";
	                params.put("signed_field_names", signedFields);
	                
//	                params.put("signed_field_names", vendor.getSignedFields());
//	                params.put("unsigned_field_names", vendor.getUnsignedFields());
	             
//	                params.put("transaction_type", "authorization");
	                params.put("transaction_type", "sale");

	                params.put("transaction_uuid", payment.getMerchantReference());
	                params.put("unsigned_field_names", "");		                
	               
	                		             
	                String preSignatureRequestBody = params.entrySet().stream()
	                        .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(","));

//	                StringBuilder dataToSign = new StringBuilder();
//	                for (String field : signedFields.split(",")) {
//	                    String value = params.get(field) != null ? params.get(field).trim() : "";
//	                    dataToSign.append(field.trim()).append(value);
//	                }
	                 
	                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
	                SecretKeySpec secretKey = new SecretKeySpec(vendor.getApiSecretKey().getBytes("UTF-8"), "HmacSHA256");
	                sha256_HMAC.init(secretKey);
	                byte[] hash = sha256_HMAC.doFinal(preSignatureRequestBody.toString().getBytes("UTF-8"));
	                signature = Base64.getEncoder().encodeToString(hash);
	         

	                params.put("signature", signature);
	                
//	                params.put("merchant_id", "absa_phoenix_1049048_bwtestmid"); //test
	                params.put("merchant_id", "absa_phoenix_1049048_bw"); //live
	                
	                params.put("payment_method", "card");

	                String requestBody = params.entrySet().stream()
	                        .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(","));

	                JsonObject paramsJson = new JsonObject();
	                for (Map.Entry<String, String> entry : params.entrySet()) {
	                    paramsJson.addProperty(entry.getKey(), entry.getValue());
	                }

	                JsonArray dataArray = new JsonArray();
	                dataArray.add(paramsJson);

	                jsonResponse.addProperty("result", "SUCCESS");
	                jsonResponse.add("data", dataArray);

	                return jsonResponse;

	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "redirectUrl not found in response");
            jsonResponse.addProperty("result", "ERROR");

	    }

	    return jsonResponse;
	}
	
}