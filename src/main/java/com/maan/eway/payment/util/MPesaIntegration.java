package com.maan.eway.payment.util;


public class MPesaIntegration {
	
	 
//	public APIResponse pushMobile(PaymentDetail payment, PaymentVendorMaster vendor,InsuranceCompanyMaster insuranceCompanyMaster) {
//
//		try {
//	        APIContext context = new APIContext();
//	        context.setApiKey(vendor.getApiKey());
//	        context.setPublicKey(vendor.getApiSecretKey());
//	        context.setSsl(false);
//			context.setMethodType(APIMethodType.POST);
//	        context.setAddress(vendor.getApiBaseUrl() );
//	        context.setPort(18352);
//	        context.setPath(vendor.getPaymentUrlLink());
//
//	        // Add Header
//	        context.addHeader("Origin", "*");
//
//	        // Set parameters used for the API
//			context.addParameter("input_TransactionReference",payment.getQuoteNo());
//			context.addParameter("input_CustomerMSISDN",payment.getWhatsappCode().concat(payment.getWhatsappNo()));
//			BigDecimal OurPremium=BigDecimal.ZERO;
//			if(insuranceCompanyMaster.getCurrencyId().equals(payment.getCurrencyId())) {
//				OurPremium = payment.getPremiumLc();
//			}else {
//				OurPremium = payment.getPremiumFc();
//			}
//			context.addParameter("input_Amount",OurPremium.toPlainString());
//			context.addParameter("input_ThirdPartyReference",payment.getMerchantReference());
//			context.addParameter("input_ServiceProviderCode","171717");//"258863084495");// mpesa account mobile number
//
//
//	        // Create API request and execute it.
//	        APIRequest request = new APIRequest(context);
//	        APIResponse response = request.execute();
//	        
//	        // Print results to the console
//	        System.out.println(":::: MPESA"+payment.getMerchantReference());
//	        if(response != null) {
//	            System.out.println(response.getStatusCode() + " - " + response.getReason());
//	            System.out.println(response.getResult());
//
//	            for(Map.Entry<String, String> entry: response.getParameters().entrySet()){
//	                System.out.println(entry.getKey() + ":" + response.getParameter(entry.getKey()));
//	            }
//	            
//	           return response;	
//	        }	        
//		}catch(Exception e) {
//			e.printStackTrace();
//		}	
//		return null;
//	}
//	public APIResponse mpesaOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
//		try {
//			APIContext context = new APIContext();
//			// Set API key that can be found in the user profile section
//			context.setApiKey(vendor.getApiKey());
//			// Set Public key that can be found in the user profile section
//			context.setPublicKey(vendor.getApiSecretKey());
//			// Set SSL true or false
//			context.setSsl(false);
//			// Set the method type of the HTTP Request (GET, POST, PUT)
//			context.setMethodType(APIMethodType.GET);
//			// Set the address of the API Server
//			context.setAddress(vendor.getApiBaseUrl());
//			// Set the TCP port of the API Server
//			context.setPort(18353);
//			// Set path for the API
//			context.setPath(vendor.getCheckStatusUrl());
//
//			// Add Header
//			context.addHeader("Origin", "*");
//
//			// Set parameters used for the API
//			context.addParameter("input_ThirdPartyReference",payment.getMerchantReference());
//			context.addParameter("input_QueryReference",payment.getReference());
//			context.addParameter("input_ServiceProviderCode","171717");//"258863084495");// mpesa account mobile number old one is "171717"
//
//
//			// Create API request and execute it.
//			APIRequest request = new APIRequest(context);
//			APIResponse response = request.execute();
//
//			// Print results to the console
//			if(response != null) {
//				System.out.println(response.getStatusCode() + " - " + response.getReason());
//				System.out.println(response.getResult());
//
//				for(Map.Entry<String, String> entry: response.getParameters().entrySet()){
//					System.out.println(entry.getKey() + ":" + response.getParameter(entry.getKey()));
//				}
//			}
//			return response;
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
 }
