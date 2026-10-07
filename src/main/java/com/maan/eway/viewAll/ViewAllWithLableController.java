package com.maan.eway.viewAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.viewAll.dto.DropDownReq;
import com.maan.eway.viewAll.dto.FieldQueryTableQueryDto;
import com.maan.eway.viewAll.dto.GetALLCoverDto;
import com.maan.eway.viewAll.dto.OverAllResForView;
import com.maan.eway.viewAll.dto.RiskFieldFlowDto;
import com.maan.eway.viewAll.dto.viewAllReq;
import com.maan.eway.viewAll.service.UgandaDebiteJsonService;
import com.maan.eway.viewAll.service.ViewAllWithLableService;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "View All Policy Detail with lable", description = "API's")
@RequestMapping("/view")
public class ViewAllWithLableController {
	@Autowired
	private ViewAllWithLableService service;
	
	@Autowired
	private UgandaDebiteJsonService ugandaService;
	
//	@Autowired
//	private ViewAllWithLableServiceQuation serviceQuation; 
	
	@PostMapping("/all/policy")
	public ResponseEntity<CommonRes> viewlocation(@RequestBody viewAllReq req ) {
		CommonRes res = new CommonRes();
		OverAllResForView data= service.viewAllinKeyAndValue(req);
		res.setCommonResponse(data);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
//	@PostMapping("/all/Quotation")
//	public ResponseEntity<OverAllResForView> viewQuation(@RequestBody QuotationReq req ) {
//		OverAllResForView data= serviceQuation.viewAllQuotation(req);
//	 	if (data != null) {
//			return new ResponseEntity<OverAllResForView>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//	}
//	
	
	@PostMapping("/insertCover")
	public ResponseEntity<CommonRes> insertCover(@RequestBody RiskFieldFlowDto req ) {
		CommonRes data= service.insertcoverForRFL(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/getCover")
	public ResponseEntity<CommonRes> getAllCover(@RequestBody GetALLCoverDto req ) {
		CommonRes data= service.getAllcoverForRFL(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/getQuery/DropDown")
	public ResponseEntity<CommonRes> queryDropDown() {
		CommonRes data= service.getQueryDropDown();
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/getQuery/Insert")
	public ResponseEntity<CommonRes> queryInsert(@RequestBody FieldQueryTableQueryDto req) {
		CommonRes data= service.getQueryInsert(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/getField/DropDown")
	public ResponseEntity<CommonRes> getField(@RequestBody DropDownReq req) {
		CommonRes data= service.getFieldDropDown(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/Delete/Record")
	public ResponseEntity<CommonRes> deleteRecordInRisk(@RequestBody viewAllReq req) {
		CommonRes data= service.deleteRecord(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/oman/pdf")
	public ResponseEntity<CommonRes> OmanRecords(@RequestBody viewAllReq req) {
		CommonRes data= service.omanRecordForall(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@PostMapping("/uganda/tax")
	public ResponseEntity<CommonRes> ugandaTax(@RequestBody viewAllReq req) {
		CommonRes data= ugandaService.jsonFrame(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

}
