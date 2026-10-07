package com.maan.eway.admin.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.AttachEndtIdsReq;
import com.maan.eway.admin.req.AttachIssuerProductRequest;
import com.maan.eway.admin.req.BrokerCompanyListProductsGetAllRes;
import com.maan.eway.admin.req.BrokerCompanyProductGetReq;
import com.maan.eway.admin.req.BrokerCompanyProductsGetRes;
import com.maan.eway.admin.req.BrokerProductGetReq;
import com.maan.eway.admin.req.IssuerProductGetReq;
import com.maan.eway.admin.req.UserCompanyProductGetReq;
import com.maan.eway.admin.res.BrokerProductGetRes;
import com.maan.eway.admin.res.IssuerProductGetRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.service.LoginProductService;
import com.maan.eway.admin.service.LoginValidationService;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerCompanyListProductReq;
import com.maan.eway.master.req.BrokerCompanyProductReq;
import com.maan.eway.master.req.BrokerProductChangeReq;
import com.maan.eway.master.req.BrokerProductReq;
import com.maan.eway.master.res.CompanyProductMasterRes;
import com.maan.eway.master.res.GetAllNonSelectedBrokerProductMasterRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "ADMIN : Login Product ", description = "API's")
@RequestMapping("/admin")
public class LoginProductController {


	@Autowired
	private  LoginProductService entityService;
	
	@Autowired
	private LoginValidationService validationService ;

	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	
//*************************************** Add Products Apis **********************************************************//
	 
	@PostMapping("/attachbrokerproducts")
	@ApiOperation(value="This method is to Attach Broker Products")
	public ResponseEntity<CommonRes> attachBrokerProducts(@RequestBody  AttachCompnayProductRequest req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = validationService.validateBrokerProductReq(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.getInsuranceId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
	
		//// validation
		if (validation != null && validation.size() != 0) 	{
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			LoginCreationRes res = entityService.saveBrokerProductDetails(req);
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

//*************************************** Get Products Apis **********************************************************//
	 
	@PostMapping("/getbrokerproductbyid")
	@ApiOperation(value="This method is to Get Broker Products")
	public ResponseEntity<CommonRes> getBrokerProducts(@RequestBody  BrokerProductGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// get
		BrokerProductGetRes res = entityService.getBrokerProducts(req);
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
	
	
//*************************************** Get One CompanyProducts Apis **********************************************************//	
	
	 
	@PostMapping("/getbrokercompanyproducts")
	@ApiOperation(value="This method is to Get Broker Company Products")
	public ResponseEntity<CommonRes> getBrokerCompanyProducts(@RequestBody  BrokerCompanyProductGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// get
		List<BrokerCompanyProductsGetRes> res = entityService.getBrokerCompanyProducts(req);
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
	
	
//  Get All Cover Master
	 
	@PostMapping("/updatebrokercompanyproducts")
	@ApiOperation(value = "This method is Insert Company Product Master")
	public ResponseEntity<CommonRes> insertCompanyProducts(@RequestBody BrokerCompanyProductReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<Error> validation = entityService.validateUpdateBrokerCompanyProductDetails(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Save
			SuccessRes res = entityService.updateBrokerCompanyProductDetails(req);
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
	
	 
	@PostMapping("/getallnonselectedbrokerproducts")
	@ApiOperation("This method is getall Company Product Master")
	public ResponseEntity<CommonRes> getallNonSelectedBrokerCompanyProducts(@RequestBody BrokerCompanyProductGetReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<CompanyProductMasterRes> res = entityService.getallNonSelectedBrokerCompanyProducts(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}
	
	 
	@PostMapping("/getallnonselecteduserproducts")
	@ApiOperation("This method is getall User Company Product Master")
	public ResponseEntity<CommonRes> getallNonSelectedUserCompanyProducts(@RequestBody UserCompanyProductGetReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<CompanyProductMasterRes> res = entityService.getallNonSelectedUserCompanyProducts(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}
	 
	@PostMapping("/brokercompanyproducts/changestatus")
	@ApiOperation(value = "This method is get Company Product Master Drop Down")

	public ResponseEntity<CommonRes> changeStatusOfCompanyProduct(@RequestBody BrokerProductChangeReq req) {

		CommonRes data = new CommonRes();
		// Change Status
		SuccessRes res = entityService.changeStatusOfCompanyProduct(req);
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
	
	// Branch Master Drop Down Type
	 
	@PostMapping("/dropdown/brokerproducts")
	@ApiOperation(value = "This method is get Branch Master Drop Down")
	public ResponseEntity<CommonRes> getBrokerProductDropdown(@RequestBody BrokerProductReq req ) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = entityService.getBrokerProductDropdown(req);
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
	
	
	@PostMapping("/attachissuerproducts")
	@ApiOperation(value="This method is to Attach Issuer Products")
	public ResponseEntity<CommonRes> attachIssuerProducts(@RequestBody  AttachIssuerProductRequest req) {
		
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = validationService.validateIssuerProductReq(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.getInsuranceId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
	
		//// validation
		if (validation != null && validation.size() != 0) 	{
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			LoginCreationRes res = entityService.saveIssuerProducts(req);
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
	
	
	@PostMapping("/attachloginendtids")
	@ApiOperation(value="This method is to Attach Issuer Products")
	public ResponseEntity<CommonRes> attachProductsEndtIds(@RequestBody  AttachEndtIdsReq req) {
		
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = validationService.validateProductsEndtIds(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.getInsuranceId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		
	
		//// validation
		if (validation != null && validation.size() != 0) 	{
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			LoginCreationRes res = entityService.saveProductsEndtIds(req);
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

	@PostMapping("/getissuerproductbyid")
	@ApiOperation(value="This method is to Get Broker Products")
	public ResponseEntity<CommonRes> getIssuerProducts(@RequestBody  IssuerProductGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// get
		List<IssuerProductGetRes> res = entityService.getIssuerProducts(req);
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
//************************************************************************************************************
	
	@PostMapping("/getallbrokercompanylistproduct")
	@ApiOperation(value="This method is to Get Broker Company Products")
	public ResponseEntity<CommonRes> getAllBrokerCompanyListProducts(@RequestBody  BrokerCompanyProductGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// get
		List<BrokerCompanyListProductsGetAllRes> res = entityService.getAllBrokerCompanyListProducts(req);
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

	@PostMapping("/updatebrokercompanylistproducts")
	@ApiOperation(value = "This method is Insert Company Product Master")
	public ResponseEntity<CommonRes> brokerListCompanyProducts(@RequestBody List<BrokerCompanyListProductReq> req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<Error> validation = entityService.validatebrokerListCompanyProducts(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Save
			SuccessRes res = entityService.brokerListCompanyProducts(req);
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

	@PostMapping("/getallnonselecteduserproductslist")
	@ApiOperation("This method is getall User Company Product Master")
	public ResponseEntity<CommonRes> getallNonSelectedUserCompanyProductsList(@RequestBody UserCompanyProductGetReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<GetAllNonSelectedBrokerProductMasterRes> res = entityService.getallNonSelectedUserCompanyProductsList(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}
	
}
