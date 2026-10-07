package com.maan.eway.common.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.maan.eway.claim.service.serviceImpl.ClaimCustomerDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.bean.EserviceCustomerLocation;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.req.CustomerChangesSaveReq;
import com.maan.eway.common.req.CustomerDetailsReq;
import com.maan.eway.common.req.CustomerLocationReq;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetByCustomerRefNoReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.InsuredDetailsGetRes;
import com.maan.eway.common.service.EserviceCustomerDetailsService;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
@Api(tags = "3. COMMON : Eservice Customer Details ", description = "API's")
public class EserviceCustomerDetailsController {

	
	@Autowired 
	private EserviceCustomerDetailsService entityService ; 
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;

	@Autowired
	private ClaimCustomerDetailsServiceImpl claimCustomerDetailsService;
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/savecustomerdetails")
	public ResponseEntity<CommonRes> saveCustomerDetails(@RequestBody EserviceCustomerSaveReq req) {
		
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = new ArrayList<>();
		validationCodes = entityService.validateCustomerDetails(req);
		List<Error> validation = null;
		if (validationCodes != null && !validationCodes.isEmpty()) {

			if (validationCodes.contains("ID_NUMBER_EXISTS") || validationCodes.contains("EMAIL_EXISTS")
					|| validationCodes.contains("MOBILE_EXISTS")) {
				return entityService.getExistingCustomerDetails(req, validationCodes);

			}

			
			else {
				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(req.getCompanyId());
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				validation = errorDescService.getErrorDesc(validationCodes, comErrDescReq);
			}
		}
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			SuccessRes res = entityService.saveCustomerDetails(req);
            if (req.getCustomerReferenceNo() != null) {
				claimCustomerDetailsService.updateClaimCustomerFromEservice(req.getCustomerReferenceNo());
            }
            //entityService.financeIntegrationCustomerCreation(req, res);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if (res != null) {
				// Sending Welcome Email !
				//customerWelcomeEmail.customerWelcomeEmail(req);
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	}

	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/saveinsureddetails")
	public ResponseEntity<CommonRes> saveInsuredDetails(@RequestBody  EserviceCustomerSaveReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = new ArrayList<>();
		 validationCodes = entityService.validateInsuredDetails(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getCompanyId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("1");
			comErrDescReq.setModuleName("CUSTOMER CREATION");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			SuccessRes res = entityService.saveInsuredDetails(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
    } 
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/customer")
	public ResponseEntity<CommonRes> saveCustomerLess(@RequestBody  EserviceCustomerSaveReq req){

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = entityService.validateCustomerDetails(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
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

		} 
		else {
			/////// save
			//req.setDobOrRegDate(new Date());
			req.setOccupation("12");
//			req.setStateCode(null);
//			req.setCityCode(null);
//			req.setRegionCode(null);
			
			SuccessRes res = entityService.saveCustomerDetails(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
    } 
	
	
	// Get
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getcustomerdetails")
	public ResponseEntity<CommonRes> getMsPersonalInfo(@RequestBody GetCustomerDetailsReq req){
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	CustomerDetailsGetRes res = entityService.getCustomerDetails(req);
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
	
	// Get
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/getinsureddetails")
		public ResponseEntity<CommonRes> getInsuredInfo(@RequestBody GetCustomerDetailsReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		InsuredDetailsGetRes res = entityService.getInsuredDetails(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(data, HttpStatus.CREATED);
		}
		}
			
	//Getall
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getallcustomerdetails")
	public ResponseEntity<CommonRes> getallCustomerDetails(@RequestBody GetAllCustomerDetailsReq req){
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	List<CustomerDetailsGetRes> res = entityService.getallCustomerDetails(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if(res!=null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	}
	else {
	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);	
	}
	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getactivecustomerdetails")
	public ResponseEntity<CommonRes> getActiveCustomerDetails(@RequestBody GetAllCustomerDetailsReq req){
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	List<CustomerDetailsGetRes> res = entityService.getActiveCustomerDetails(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if(res!=null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	}
	else {
	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);	
	}
	}

	// Search by Vr Tin No
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/searchcustomerdata")
	public ResponseEntity<CommonRes> getbyvrtinno(@RequestBody EserviceCustomerSearchVrtinReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<CustomerDetailsGetRes> res = entityService.getbyvrtinno(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
	if(res!=null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	}
	else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	}
	
		//External Call Api To Update E service Customer Details and Personal Info
		@PostMapping("/updatebycustrefno")
		public ResponseEntity<CommonRes> updatebycustrefno(@RequestBody GetByCustomerRefNoReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		SuccessRes res = entityService.updatebycustrefno(req);
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
	
		
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/validatecustomerid", method = RequestMethod.GET)
		@ApiOperation(value = "Validate ID", notes = "validation based on individual and corporate")
		public CommonRes validateCustomerId(@RequestParam(value = "accounttype", required = true) String accountType,
				@RequestParam(value = "identifytype", required = true) String identifyType,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit,
				@RequestParam(value = "id", required = true) String customerId) {

			return entityService.validateCustomerId(accountType, identifyType, companyId, saveOrSubmit, customerId);

		}

		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/validateCustomerName", method = RequestMethod.GET)
		@ApiOperation(value = "Validate Customer Name", notes = "validate length , empty etc ")
		public CommonRes validateCustomerName(@RequestParam(value = "name", required = true) String customerName,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit) {

			return entityService.validateCustomerName(customerName,companyId , saveOrSubmit);

		}

		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/validateOccupation", method = RequestMethod.GET)
		@ApiOperation(value = "Validate Occupation And Other Occupation", notes = "validate length , empty etc")
		public CommonRes validateOccupationAndOtherOccupation(
				@RequestParam(value = "occupation", required = true) String occupation,
				@RequestParam(value = "otheroccupation", required = true) String OtherOccupation,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit) {

			return entityService.validateOccupationAndOtherOccupation(occupation, OtherOccupation, companyId,
					saveOrSubmit);

		}

		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/validateaddress", method = RequestMethod.GET)
		@ApiOperation(value = "Validate Address", notes = "validate length and empty")
		public CommonRes validateAddress(@RequestParam(value = "address", required = true) String address,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit) {

			return entityService.validateAddress(address, companyId, saveOrSubmit);

		}
		
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/validatedistrict", method = RequestMethod.GET)
		@ApiOperation(value = "Validate District", notes = "validation based on companies")
		public CommonRes validateDistrict(@RequestParam(value = "name", required = true) String cityName,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit) {

			return entityService.validateCityName(cityName ,companyId, saveOrSubmit);

		}
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/validatestatus", method = RequestMethod.GET)
		@ApiOperation(value = "Validate Status", notes = "validate length and empty etc")
		public CommonRes validateStatus(@RequestParam(value = "status", required = true) String status,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit) {

			return entityService.validateStatus(status ,companyId, saveOrSubmit);

		}

		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/validatemobilenumber", method = RequestMethod.GET)
		@ApiOperation(value = "Validate Mobile Number", notes = "validate length and empty etc")
		public CommonRes validateMobileNumber(@RequestParam(value = "number", required = true) String mobileNumber,
				@RequestParam(value = "code", required = true) String mobileCode,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit) {

			return entityService.validateMobileNumber(mobileNumber, mobileCode, companyId, saveOrSubmit);

		}
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/blankvalidation", method = RequestMethod.POST)
		@ApiOperation(value = "Validate Customer Creation Fields", notes = "validate empty")
		public CommonRes validateCustomerCreationFields(@RequestBody EserviceCustomerSaveReq req) {

			return entityService.validateCustomerCreationFields(req);

		}
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/Validatepincode", method = RequestMethod.GET)
		@ApiOperation(value = "Validate Pincode", notes = "validate length")
		public CommonRes validatePincode(@RequestParam(value =  "pincode" , required = true) String pinCode,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit) {

			return entityService.validatePincode(pinCode , companyId , saveOrSubmit);

		}
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/ValidateEmail", method = RequestMethod.GET)
		@ApiOperation(value = "Validate Email", notes = "validate length and format")
		public CommonRes validateEmail(@RequestParam(value =  "email" , required = true) String email,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit) {

			return entityService.validateEmail(email , companyId , saveOrSubmit);

		}

		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@RequestMapping(value = "/Validatdate", method = RequestMethod.GET)
		@ApiOperation(value = "Validate Date", notes = "validate date")
		public CommonRes validateDate(@RequestParam(value = "date", required = true) Date date,
				@RequestParam(value = "policyholdertype", required = true) String policyHolderType,
				@RequestParam(value = "idtype", required = true) String idType,
				@RequestParam(value = "companyid", required = true) String companyId,
				@RequestParam(value = "saveOrsubmit", required = true) String saveOrSubmit , 
				@RequestParam(value = "gender", required = false) String gender) {

			return entityService.validateDate(date,policyHolderType , idType ,  companyId, saveOrSubmit, gender);

		}

		@PostMapping(value = "/customerchanges")
		public ResponseEntity<CommonRes> customerChanges(@RequestBody CustomerChangesSaveReq  req) {
			CommonRes data = new CommonRes();
			// Validation
			List<Error> validation = entityService.validate(req);
			if (validation != null && validation.size() != 0) {

				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(validation);
				data.setMessage("Failed");

				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

			} else {
				// Save
				SuccessRes res = entityService.customerChanges(req);
				data.setCommonResponse(res);
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");

				if (res != null) {
					return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				} else {
					return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
				}
			}
	    }
		

		
		@RequestMapping(value = "/policydata", method = RequestMethod.GET)
		@ApiOperation(value = "Fetch Policy Data Based On Policy Number", notes = "Fetch Policy data")
		public CommonRes fetchPolicyData(@RequestParam(value = "policyno", required = true) String policyNumber) {

			return entityService.fetchPolicyData(policyNumber);

		}
		
		
		  @PostMapping("/saveMultipleCustomerLocation")
		    public CommonRes saveMultipleLocations(
		            @RequestBody List<EserviceCustomerLocation> locations) {
		        
		        CommonRes response = entityService.saveMultipleLocations(locations);

		        return response;	
		        }

		    @GetMapping("/getCustomerLocationDetail")
		    public CommonRes getLocationsByCustomerByReference(@RequestParam String customerRefNo) {
		    	List<EserviceCustomerLocation> locations=entityService.getLocationsByCustomerRefNo(customerRefNo);
		    	 CommonRes response=new CommonRes();
			        response.setCommonResponse(locations);
			        response.setErroCode(200);
			        response.setErrorMessage(null);
			        response.setMessage("Success");
			        
			        return response;
			        
		    }
		    
		    @PostMapping("/fetchCustomerQuoteAndPolicyDetails")
			public CommonRes fetchCustomerQuoteAndPolicyDetails(@RequestBody CustomerDetailsReq customerDetailsReq){
				return  entityService.fetchCustomerQuoteAndPolicyDetails(customerDetailsReq);
			}
		    
		    @PostMapping("/deleteLocation")
		    public CommonRes deleteLocation(@RequestBody CustomerLocationReq request) {
		        return entityService.deleteLocation(request.getLocationId(), request.getCustomerReferenceNo());
		    }

}
