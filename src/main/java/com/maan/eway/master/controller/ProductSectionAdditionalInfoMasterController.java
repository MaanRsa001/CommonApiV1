package com.maan.eway.master.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.document.controller.DocumentController;
import com.maan.eway.error.CommonValidationException;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetAllSectionAdditionalDetailsReq;
import com.maan.eway.master.req.GetOptedSectionAdditionalInfoReq;
import com.maan.eway.master.req.GetSectionAdditionalDetailsReq;
import com.maan.eway.master.req.InsertAdditionalInfoReq;
import com.maan.eway.master.req.UploadReq;
import com.maan.eway.master.res.GetOptedSectionAdditionalInfoRes;
import com.maan.eway.master.res.GetSectionAdditionalDetailsRes;
import com.maan.eway.master.service.ProductSectionAdditionalInfoMasterService;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MASTER : Product Section Additional Info Master", description = "API's")
@RequestMapping("/master")
public class ProductSectionAdditionalInfoMasterController {
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private ProductSectionAdditionalInfoMasterService service;
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getoptedsectionadditionalinfo") 	//based on product and opted sections (Quote Flow)
	public ResponseEntity<CommonRes> getOptedSectionAdditionalInfo(@RequestBody GetOptedSectionAdditionalInfoReq req){
		CommonRes data = new CommonRes();
			reqPrinter.reqPrint(req);
			List<GetOptedSectionAdditionalInfoRes> res = service.getOptedSectionAdditionalInfo(req);
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
	@PostMapping("/insertadditionalinfo")
	@ApiOperation(value = "This method is Insert Additional Info")
	public ResponseEntity<CommonRes> insertAdditionalInfo(@RequestBody InsertAdditionalInfoReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<Error> validation = service.validateAdditionalInfo(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Get All
			SuccessRes res = service.insertAdditionalInfo(req);
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
	
	//  Get By Section Id (Admin side)
		@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER')")
		@PostMapping("/getsectionadditionaldetails")
		@ApiOperation("This method is get section additional details")
		public ResponseEntity<CommonRes> getSectionAdditionalDetails(@RequestBody GetSectionAdditionalDetailsReq req)
		{
			CommonRes data = new CommonRes();
			reqPrinter.reqPrint(req);
			
			GetSectionAdditionalDetailsRes res = service.getSectionAdditionalDetails(req);
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

		private Logger log = LogManager.getLogger(DocumentController.class);
		
		//File Upload
		@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
		@PostMapping("/upload")
		@ApiOperation(value = "This method is to Upload Document")
		public ResponseEntity<CommonRes> uploadFile(@RequestParam("File") MultipartFile file,  @RequestParam("Req") String jsonString) throws CommonValidationException, JsonMappingException, JsonProcessingException{
			
			log.info(jsonString);
			
			UploadReq req =  new ObjectMapper().readValue(jsonString, UploadReq.class); 
			
	    	List<Error> error = new ArrayList<Error>();
			error = service.docvalidation(file, req);
			if (error != null && error.size() > 0) {
				
				CommonRes res = new CommonRes();
				res.setCommonResponse(null);
				res.setIsError(true);
				res.setErrorMessage(error);
				res.setMessage("Success");
				
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(res);
				
			}else {
				CommonRes res = service.fileupload(file, req);
				return ResponseEntity.status(HttpStatus.OK).body(res);
			}
			
		}
		
	//  Get All Section Id (Admin side)
			@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER')")
			@PostMapping("/getallsectionadditionaldetails")
			@ApiOperation("This method is get section additional details")
			public ResponseEntity<CommonRes> getAllSectionAdditionalDetails(@RequestBody GetAllSectionAdditionalDetailsReq req)
			{
				CommonRes data = new CommonRes();
				reqPrinter.reqPrint(req);
				
				List<GetSectionAdditionalDetailsRes> res = service.getAllSectionAdditionalDetails(req);
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
