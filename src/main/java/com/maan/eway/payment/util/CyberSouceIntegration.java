package com.maan.eway.payment.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentVendorMaster;

public class CyberSouceIntegration {

	private static final String ALGO = "AES";
	private static final String	UNICODE_FORMAT= "UTF-8";
	private static final String AES_KEY = "1234567812345675";
	private static final String HMAC_SHA256 = "HmacSHA256";
	
	public JsonObject createPay(PaymentVendorMaster vendor, PaymentDetail payment, String castAmountValue ) {
		JsonObject jsonResponse = new JsonObject();
		String signature;
		try {
			HashMap<String,String> params = new HashMap<String,String>();		 
			
			params.put("amount", castAmountValue);
			params.put("reference_number", payment.getMerchantReference());
			params.put("signed_field_names", vendor.getSignedFields());
			params.put("profile_id", vendor.getRemarks());
			params.put("signed_date_time", new Date().toString());
			params.put("transaction_type", "sale");
			params.put("locale", "en");
			params.put("transaction_uuid", payment.getMerchantReference());
			params.put("access_key", decrypt(vendor.getApiKey()));
			params.put("unsigned_field_names", vendor.getUnsignedFields());
			params.put("currency", payment.getCurrencyId());

			params.put("recurring_amount", null);
			params.put("recurring_frequency", null);
			params.put("recurring_start_date", null);
			params.put("recurring_number_of_installments", null);
			params.put("recurring_automatic_renew", null);
			
			params.put("bill_to_forename", payment.getReqBillToForename());
			params.put("bill_to_surname", payment.getReqBillToSurname());
			params.put("bill_to_address_line1", payment.getReqBillToAddressLine1());
			params.put("bill_to_address_city", payment.getReqBillToAddressCity());
			params.put("bill_to_address_country", payment.getReqBillToCountry());
			params.put("bill_to_address_postal_code", payment.getReqBillToAddrPostalCode());
			params.put("bill_to_email", payment.getReqBillToEmail());
			
			signature = peachGenerateSignature(params, vendor.getApiSecretKey());	
			
			params.put("signature", signature);
			params.put("shopperResultUrl",URLEncoder.encode( vendor.getReturnUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()), StandardCharsets.UTF_8.toString()) );//URLEncoder.encode(, StandardCharsets.UTF_8.toString()) );	
			params.put("cancelUrl", URLEncoder.encode(vendor.getCancelUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()), StandardCharsets.UTF_8.toString()));
			params.put("notificationUrl", URLEncoder.encode(vendor.getWebhookUrlLink(), StandardCharsets.UTF_8.toString()));
			String requestBody = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
					.collect(Collectors.joining("&"));
			System.out.println(" checkOut Request Body: " + requestBody);

			try (CloseableHttpClient client = HttpClients.createDefault()) {
				HttpPost httpPost = new HttpPost(vendor.getPaymentUrlLink());
				httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httpPost.setEntity(new StringEntity(requestBody));

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
				return jsonResponse;
			}catch (Exception e) {
				 e.printStackTrace();
				 
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
		public String decrypt(String encryptedData) throws Exception {
			SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8),ALGO);
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
			byte[] decValue = c.doFinal(decordedValue);
			return new String(decValue, StandardCharsets.UTF_8);
		}
		
		private String peachGenerateSignature(HashMap<String,String> params,String secretKey) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
	        return sign(buildDataToSign(params), secretKey);
	    }
		
		private String sign(String data, String secretKey) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
	        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
	        Mac mac = Mac.getInstance(HMAC_SHA256);
	        mac.init(secretKeySpec);
	        byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));
	        return DatatypeConverter.printBase64Binary(rawHmac).replace("\n", "");
	    }
		
		private String buildDataToSign(HashMap<String,String> params) {
	        String[] signedFieldNames = String.valueOf(params.get("signed_field_names")).split(",");
	        ArrayList<String> dataToSign = new ArrayList<String>();
	        for (String signedFieldName : signedFieldNames) {
	            dataToSign.add(signedFieldName + "=" + String.valueOf(params.get(signedFieldName)));
	        }
	        return commaSeparate(dataToSign);
	    }
		
		 private String commaSeparate(ArrayList<String> dataToSign) {
		        StringBuilder csv = new StringBuilder();
		        for (Iterator<String> it = dataToSign.iterator(); it.hasNext(); ) {
		            csv.append(it.next());
		            if (it.hasNext()) {
		                csv.append(",");
		            }
		        }
		        return csv.toString();
		    }

}

	
