package com.maan.eway.pdfReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.viewAll.dto.viewAllReq;
import com.maan.eway.viewAll.service.UgandaDebiteJsonService;



@RestController
@RequestMapping("/view")
public class PdfReportController {
	@Autowired
	private PdfReportSerivceImpl pdfReportSerivceImpl;
	
	@Autowired
	private UgandaDebiteJsonService ugandaReportSerivceImpl;
	
	@PostMapping("/generateDebitNoteUganda/pdf")
	public ResponseEntity<CommonRes> generateDebitNoteUgandaRecords(@RequestBody viewAllReq req) {
		CommonRes data= ugandaReportSerivceImpl.generateDebitNoteUganda(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/generateUgandaSchedule/pdf")
	public ResponseEntity<CommonRes> generateUgandaScheduleRecords(@RequestBody JasperDocumentReq req) {
		CommonRes data= pdfReportSerivceImpl.generateScheduleUganda(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

}