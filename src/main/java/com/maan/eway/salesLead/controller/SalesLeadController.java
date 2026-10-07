package com.maan.eway.salesLead.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.salesLead.req.EnquiryDetailsDTO;
import com.maan.eway.salesLead.req.GetEnquiryDetailsReq;
import com.maan.eway.salesLead.req.GetUploadDocumentListReq;
import com.maan.eway.salesLead.req.InsertSalesReq;
import com.maan.eway.salesLead.req.SaveUploadDocumentsReq;
import com.maan.eway.salesLead.service.SalesLeadService;
import com.maan.eway.salesLead.validation.SalesLeadValidation;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/sales")
public class SalesLeadController {
	
	@Autowired
	private SalesLeadService service;
	
	@Autowired	
	private SalesLeadValidation leadVali;
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
		
	@PostMapping("/insertLeadDetails")
	public ResponseEntity<?> insertLeadContact(@RequestBody List<InsertSalesReq> req){
		CommonRes data = new CommonRes();
		List<Error> errors = null;	//leadVali.insertLeadContactVali(req);
		if (errors != null && errors.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(errors);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		} else {
			boolean status = service.insertLeadDetails(req);
			data.setCommonResponse(status);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if (status) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("/getSalesLead")
	public ResponseEntity<?> getSalesLead(@RequestParam (value = "leadId",required = false) String leadId){
		CommonRes res = service.getSalesLead(leadId);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	@PostMapping("/insertEnquiry")
	public ResponseEntity<?> insertEnquiry(@RequestBody EnquiryDetailsDTO req){
		CommonRes res = service.insertEnquiry(req);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	@PostMapping("/getEnquirys")
	public ResponseEntity<?> getAllEnquiry(@RequestBody GetEnquiryDetailsReq req){
		CommonRes res = service.getEnquirys(req);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	/// Drop  down api //
	@PostMapping("/contactType")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> contactType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.contactType(req);
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
	
	@PostMapping("/sectionType")
	@ApiOperation(value = "This method is to get section type Drop Down")
	public ResponseEntity<CommonRes> sectionType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.sectionType(req);
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
	
	@PostMapping("/channel")
	@ApiOperation(value = "This method is to get channel Drop Down")
	public ResponseEntity<CommonRes> customerType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.customerType(req);
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
	
	@PostMapping("/typeOfBusiness")
	@ApiOperation(value = "This method is to get type of business Drop Down")
	public ResponseEntity<CommonRes> typeOfBusiness(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.typeOfBusiness(req);
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
	
	@PostMapping("/currentInsurer")
	@ApiOperation(value = "This method is to get current Insurer Drop Down")
	public ResponseEntity<CommonRes> currentInsurer(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.currentInsurer(req);
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
	
	@PostMapping("/lineOfBusiness")
	@ApiOperation(value = "This method is to get line of business Drop Down")
	public ResponseEntity<CommonRes> lineOfBusiness(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.lineOfBusiness(req);
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
	
	@PostMapping("/product")
	@ApiOperation(value = "This method is to get product Drop Down")
	public ResponseEntity<CommonRes> product(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.product(req);
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
	
	@PostMapping("/businessType")
	@ApiOperation(value = "This method is to get business type Drop Down")
	public ResponseEntity<CommonRes> businessType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.businessType(req);
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
	
	@PostMapping("/pos")
	@ApiOperation(value = "This method is to get pos Drop Down")
	public ResponseEntity<CommonRes> probabilityOfSuccess(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.probabilityOfSuccess(req);
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

	@GetMapping("/status")
	@ApiOperation(value = "This method is to get status Drop Down")
	public ResponseEntity<CommonRes> getStatusByUserType() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.getStatusByUserType();
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
	
	@PostMapping("saveUploadDocuments")
	private ResponseEntity<CommonRes> saveUploadDocuments(@RequestParam("Req") String jsonReq,@RequestParam(name = "files",required = true) List<MultipartFile> fileReq) throws IOException {
		CommonRes data = new CommonRes();
		SaveUploadDocumentsReq req = new ObjectMapper().readValue(jsonReq, SaveUploadDocumentsReq.class);
		reqPrinter.reqPrint(req);
		boolean status = service.saveUploadDocuments(req,fileReq);
		if(status) {
			data.setCommonResponse("Successfully Uploaded");
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		}else {
			data.setCommonResponse("Document Uploaded Failed");
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Failes");
		}
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	}
	
	@PostMapping("getUploadDocumentList")
	private ResponseEntity<CommonRes> getUploadDocumentList(@RequestBody GetUploadDocumentListReq req){
		CommonRes res = service.getUploadDocumentList(req);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping("downloadUploadDocuments/{id}")
	private ResponseEntity<CommonRes> downloadUploadDocuments(@PathVariable ("id") String id){
			CommonRes data = new CommonRes();
			JasperDocumentRes res = service.downloadUploadDocuments(id);
			if(res!=null) {
				data.setCommonResponse(res);
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			}
	}
	

	/*@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/saveleaddetails")
	public ResponseEntity<CommonRes> saveLeadDetails(@RequestBody  EserviceLeadSaveReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = new ArrayList<>();
		 validationCodes = leadVali.validateCustomerDetails(req);
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
			SuccessRes res = service.saveLeadDetails(req);
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
	@PostMapping("/getLeaddetails")
	public ResponseEntity<CommonRes> getLeadDetails(@RequestBody GetCustomerDetailsReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<GetLeadDetailsRes> res = service.getLeadDetails(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}*/
}
