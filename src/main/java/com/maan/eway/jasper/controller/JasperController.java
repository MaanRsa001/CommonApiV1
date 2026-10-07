package com.maan.eway.jasper.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PortFolioDashBoardRes;
import com.maan.eway.error.Error;
import com.maan.eway.jasper.req.GetApiDocReportReq;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.req.JasperScheduleReq;
import com.maan.eway.jasper.req.PdfJsonReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.res.ApiDocListRes;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.res.ViewReportDetailsRes;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.service.PrintReqService;
import com.maan.eway.service.ValidationService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/pdf")
@Api(tags = "REPORT : Jasper Reports", description = "API's")
public class JasperController {
	
	@Autowired
	private JasperService jasper;
	@Autowired
	private  PrintReqService printReq;
	
	@Autowired
	private ValidationService servicevali;
	
	@Autowired
	private PrintReqService reqPrinter;

	
	@PostMapping("/policyform") 
	private ResponseEntity<CommonRes> policyform(@RequestBody JasperDocumentReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation =servicevali.validateMotorSchedule(req);
		//List<Error> validation =null;
		if(validation != null && !validation.isEmpty()){
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}else {
			JasperDocumentRes res = jasper.policyform(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("/proposalform") 
	private ResponseEntity<CommonRes> proposalform(@RequestBody JasperDocumentReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		
		JasperDocumentRes res = jasper.proposalform(req);;
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
	
	
	@PostMapping("/policyreport") 
	private ResponseEntity<CommonRes> policyreportform(@RequestBody JasperReportDocReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		
		JasperDocumentRes res = jasper.policyreportform(req);
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
	
//	@GetMapping("/taxInvoice")
//	private ResponseEntity<CommonRes> taxInvoice(@RequestParam ("quoteNo") String quoteNo){
//		CommonRes data = new CommonRes();
//		JasperDocumentRes res = jasper.taxInvoice(quoteNo);
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//		if(res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//	}
	
	@GetMapping("/taxInvoice")
	private ResponseEntity<CommonRes> taxInvoice(@RequestParam("quoteNo") String quoteNo) {
	    CommonRes data = new CommonRes();
	    System.out.println("Entering Taxinvoice API");
	    List<Error> validationErrors = servicevali.validateTaxInvoice(quoteNo);
	    
	    if (!validationErrors.isEmpty()) {
	    	System.out.println("Entering Taxinvoice API");
	        data.setCommonResponse(null);
	        data.setIsError(true);
	        data.setErrorMessage(validationErrors);
	        data.setMessage("Validation Failed");
	        return new ResponseEntity<>(data, HttpStatus.OK);
	    }

	    JasperDocumentRes res = jasper.taxInvoice(quoteNo);
	    data.setCommonResponse(res);
	    data.setIsError(false);
	    data.setErrorMessage(Collections.emptyList());
	    data.setMessage("Success");

	    if (res != null) {
	        return new ResponseEntity<>(data, HttpStatus.CREATED);
	    } else {
	        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	    }
	}
	
	@GetMapping("/creditNote")
	private ResponseEntity<CommonRes>  creditNote(@RequestParam ("quoteNo") String quoteNo){
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.creditNote(quoteNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/premium/report")
	public CommonRes getPremiumReport(@RequestBody PremiumReportReq req) {
		return jasper.getPremiumReport(req);
	}
	
	@PostMapping("/getPremiumReportDetails")
	public CommonRes getPremiumReportDetails(@RequestBody PremiumReportReq req) {
		return jasper.getPremiumReportDetails(req);
	}
	@PostMapping("/illustration/{JsonFile}")
	public ResponseEntity<JasperDocumentRes> illustration(@PathVariable("JsonFile") String jsonFile) {
		//CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.illustration(jsonFile);
		/*data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");*/
		if(res != null) {
			return new ResponseEntity<JasperDocumentRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/InalipaSchedule")
	public ResponseEntity<CommonRes> getInalipaSchedule(@RequestParam ("policyNo") String policyNo) {
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.getInalipaSchedule(policyNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/getSchedule")
	public CommonRes getSchedule(@RequestBody JasperScheduleReq req) {
		return jasper.getSchedule(req);
	}
	
	@PostMapping("/json/Response")
	public CommonRes PdfJsonResponse(@RequestBody PdfJsonReq req) {
		return jasper.PdfJsonResponse(req);
	}
	
	@GetMapping("/getKenyaMOTbyRefNo")
	public ResponseEntity<?> GetKenyaMOTbyRefNo(@RequestParam(value = "requestRefNo",required = true) String requestRefNo){
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.GetKenyaMOTbyRefNo(requestRefNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	@GetMapping("/GetTravelQuotation")
	public ResponseEntity<?> GetTravelQuotation(@RequestParam(value = "requestRefNo",required = true) String requestRefNo){
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.GetTravelQuotation(requestRefNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	@GetMapping("/getApiDocList/{quoteNo}")
	public ResponseEntity<?> getApiDocList(@PathVariable(value = "quoteNo") String quoteNo){
		CommonRes data = new CommonRes();
		List<ApiDocListRes> res = jasper.getApiDocList(quoteNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/download/ApiDoc")
	public ResponseEntity<?> getApiDocReport(@RequestBody GetApiDocReportReq req){
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.getApiDocReport(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("view/Certificate/{quoteNo}")
	public ResponseEntity<?> viewCertificate(@PathVariable ("quoteNo") String quoteNo,@RequestParam(name = "diskNo",required = false) String diskNo,
			@RequestParam(name = "travelYN") String travelYN){
		if("Y".equalsIgnoreCase(travelYN)) {
			GetApiDocReportReq req = new GetApiDocReportReq();
			req.setQuoteNo(quoteNo);
			req.setTravelYN("Y");
			JasperDocumentRes res = jasper.getApiDocReport(req);
			String base64 = res.getPdfoutfile();
			if (base64 == null || base64.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }
			byte[] decodedBytes = Base64.getDecoder().decode(base64.split(",")[1]);
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+quoteNo+".pdf")
					.contentType(org.springframework.http.MediaType.APPLICATION_PDF)
					.body(decodedBytes);
		}else {
			JasperDocumentReq req = new JasperDocumentReq();
			req.setQuoteNo(quoteNo);
			req.setCertificateYn("Y");
			req.setDiskNo(diskNo);
			JasperDocumentRes res = jasper.policyform(req);
			String base64 = res.getPdfoutfile();
			if (base64 == null || base64.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }
			byte[] decodedBytes = Base64.getDecoder().decode(base64.split(",")[1]);
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+quoteNo+".pdf")
					.contentType(org.springframework.http.MediaType.APPLICATION_PDF)
					.body(decodedBytes);
		}
	}
	
	@GetMapping("/eagle/quotation/{requestReferenceNo}")
	public ResponseEntity<?> eagleQuotation(@PathVariable ("requestReferenceNo") String requestReferenceNo,
			@RequestParam(name = "computationSheetYn",required = false) String computationSheetYn,
			@RequestParam(name = "vehicleId",required = false) String vehicleId){
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.getEagleQuotation(requestReferenceNo,computationSheetYn,vehicleId);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("viewReportDetails/{quoteNo}")
	private ResponseEntity<?> viewReportDetails(@PathVariable ("quoteNo") String quoteNo){
		CommonRes data = new CommonRes();
		ViewReportDetailsRes res = jasper.viewReportDetails(quoteNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/admin/portfoliodashboard")
	public ResponseEntity<CommonRes> getAllAdminPortfolio(@RequestBody PortFolioDashBoardReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<PortFolioDashBoardRes> res = jasper.getAllAdminPortfolio(req);
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
	
	 
	
	@PostMapping("/policyformbyrequestref") 
	private ResponseEntity<CommonRes> policyformByRequestRef(@RequestBody JasperDocumentReq req) {
	    printReq.reqPrint(req);
	    CommonRes data = new CommonRes();
	    
	    // First validate that RequestReferenceNo is provided
	    if (StringUtils.isBlank(req.getRequestReferenceNo())) {
	        data.setCommonResponse(null);
	        data.setIsError(true);
	        data.setErrorMessage(List.of(new Error("", "VALIDATION_ERROR", "RequestReferenceNo is required")));
	        data.setMessage("Failed");
	        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
	    }
	    
	//    List<Error> validation = servicevali.validateMotorSchedule1(req);
	    List<Error> validation = new ArrayList<Error>();
	    
	    if(validation != null && !validation.isEmpty()){
	        data.setCommonResponse(null);
	        data.setIsError(true);
	        data.setErrorMessage(validation);
	        data.setMessage("Failed");
	        return new ResponseEntity<>(data, HttpStatus.OK);
	    } else {
	        JasperDocumentRes res = jasper.policyformByRequestRef(req);
	        data.setCommonResponse(res);
	        data.setIsError(false);
	        data.setErrorMessage(Collections.emptyList());
	        data.setMessage("Success");
	        return new ResponseEntity<>(data, HttpStatus.CREATED);
	    }
	}
	
	@GetMapping("/download/policywording/{quoteNo}")
	private ResponseEntity<?> downloadPolicyWording(@PathVariable("quoteNo") String quoteNo){
		CommonRes data = new CommonRes();
		 JasperDocumentRes res = jasper.downloadPolicyWording(quoteNo);
		 if(res != null) {
			data.setCommonResponse(res);
	        data.setIsError(false);
	        data.setErrorMessage(Collections.emptyList());
	        data.setMessage("Success");
	        return new ResponseEntity<>(data, HttpStatus.CREATED);
		 }else {
			data.setCommonResponse(null);
	        data.setIsError(true);
	        data.setMessage("Failed");
	        return new ResponseEntity<>(data, HttpStatus.OK);
		 }
	        
	}
}
