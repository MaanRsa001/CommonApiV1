package com.maan.eway.document.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.document.req.DocGetReq;
import com.maan.eway.document.req.DocTypeDropDownReq;
import com.maan.eway.document.req.DocTypeReq;
import com.maan.eway.document.req.DocumentDeleteReq;
import com.maan.eway.document.req.DocumentUploadOCRReq;
import com.maan.eway.document.req.DocumentUploadReq;
import com.maan.eway.document.req.FilePathReq;
import com.maan.eway.document.req.GetDocListReq;
import com.maan.eway.document.req.GetEmiDocReq;
import com.maan.eway.document.req.TermsDocUploadReq;
import com.maan.eway.document.req.UpdateVerifiedYnReq;
import com.maan.eway.document.res.CommonDocumentRes;
import com.maan.eway.document.res.DocTypeRes;
import com.maan.eway.document.res.DocumentListRes;
import com.maan.eway.document.res.DocumentTypeDetails;
import com.maan.eway.document.res.FilePathRes;
import com.maan.eway.document.res.TermsDocRes;
import com.maan.eway.document.service.DocumentService;
import com.maan.eway.document.service.impl.GetFileFromPath;
import com.maan.eway.error.CommonValidationException;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping("/document")
@Api(tags = "DOCUMENT : Document ", description = "API's")
@RestController
public class DocumentController {

	@Autowired
	private DocumentService documentservice;

	@Autowired
	private PrintReqService reqPrinter;

	// DropDown
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getlocationwisesrisk")
	@ApiOperation(value = "This method is to Get Location Wise Risk")
	public ResponseEntity<CommonDocumentRes> getLocationWiseSections(@RequestBody DocTypeDropDownReq req) {
		CommonDocumentRes data = new CommonDocumentRes();
		DocumentTypeDetails res = documentservice.getLocationWiseSections(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		return new ResponseEntity<CommonDocumentRes>(data, HttpStatus.CREATED);

	}

	private Logger log = LogManager.getLogger(DocumentController.class);

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/upload")
	@ApiOperation(value = "This method is to Upload Document")
	public ResponseEntity<CommonRes> uploadFile(@RequestParam(value = "File", required = false ) MultipartFile file,
			@RequestParam("Req") String jsonString)
			throws CommonValidationException, JsonMappingException, JsonProcessingException {

		log.info(jsonString);

		DocumentUploadReq req = new ObjectMapper().readValue(jsonString, DocumentUploadReq.class);

		List<Error> error = new ArrayList<Error>();
		error = documentservice.docvalidation(req, file);
		if (error != null && error.size() > 0) {

			CommonRes res = new CommonRes();
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErrorMessage(error);
			res.setMessage("Success");

			return ResponseEntity.status(HttpStatus.OK).body(res);

		} else {
			CommonRes res = documentservice.fileupload(req, file);
			return ResponseEntity.status(HttpStatus.OK).body(res);
		}

	}
	
//	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/uploadNoAuth")
	@ApiOperation(value = "This method is to Upload Document")
	public ResponseEntity<CommonRes> uploadFileNoAuth(@RequestParam(value = "File", required = false ) MultipartFile file,
			@RequestParam("Req") String jsonString)
			throws CommonValidationException, JsonMappingException, JsonProcessingException {

		log.info(jsonString);

		DocumentUploadReq req = new ObjectMapper().readValue(jsonString, DocumentUploadReq.class);

		List<Error> error = new ArrayList<Error>();
		error = documentservice.docvalidation(req, file);
		if (error != null && error.size() > 0) {

			CommonRes res = new CommonRes();
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErrorMessage(error);
			res.setMessage("Success");

			return ResponseEntity.status(HttpStatus.OK).body(res);

		} else {
			CommonRes res = documentservice.fileupload(req, file);
			return ResponseEntity.status(HttpStatus.OK).body(res);
		}

	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/uploadOcr")
	@ApiOperation(value = "This method is to Upload Document for OCR")
	public ResponseEntity<CommonRes> uploadFileForOCR(@RequestBody DocumentUploadOCRReq req) {
		log.info(req);

		List<Error> errorList = documentservice.ocrFileValidation(req);
		
		if(errorList != null && errorList.size() > 0){
			
			CommonRes res = new CommonRes();
			res.setMessage("Failed");
			res.setIsError(true);
			res.setCommonResponse(null);
			res.setErrorMessage(errorList);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(res);
		}

		CommonRes res = documentservice.fileuploadOCR(req);
		/*
		 * if(res.getIsError()) { res.setErrorMessage(List.of(new
		 * Error("01","File","Please Enter Correct Id Value"))); }
		 */
		return ResponseEntity.status(HttpStatus.OK).body(res);

	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/termsdocupload")
	@ApiOperation(value = "This method is to Upload Terms & Condition Document")
	public ResponseEntity<CommonRes> termsDocUploadFile(@RequestParam("File") MultipartFile file,
			@RequestParam("Req") String jsonString)
			throws CommonValidationException, JsonMappingException, JsonProcessingException {

		log.info(jsonString);

		TermsDocUploadReq req = new ObjectMapper().readValue(jsonString, TermsDocUploadReq.class);

		List<Error> error = new ArrayList<Error>();
		error = documentservice.doctermsvalidation(req, file);
		if (error != null && error.size() > 0) {

			CommonRes res = new CommonRes();
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErrorMessage(error);
			res.setMessage("Success");

			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(res);

		} else {
			CommonRes res = documentservice.termsfileupload(req, file);
			return ResponseEntity.status(HttpStatus.OK).body(res);
		}

	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/gettermsdoc")
	@ApiOperation(value = "This method is to Get Upload Terms & Condition Image File")
	public ResponseEntity<CommonRes> getTermsFilePath(@RequestBody DocGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		TermsDocRes res = documentservice.getTermsFilePath(req);
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

//	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
//	@PostMapping("/uploadwithoutfile")
//	@ApiOperation(value = "This method is to Upload Document")
//	public ResponseEntity<CommonRes> uploadWithoutFile( @RequestParam("Req") String jsonString) throws CommonValidationException, JsonMappingException, JsonProcessingException{
//		
//		log.info(jsonString);
//		 MultipartFile file = null ;
//		DocumentUploadReq req =  new ObjectMapper().readValue(jsonString, DocumentUploadReq.class); 
//		
//    	List<Error> error = new ArrayList<Error>();
//		error = documentservice.docvalidation(req,file);
//		if (error != null && error.size() > 0) {
//			
//			CommonRes res = new CommonRes();
//			res.setCommonResponse(null);
//			res.setIsError(true);
//			res.setErrorMessage(error);
//			res.setMessage("File Upload Faild");
//			
//			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(res);
//			
//		}else {
//			CommonRes res = documentservice.fileupload(req,file);
//			return ResponseEntity.status(HttpStatus.OK).body(res);
//		}
//		
//	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/delete")
	@ApiOperation(value = "This method is to Remove Document")
	public ResponseEntity<CommonRes> deleteFile(@RequestBody DocumentDeleteReq req) {

		CommonRes res = documentservice.deleteFile(req);
		return ResponseEntity.status(HttpStatus.OK).body(res);

	}

	// DropDown
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/dropdown/doctypes")
	@ApiOperation(value = "This method is to Get Doc Types")
	public ResponseEntity<CommonRes> getDocTypeDropDowns(@RequestBody DocTypeReq req) {
		CommonRes data = new CommonRes();
		List<DocTypeRes> res = documentservice.getDocTypeDropDowns(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	}
	
	@PostMapping("/dropdown/doctypesforR")
	@ApiOperation(value = "This method is to Get Doc Types")
	public ResponseEntity<CommonRes> getDocTypeForRegister(@RequestBody DocTypeReq req) {
		CommonRes data = new CommonRes();
		List<DocTypeRes> res = documentservice.getDocTypeDropDowns(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	}

	// Get Doc List
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getdoclist")
	@ApiOperation(value = "This method is to Get Document List")
	public ResponseEntity<CommonRes> getdoclist(@RequestBody GetDocListReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		// Total Doc List
		DocumentListRes res = documentservice.getTotalDocList(req);

		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			data.setCommonResponse(null);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return new ResponseEntity<>(data, HttpStatus.OK);
		}

	}

	// Get EMI Doc List
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getemidoc")
	@ApiOperation(value = "This method is to Get Document List")
	public ResponseEntity<CommonRes> getEmiDoc(@RequestBody GetEmiDocReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		// Total Doc List
		DocumentListRes res = documentservice.getEmiDoc(req);

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

	// Get Original Image
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@RequestMapping(path = "/download", method = RequestMethod.POST)
	public ResponseEntity<Resource> download(@RequestParam("FilePath") String param) throws IOException {

		File file = new File(param);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@RequestMapping(path = "/downloadbase64", method = RequestMethod.POST)
	public ResponseEntity<CommonRes> downloadBase64(@RequestParam("FilePath") String param) throws Exception {
		CommonRes data = new CommonRes();
		FilePathRes fileRes = new FilePathRes();
		fileRes.setFilepathname(param);
		if (StringUtils.isNotBlank(fileRes.getFilepathname()) && new File(fileRes.getFilepathname()).exists()) {
			fileRes.setImgurl(new GetFileFromPath(fileRes.getFilepathname()).call().getImgUrl());
		} else
			System.out.println("File is Not found!!" + fileRes.getFilepathname());
		data.setCommonResponse(fileRes);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (fileRes != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getoriginalimage")
	@ApiOperation(value = "This method is to Get Image File ")
	public ResponseEntity<CommonRes> getFilePath(@RequestBody FilePathReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		FilePathRes res = documentservice.getFilePath(req);
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

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getcompressedimage")
	@ApiOperation(value = "This method is to Get Compressed Image File ")
	public ResponseEntity<CommonRes> getCompressedImages(@RequestBody FilePathReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		FilePathRes res = documentservice.getCompressedImages(req);
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
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/updateverifiedyn")
	@ApiOperation(value = "This method is to Get Upload Terms & Condition Image File")
	public ResponseEntity<CommonRes> updateVerifiedYn(@RequestBody UpdateVerifiedYnReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		SuccessRes res = documentservice.updateVerifiedYn(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getExistingDocuments")
	@ApiOperation(value = "Get existing documents for a customer by quote no")
	public ResponseEntity<CommonRes> getExistingDocuments(
	        @RequestBody Map<String, String> request) {

	    CommonRes res = documentservice.getExistingDocuments(request.get("QuoteNo"));
	    return ResponseEntity.status(HttpStatus.OK).body(res);
	}


}
