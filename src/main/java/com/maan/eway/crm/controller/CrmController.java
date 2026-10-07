package com.maan.eway.crm.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.admin.req.BrokerLoginGridReq;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.crm.bean.CustomerDetail;
import com.maan.eway.crm.bean.CustomerLeadReq;
import com.maan.eway.crm.bean.QuoteReq;
import com.maan.eway.crm.bean.UserLoginResponseData;
import com.maan.eway.crm.service.CrmService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "CRM : Controller", description = "API.")
@RequestMapping("/crm")
public class CrmController {
	@Autowired
	private CrmService crmService;
	@Autowired
	private AuthendicationService authendicationService;

	
	@PostMapping("/authentication/validateToken")
	public UserLoginResponseData validateToken(@RequestBody Map<String, String> req) {
			  return crmService.validateTokenForCRM(req.get("token"));
	}


	@PostMapping("/productListByUserId")
	public List<ProductDropDownRes> productListByUserId(@RequestBody LoginRequest userData) {

		return crmService.getProductDetailByLoginId(userData.getLoginId(), userData.getCompanyId());
	}
	
	@GetMapping("/getEnqiryDetail/{enquiryId}")
	public ResponseEntity<CommonRes> getEnqiryDetail(@PathVariable Long enquiryId,
			@RequestHeader("Authorization") String token) {

		CommonRes data = new CommonRes();
		ObjectMapper mapper = new ObjectMapper();

		try {
			String response = crmService.getEnqiryDetail(enquiryId, token);

			Object jsonObj = mapper.readValue(response, Object.class);

			data.setCommonResponse(jsonObj);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			return new ResponseEntity<>(data, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/getCustomerDetail")
	public ResponseEntity<CommonRes> getCustomerDetailByLeadseqNo(@RequestBody CustomerLeadReq customerDetail,
	        @RequestHeader("Authorization") String token) {

	    CommonRes data = new CommonRes();
	    try {
	        List<EserviceCustomerDetails> customerDetailByLeadseqNo = crmService
	                .getCustomerDetailByLeadseqNo(customerDetail.getLeadSeqNo(), customerDetail.getCompanyId(), token);

	        int size = customerDetailByLeadseqNo.size();

	        // Create response body
	        Map<String, Object> response = new HashMap<>();
	        response.put("size", size);
	        response.put("data", customerDetailByLeadseqNo);

	        data.setCommonResponse(response);
	        data.setIsError(false);
	        data.setErrorMessage(Collections.emptyList());
	        data.setMessage("Success");

	        return new ResponseEntity<>(data, HttpStatus.OK);

	    } catch (Exception e) {
	        e.printStackTrace();
			data.setIsError(true);
			data.setMessage("Failed to fetch customer details");
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
		
	    }
	}
	
	 @PostMapping("/updateEnquiryQuotestatus")
		public  ResponseEntity<CommonRes> updateEnquiryQuotestatus(@RequestBody QuoteReq quoteReq) {

			return crmService.updateEnquiryQuotestatus(quoteReq);
		}
		
		@PostMapping("/fetchQuoteDetailByLeadSeqNo")
		public  ResponseEntity<CommonRes> fetchQuoteDetailByLeadSeqNo(@RequestBody QuoteReq quoteReq) {

			return crmService.fetchQuoteDetailByLeqdSeqNo(quoteReq.getLeadId(),quoteReq.getInsuranceId());
		}
		
		@PutMapping("/updateCustomerProductId")
		public  ResponseEntity<CommonRes> updateCustomerProductId(@RequestBody CustomerDetail customerDetailReq) {

			return crmService.updateCustomerProductId(customerDetailReq);
		}
		@PostMapping("/updateCRMEnquiryQuotestatus")
		public  ResponseEntity<CommonRes> updateCRMEnquiryQuotestatus(@RequestBody  CustomerDetail req,
				@RequestHeader("Authorization") String tokens) {

			return crmService.updateCRMEnquiryQuotestatus(req, tokens);
		}
		
		
	@PostMapping(value = "/dropdown/getApproverDropDownByClientId", produces = "application/json")
	@ApiOperation(value = "This method retrieves approver login IDs by client ID")
	public ResponseEntity<CommonRes> getApproverDropDownByClientId(@RequestBody BrokerLoginGridReq req) {
	    return crmService.getApproverDropDownByClientId(req);

	}

	@PostMapping("/productListByCompanyId")
	public ResponseEntity<CommonRes> productListByCompanyId(@RequestBody LoginRequest userData) {

		return crmService.getProductDetailByCompanyId(userData.getCompanyId());
	}

	@PostMapping("/getsectionDetailByProductIdandCompanyId")
	public ResponseEntity<CommonRes> getsectionDetailByProductIdandCompanyId(@RequestBody LoginRequest userData) {

		return crmService.getsectionDetailByProductIdandCompanyId(userData.getCompanyId(),
				Integer.valueOf(userData.getProductId()));
	}

}
