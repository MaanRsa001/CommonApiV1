package com.maan.eway.payment;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.common.req.CybersourceReq;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.payment.service.SelcomPaymentService;
import com.maan.eway.payment.service.impl.SelcomPaymentImpl;
import com.maan.eway.repository.PaymentDetailRepository;

import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/selcom")
public class SelcomPaymentController {
	@Autowired
	private SelcomPaymentService service;
	
	private Logger log = LogManager.getLogger(SelcomPaymentController.class);
	
	@Autowired
	private PaymentDetailRepository paymentDetailRepo;
	
	@PostMapping("/v1/checkout/create-order/{merchantRefernceNo}")
	@ApiOperation(value = "This method is to Payment Sava")
	//@RequestBody
	public ResponseEntity<Object> createOrder(@PathVariable("merchantRefernceNo") String merchantRefernceNo) {
		JsonObject data =service.createOrderForPayment(merchantRefernceNo);
		if (data != null) {
			return new ResponseEntity<Object>(data.toString(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}	
	}
	
	
	@PostMapping("/v1/checkout/webhook")
	@ApiOperation(value = "This method is to Payment Sava")
	//@RequestBody
	public ResponseEntity<Map<String,Object>> methodWebhook(@RequestBody Map<String,Object> jsObject) {
		/*new Runnable() {
			
			@Override
			public void run() {
				
			}
		};*/
		if(jsObject.get("OrderMerchantReference")!=null && StringUtils.isNotBlank(jsObject.get("OrderMerchantReference").toString()) )
			jsObject.put("order_id", jsObject.get("OrderMerchantReference"));
		
		service.methodWebhook(jsObject);
		jsObject.put("AcknowledegeStatus", true);
		return new ResponseEntity<Map<String,Object>>(jsObject, HttpStatus.CREATED);	
	}
	
	@PostMapping("/v1/checkout/lipilawebhook")
	@ApiOperation(value = "This method is to Payment Sava")
	//@RequestBody
	public ResponseEntity<Map<String,Object>> lipilamethodWebhook(@RequestBody Map<String,Object> jsObject) {
		/*new Runnable() {
			
			@Override
			public void run() {
				
			}
		};*/
		if(jsObject.get("OrderMerchantReference")!=null && StringUtils.isNotBlank(jsObject.get("OrderMerchantReference").toString()) )
			jsObject.put("order_id", jsObject.get("OrderMerchantReference"));
		
		service.lipilamethodWebhook(jsObject);
		jsObject.put("AcknowledegeStatus", true);
		return new ResponseEntity<Map<String,Object>>(jsObject, HttpStatus.CREATED);	
	}
	

	
	
	@PostMapping("/v1/checkout/order-status/{orderId}")
	@ApiOperation(value = "This method is to Payment ")
	public ResponseEntity<Object> orderStatus(@PathVariable("orderId") String orderId,@RequestHeader("Authorization") String tokens) {
		JsonObject data = service.orderStatus(orderId,tokens);
		if (data != null) {
			return new ResponseEntity<Object>(data.toString(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/v1/checkout/create-order-minim/{merchantRefernceNo}")
	@ApiOperation(value = "This method is to Payment Sava")
	//@RequestBody
	public ResponseEntity<Object> createOrderMinimum(@PathVariable("merchantRefernceNo") String merchantRefernceNo) {
		
		JsonObject data =service.createOrderMinimal(merchantRefernceNo);
		if (data == null) {
	        JsonObject response = new JsonObject();
	        response.addProperty("result", "ERROR");
	        response.addProperty("message", "No response received from payment service");
	        return ResponseEntity.ok(response.toString());
	    }

	    return ResponseEntity.ok(data.toString());	
	}
	
	@PostMapping("/v1/checkout/order-cancel/{orderId}")
	@ApiOperation(value = "This method is to Payment ") 
	public ResponseEntity<Object> ordercancel(@PathVariable("merchantRefernceNo") String merchantRefernceNo) {
		JsonObject data = service.orderCancel(merchantRefernceNo);
		if (data != null) {
			return new ResponseEntity<Object>(data.toString(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@PostMapping("/v1/checkout/cybersourceWebhook")
    public RedirectView handleCyberSourceWebhook(
        @RequestParam MultiValueMap<String, String> formData) {
        return service.handleCyberSourceRedirect(formData);
    }
	
	@PostMapping("/v1/checkout/cybersourceorderStatusminimal")
	@ApiOperation(value = "This method is to Payment ") 
	public ResponseEntity<Object> cybersourceorderStatusminimal(@RequestBody CybersourceReq req, @RequestHeader("Authorization") String tokens) {
		JsonObject data = service.cybersourceorderStatusminimal(req.getQuoteId(), req.getPaymentId(), tokens);
		if (data != null) {
			return new ResponseEntity<Object>(data.toString(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/v1/RSTAIntegeration/{quoteNo}")
	@ApiOperation(value = "This method is to Integeration with RSTA")
	public void callRSTAIntegeration(@PathVariable("quoteNo") String quoteNo){
		service.callRSTAIntegeration(quoteNo);
	}
	
	@PostMapping("/check/paymentdetails")
	@ApiOperation(value = "This method is to Check the payment details with quoteNo or merchantNo")
	public ResponseEntity<CommonRes> checkPaymentDetails(@RequestBody PaymentDetailsGetReq req){
		CommonRes data = new CommonRes();
		
		data = service.checkPaymentDetails(req);
		
		if(data.getIsError() == false) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}else {
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
	}
	
	@PostMapping("/oman/checkout")
	   public Object handlePayFortSourceWebhook(@RequestBody Object req,@RequestHeader("Authorization") String tokens) {
		   return service.handleSmartPaySourceWebhook(req,tokens);
	   }
	
	
	@PostMapping(value = "/oman/payment-return",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public void OmanPaymentReturn(@RequestParam MultiValueMap<String, String> req,HttpServletResponse response) throws IOException {
		
		req.forEach((key, value) -> {
	        log.info("SALAMA_PAYMENT_RETURN | {} = {}", key, value);
	    });
		
		Map<String,Object>  res = service.checkOmanPayment(req);
		 boolean check = res.get("Check") == null ? false : (boolean) res.get("Check");
		 String quoteNo = res.get("QuoteNo") == null ? "" :  res.get("QuoteNo").toString();
		 String returnUrl = res.get("ReturnUrl") == null ? "" :  res.get("ReturnUrl").toString();
		if(check) {
			returnUrl=returnUrl.replace("<QuoteNo>", quoteNo);
	    	returnUrl=returnUrl.replace("<type>", "success");
		}else {
			returnUrl=returnUrl.replace("<QuoteNo>", quoteNo);
	    	returnUrl=returnUrl.replace("<type>", "cancel");
		}
		
		response.sendRedirect(returnUrl);

	}
	

	
}
