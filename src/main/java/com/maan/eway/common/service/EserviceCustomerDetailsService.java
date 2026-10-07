package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.maan.eway.bean.EserviceCustomerLocation;
import com.maan.eway.common.req.CustomerChangesSaveReq;
import com.maan.eway.common.req.CustomerDetailsReq;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetByCustomerRefNoReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.InsuredDetailsGetRes;
import com.maan.eway.error.Error;
import com.maan.eway.finanaceIntegration.req.CustomerCreationReq;
import com.maan.eway.res.SuccessRes;

public interface EserviceCustomerDetailsService {

	List<String> validateCustomerDetails(EserviceCustomerSaveReq req);
	
	List<String> validateInsuredDetails(EserviceCustomerSaveReq req);

	SuccessRes saveCustomerDetails(EserviceCustomerSaveReq req);
	
	SuccessRes saveInsuredDetails(EserviceCustomerSaveReq req);

	CustomerDetailsGetRes getCustomerDetails(GetCustomerDetailsReq req);
	
	InsuredDetailsGetRes getInsuredDetails(GetCustomerDetailsReq req);
	
	List<CustomerDetailsGetRes> getallCustomerDetails(GetAllCustomerDetailsReq req);

	List<CustomerDetailsGetRes>  getbyvrtinno(EserviceCustomerSearchVrtinReq req);

	List<CustomerDetailsGetRes> getActiveCustomerDetails(GetAllCustomerDetailsReq req);

	SuccessRes updatebycustrefno(GetByCustomerRefNoReq req);

	CommonRes validateCustomerId(String accountType, String identifyType, String companyId, String saveOrSubmit, String customerId);

	CommonRes validateCustomerName(String customerName,String companyId , String saveOrSubmit);

	CommonRes validateOccupationAndOtherOccupation(String occupation, String otherOccupation, String companyId,
			String saveOrSubmit);

	CommonRes validateAddress(String address, String companyId, String saveOrSubmit);

	CommonRes validateCityName(String cityName, String companyId, String saveOrSubmit);

	CommonRes validateStatus(String status, String companyId, String saveOrSubmit);

	CommonRes validateMobileNumber(String mobileNumber, String mobileCode, String companyId, String saveOrSubmit);

	CommonRes validateCustomerCreationFields(EserviceCustomerSaveReq req);

	CommonRes validatePincode(String pinCode, String companyId, String saveOrSubmit);

	CommonRes validateEmail(String email, String companyId, String saveOrSubmit);


	CommonRes validateDate(Date date, String policyHolderType, String idType, String companyId, String saveOrSubmit, String gender);

	List<Error> validate(CustomerChangesSaveReq req);

	SuccessRes customerChanges(CustomerChangesSaveReq req);	
	
	CommonRes fetchPolicyData(String policyNumber);
	
	SuccessRes updateCustomerDetails(EserviceCustomerSaveReq req);
	
	ResponseEntity<CommonRes> getExistingCustomerDetails(EserviceCustomerSaveReq req, List<String> validationCodes);

	List<EserviceCustomerLocation> getLocationsByCustomerRefNo(String customerRefNo);

	CommonRes fetchCustomerQuoteAndPolicyDetails(CustomerDetailsReq customerDetailsReq);

	CommonRes deleteLocation(Long locationId, String customerReferenceNo);

	CommonRes saveMultipleLocations(List<EserviceCustomerLocation> locations);

	CustomerCreationReq financeIntegrationCustomerCreation(CustomerCreationReq req);

	void financeIntegrationCustomerDetailUpdate(CustomerCreationReq req);


	void customerCreationFinanceThirdPartyApiCall(String polCustCode, String customerReferenceNo, String companyId,
			String policyNo);

	CustomerCreationReq financeCustomerCheckExist(CustomerCreationReq req);
	

}
