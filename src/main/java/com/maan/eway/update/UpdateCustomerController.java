package com.maan.eway.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.UpdateCustomerDetailsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;

@RestController
@RequestMapping("/tira")
public class UpdateCustomerController {

	 @Autowired
	    private UpdateCustomerService customerService;  // Assumed service that handles business logic
	 
	 @Autowired
		private FetchErrorDescServiceImpl errorDescService ;

	    @PostMapping("/updatecustomer")
	    public ResponseEntity<SuccessRes> updateCustomerDetails(@RequestBody EserviceCustomerSaveReq req) {
	        // Call the service layer to update customer details
	        SuccessRes response = customerService.updateCustomerDetails(req);

	        // Return the response as a JSON object with HTTP status 200 (OK)
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	
	    
	    @PostMapping("/getpersonalinfo")
	    public ResponseEntity<CommonRes> getCustomerReferenceNo(@RequestBody GetCustomerReq req) {
	    	CommonRes data = new CommonRes();
	    	CustomerDetailsGetRes res = customerService.getCustomerReferenceNo(req);
	        
	    	data.setCommonResponse(res);
	    	data.setErrorMessage(Collections.emptyList());
	    	data.setIsError(false);
	    	data.setMessage("Success");
	    	if(res!=null) {
	    		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
	    	}
	    	else {
	    		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	    	}
	    }
	    
	    @PostMapping("/update/customerdetails")
	    public ResponseEntity<CommonRes> updateTiraCustomerDetails(@RequestBody UpdateCustomerDetailsReq req) {
	        // Call the service layer to update customer details
	    	
			CommonRes data = new CommonRes();
	    	List<String> validationCodes = new ArrayList<>();
			validationCodes = customerService.tiraValidateCustomerDetails(req);
			List<Error> validation = null;
			//// validation
			if (validationCodes != null && validationCodes.size() != 0) {
				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				
				//CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(req.getCompanyId());
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");
				
				validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);

			} 
			
			if (validation != null && validation.size() != 0) {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(validation);
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

			} else {
				/////// save
				SuccessRes res = customerService.updateTiraCustomerDetails(req);
				data.setCommonResponse(res);
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				if (res != null) {
					return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				} else {
					return new ResponseEntity<>(null, HttpStatus.OK);
				}
			}
	        
	    }
	    
	    @PostMapping("/getcustomerdetails")
	    public ResponseEntity<CommonRes> getTiraCustomerDetails(@RequestBody UpdateCustomerDetailsReq req){
	    	CommonRes data = new CommonRes();
	    	
	    	UpdateCustomerDetailsReq res = customerService.getTiraCustomerDetails(req);
	    	
	    	if(res != null) {
	    		data.setCommonResponse(res);
		    	data.setErrorMessage(Collections.emptyList());
		    	data.setIsError(false);
		    	data.setMessage("Success");
	    		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
	    	}else {
	    		data.setCommonResponse(Collections.emptyList());
		    	data.setErrorMessage(Collections.emptyList());
		    	data.setIsError(false);
		    	data.setMessage("Success");
		    	return new ResponseEntity<>(null, HttpStatus.OK);
	    	}
	    	
	    }
}
