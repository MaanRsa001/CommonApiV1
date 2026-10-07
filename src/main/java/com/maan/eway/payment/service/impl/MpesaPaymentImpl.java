package com.maan.eway.payment.service.impl; 

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.Cipher;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentDetailId;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.bean.mpesa.MpesaRequest;
import com.maan.eway.bean.mpesa.MtnPaymentRequest;
import com.maan.eway.bean.mpesa.PayerRequest;
import com.maan.eway.payment.service.MpesaPaymentService;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;

@Service
public class MpesaPaymentImpl implements MpesaPaymentService{
	
	@Autowired
	private PaymentDetailRepository paymentDetailRepo;
	
	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo ;

	@Override
	public JsonObject payment(PaymentDetail payment, PaymentVendorMaster vendor) {
		System.out.println("IN SERVICE");
		String accessToken = null;
		JsonObject outputResponse = new JsonObject();
		try {
			String publickey = vendor.getApiSecretKey();
			String apikey = vendor.getApiKey();

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Cipher cipher = Cipher.getInstance("RSA");
			byte[] encodedPublicKey = org.apache.commons.codec.binary.Base64.decodeBase64(publickey);
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
			PublicKey pk = keyFactory.generatePublic(publicKeySpec);

			cipher.init(Cipher.ENCRYPT_MODE, pk);
			byte[] encryptedApiKey = org.apache.commons.codec.binary.Base64.encodeBase64(cipher.doFinal(apikey.getBytes("UTF-8")));

			accessToken = new String(encryptedApiKey, "UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			String url = vendor.getApiBaseUrl()+vendor.getPaymentUrlLink();
			System.out.println(url);
	        HttpPost httpPost = new HttpPost(url);

	        httpPost.setHeader("Content-Type", "application/json");
	        httpPost.setHeader("Authorization", "Bearer " + accessToken);
	        httpPost.setHeader("Origin", "*");
	        
	        ObjectMapper mapper = new ObjectMapper();
	        
	        MpesaRequest mpesaRequest = new MpesaRequest();
	        
	        String inputValues = vendor.getRemarks();
	        
	        String[] inputs = inputValues.split(",");
//	        System.out.println(inputs.length);
	        
//	        String serviceProviderCode = null;
//	        String thirdPartyReference = null;
	        
	        String[] serviceProviderInputs = inputs[0].split("=");
	        String[] thirdPartyRefInputs = inputs[1].split("=");
	        
	        System.out.println(serviceProviderInputs.length + "*" + thirdPartyRefInputs.length);
	        System.out.println(serviceProviderInputs[1] + "*" + thirdPartyRefInputs[1] + "*" +payment.getMerchantReference());
	        
	        
	       // mpesaRequest.setInput_Amount(payment.getAuthAmount());
	        
			List<InsuranceCompanyMaster> insInfo = insuranceRepo
					.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),
							"Y", new Date(), new Date());
			/**if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				  mpesaRequest.setInput_Amount( payment.getPremiumLc().toPlainString());
			else
				  mpesaRequest.setInput_Amount(payment.getPremiumFc().toPlainString());**/
			mpesaRequest.setInput_Amount( "1");

		//	orderDict.addProperty("currency",payment.getCurrencyId());
	        
	        
	        mpesaRequest.setInput_CustomerMSISDN(payment.getReqBillToPhone());
	        mpesaRequest.setInput_ServiceProviderCode(serviceProviderInputs[1]);
//	        mpesaRequest.setInput_ThirdPartyReference(thirdPartyRefInputs[1]);
//	        mpesaRequest.setInput_TransactionReference(payment.getMerchantReference());
	        mpesaRequest.setInput_ThirdPartyReference(payment.getMerchantReference());
			mpesaRequest.setInput_TransactionReference(payment.getMerchantReference());
	        String jsonPayload = mapper.writeValueAsString(mpesaRequest);
	        System.out.println(jsonPayload);
	        httpPost.setEntity(new StringEntity(jsonPayload));
	        
	        try (CloseableHttpResponse response = client.execute(httpPost)) {
	        	
	            HttpEntity entity = response.getEntity();
	            JSONObject jsonResponse = new JSONObject();
	            if (entity != null) {
	                String responseBody = EntityUtils.toString(entity, "UTF-8");
	                jsonResponse = new JSONObject(responseBody);
	                System.out.println("Response Body: " + responseBody);
	            }
	        	
	        	System.out.println(response);
	            System.out.println("Response Code: " + response.getCode());
	            if(response.getCode()==201 || response.getCode()==200) {
	            	outputResponse.addProperty("result", "Success");
	            	outputResponse.addProperty("Message", "Request Processed Successfully");
	            	System.out.println("SUCCESS");
	            	
	            	PaymentDetailId id = new PaymentDetailId();
	            	id.setMerchantReference(payment.getMerchantReference());
	            	id.setPaymentId(payment.getPaymentId());
	            	id.setQuoteNo(payment.getQuoteNo());
	            	
	            	Optional<PaymentDetail> existing = paymentDetailRepo.findById(id);
	            	if (existing.isPresent()) {
	            	    System.out.println("Merchant Reference Found: " + existing.get().getMerchantReference());
	            	} else {
	            	    System.out.println("Merchant Reference NOT found!");
	            	}

	            	String trimmedRef = payment.getMerchantReference().trim();
	            	paymentDetailRepo.updatePaymentDetail(jsonResponse.getString("output_ConversationID"), trimmedRef);
	            	
	            	
	            	
	            }else {
	            	System.out.println("FAIL");
	            	outputResponse.addProperty("result", "Fail");
	            	outputResponse.addProperty("Message", "Request Process Failure");
	            }
	        }
		}catch(Exception e) {
			e.getMessage();
		}
		return outputResponse;
	}

	@Override
	public JsonObject orderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		System.out.println("IN SERVICE");
		String accessToken = null;
		JsonObject outputResponse = new JsonObject();
		try {
			String publickey = vendor.getApiSecretKey();
			String apikey = vendor.getApiKey();

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Cipher cipher = Cipher.getInstance("RSA");
			byte[] encodedPublicKey = org.apache.commons.codec.binary.Base64.decodeBase64(publickey);
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
			PublicKey pk = keyFactory.generatePublic(publicKeySpec);

			cipher.init(Cipher.ENCRYPT_MODE, pk);
			byte[] encryptedApiKey = org.apache.commons.codec.binary.Base64.encodeBase64(cipher.doFinal(apikey.getBytes("UTF-8")));

			accessToken = new String(encryptedApiKey, "UTF-8");

		} catch (Exception e) {
			e.getMessage();
		}
		
//		System.out.println(accessToken);
        String inputValues = vendor.getRemarks();
		 String[] inputs = inputValues.split(",");
		 
	        String[] serviceProviderInputs = inputs[0].split("=");
	        String[] thirdPartyRefInputs = inputs[1].split("=");
		try {
			CloseableHttpClient client = HttpClients.createDefault();
//String url = "https://api.sandbox.vm.co.mz:18353/ipg/v1x/queryTransactionStatus/?input_ThirdPartyReference="+thirdPartyRefInputs[1]+"&input_QueryReference="+payment.getReference()+"&input_ServiceProviderCode="+serviceProviderInputs[1];
			
			String url = "https://api.vm.co.mz:18353/ipg/v1x/queryTransactionStatus/?input_ThirdPartyReference="+payment.getMerchantReference()+"&input_QueryReference="+payment.getMerchantReference()+"&input_ServiceProviderCode="+serviceProviderInputs[1];
	        System.out.println("Mpesa Status Payload----"+url);
			HttpGet httpPost = new HttpGet(url);

	        httpPost.setHeader("Content-Type", "application/json");
	        httpPost.setHeader("Authorization", "Bearer " + accessToken);
	        httpPost.setHeader("Origin", "*");
	        
	        ObjectMapper mapper = new ObjectMapper();
	        
//	        MpesaRequest mpesaRequest = new MpesaRequest();
//	        String jsonPayload = mapper.writeValueAsString(mpesaRequest);
//	        System.out.println(jsonPayload);
//	        httpPost.setEntity(new StringEntity(jsonPayload));
	        
	        try (CloseableHttpResponse response = client.execute(httpPost)) {
	        	
	            HttpEntity entity = response.getEntity();
	            JSONObject jsonResponse = new JSONObject();
	            if (entity != null) {
	                String responseBody = EntityUtils.toString(entity, "UTF-8");
	                jsonResponse = new JSONObject(responseBody);
//	                System.out.println("Response Body: " + responseBody);
	            }
	        	
	        	System.out.println(jsonResponse);
//	            System.out.println("Response Code: " + response.getCode());
	            if(response.getCode()==201 || response.getCode()==200) {
	            	System.out.println("SUCCESS");
	            	outputResponse.addProperty("Status", "Success");
	            	outputResponse.addProperty("Message", "Request Processed Successfully");
	            	if(jsonResponse.getString("output_ResponseTransactionStatus").equals("Completed")) {
	            		payment.setPaymentStatus("ACCEPTED");
	            		paymentDetailRepo.save(payment);
	            	}else if(jsonResponse.getString("output_ResponseTransactionStatus").equals("Cancelled")) {
	            		payment.setPaymentStatus("FAILED");
	            		paymentDetailRepo.save(payment);
	            	}else if(jsonResponse.getString("output_ResponseTransactionStatus").equals("Expired")) {
	            		payment.setPaymentStatus("PENDING");
	            		paymentDetailRepo.save(payment);
	            	}
	            }else {
	            	System.out.println("FAIL");
	            	outputResponse.addProperty("Status", "Failure");
	            	outputResponse.addProperty("Message", "Request Process Failure");
	            }
	        }
		}catch(Exception e) {
			e.getMessage();
		}
		return outputResponse;
	}

	@Override
	public JsonObject mtnPayment(PaymentDetail payment, PaymentVendorMaster vendor) {
		JsonObject outputResponse = new JsonObject();
		

		JSONObject response = new JSONObject();
		try {
			String url = "https://sandbox.momodeveloper.mtn.com/collection/token/";

			String username = "12f1a298-50d7-4b29-aa79-4f175224dc47";
			String password = "f9f4debcdbd640ee826efa8ce6792126";

			String auth = username + ":" + password;
			String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
			String authHeader = "Basic " + encodedAuth;
			HttpHeaders headers = new HttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Ocp-Apim-Subscription-Key", vendor.getApiSecretKey());
			headers.set("Authorization", authHeader);
			org.springframework.http.HttpEntity<String> requestEntity = new org.springframework.http.HttpEntity<>(headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
			response = new JSONObject(responseEntity.getBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String url = vendor.getApiBaseUrl() + vendor.getPaymentUrlLink();

			HttpHeaders headers = new HttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Ocp-Apim-Subscription-Key", vendor.getApiSecretKey());
			headers.set("Authorization", "Bearer " + response.get("access_token"));
			headers.set("X-Target-Environment", "sandbox");
			headers.set("X-Reference-Id", payment.getMerchantReference().toString());
			System.out.println(url);
			MtnPaymentRequest paymentRequest = new MtnPaymentRequest();
			List<InsuranceCompanyMaster> insInfo = insuranceRepo
					.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),
							"Y", new Date(), new Date());
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				paymentRequest.setAmount( payment.getPremiumLc().toPlainString());
			else
				paymentRequest.setAmount(payment.getPremiumFc().toPlainString());
			paymentRequest.setAmount("10");
			paymentRequest.setExternalId("12876543");
			paymentRequest.setCurrency(payment.getCurrencyId());
			PayerRequest payer = new PayerRequest();
			payer.setPartyId(payment.getReqBillToPhone());
			payer.setPartyIdType(vendor.getRemarks());
			
			paymentRequest.setPayer(payer);
			
			org.springframework.http.HttpEntity<MtnPaymentRequest> requestEntity = new org.springframework.http.HttpEntity<>(paymentRequest, headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
			
			if(responseEntity.getStatusCode().value() == 202) {
				outputResponse.addProperty("Status", "Success");
				outputResponse.addProperty("Message", "Payment Initiated Successfully");
				return outputResponse;
			}else {
				outputResponse.addProperty("Status", "Failure");
				outputResponse.addProperty("Message", "Payment Cannot be Initiated");
				return outputResponse;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			outputResponse.addProperty("Status", "Failure");
			outputResponse.addProperty("Message", e.getMessage());
			return outputResponse;
		}
	}

	@Override
	public JsonObject mtnOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		JsonObject outputResponse = new JsonObject();

		JSONObject response = new JSONObject();
		try {
			String url = "https://sandbox.momodeveloper.mtn.com/collection/token/";

			String username = "12f1a298-50d7-4b29-aa79-4f175224dc47";
			String password = "f9f4debcdbd640ee826efa8ce6792126";

			String auth = username + ":" + password;
			String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
			String authHeader = "Basic " + encodedAuth;
			HttpHeaders headers = new HttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Ocp-Apim-Subscription-Key", vendor.getApiSecretKey());
			headers.set("Authorization", authHeader);
			org.springframework.http.HttpEntity<String> requestEntity = new org.springframework.http.HttpEntity<>(
					headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
			response = new JSONObject(responseEntity.getBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String url = vendor.getCheckStatusUrl() + payment.getMerchantReference();
			System.out.println(vendor.getApiSecretKey());
			HttpHeaders headers = new HttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Ocp-Apim-Subscription-Key", vendor.getApiSecretKey());
			headers.set("Authorization", "Bearer " + response.get("access_token"));
			headers.set("X-Target-Environment", "sandbox");
			System.out.println(url);
			
			org.springframework.http.HttpEntity<MtnPaymentRequest> requestEntity = new org.springframework.http.HttpEntity<>(headers);
			
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
			
			String responseBody = responseEntity.getBody();
			
			JSONObject jsonResponse = new JSONObject();

			if (responseBody != null) {
			    jsonResponse = new JSONObject(responseBody);
			    System.out.println("JSON Response: " + jsonResponse);
			} else {
			    System.out.println("Response body is null");
			}
			
			if(jsonResponse.has("status") && jsonResponse.get("status").equals("SUCCESSFUL")) {
        		payment.setPaymentStatus("ACCEPTED");
        		paymentDetailRepo.save(payment);
				outputResponse.addProperty("Status", "Success");
				outputResponse.addProperty("Message", "Payment Completed Successfully");
				return outputResponse;
			}else {
        		payment.setPaymentStatus("FAILED");
        		paymentDetailRepo.save(payment);
				outputResponse.addProperty("Status", "Failure");
				outputResponse.addProperty("Message", "Payment Not Completed");
				return outputResponse;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			outputResponse.addProperty("Status", "Failure");
			outputResponse.addProperty("Message", e.getMessage());
			return outputResponse;
		}
	}
}