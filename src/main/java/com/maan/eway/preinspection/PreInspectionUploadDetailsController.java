package com.maan.eway.preinspection;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.service.PrintReqService;
import com.maan.eway.ticket.CustomerTrackerReq;







@RestController
@RequestMapping("/preinspection")
public class PreInspectionUploadDetailsController {
	
	@Autowired
	private PreInspectionUploadDetailsService preInspUploadService;

	@Autowired
	private  PrintReqService reqPrinter;
	
	@PostMapping("/save/details")
	public Object savePreInspectionDetails(@RequestBody PreInspectionUploadDetailSaveReq req) {
		return preInspUploadService.savePreInspectionDetails(req);
	}

	@PostMapping("/get/uploaddetails")
	public GetPreInspectionUploadDetailRes getPreInspectionDetails(@RequestBody GetPreInspectionUploadDetailReq req) {
		return preInspUploadService.getPreInspectionDetails(req);
	}
	
	@PostMapping("/get/imagedetails")
	public Object getPreInspectionImageDetails(@RequestBody GetPreInspectionUploadDetailReq req) {
		return preInspUploadService.getPreInspectionImageDetails(req);
	}
	
	@PostMapping("/delete/image")
	public Object deletePreInspectionImage(@RequestBody GetPreInspectionUploadDetailReq req) {
		return preInspUploadService.deletePreInspectionImage(req);
	}
	
	@PostMapping("/upload/file")
	public FileUploadRes uploadFile(@RequestBody PreFileUploadReq req) {
		return preInspUploadService.uploadFile(req);
	}
	
	@PostMapping("/image/dashboard")
	public ResponseEntity<CommonRes> customerTracker(@RequestBody GetPreInspectionUploadDetailReq req){
		reqPrinter.reqPrint("Printer Request --->" + req);
		CommonRes data = new CommonRes();

		CommonRes res = preInspUploadService.preInspectionDashboard(req);
		if (res.getIsError() == false ) {
			data.setCommonResponse(res.getCommonResponse() );
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			
		} else {
			data.setCommonResponse(res.getCommonResponse());
			data.setIsError(true);
			data.setErrorMessage(res.getErrorMessage() );
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
	}

}
