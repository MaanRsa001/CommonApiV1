package com.maan.eway.integration.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.admin.res.GetallPortfolioActiveRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.integration.req.GetAllPolicy;
import com.maan.eway.integration.req.IntegrationStateByPolicyReq;
import com.maan.eway.integration.req.PremiaGetReq;
import com.maan.eway.integration.res.CreditLimitDetailGetRes;
import com.maan.eway.integration.res.IntegrationStatgingRes;
import com.maan.eway.integration.res.MotCommDiscountDetailGetRes;
import com.maan.eway.integration.res.MotDriverDetailGetRes;
import com.maan.eway.integration.res.PgithPolRiskAddlInfoGetRes;
import com.maan.eway.integration.res.YiChargeDetailsGetRes;
import com.maan.eway.integration.res.YiCoverDetailsGetRes;
import com.maan.eway.integration.res.YiPolicyApprovalGetRes;
import com.maan.eway.integration.res.YiPolicyDetailsGetRes;
import com.maan.eway.integration.res.YiPremCalGetRes;
import com.maan.eway.integration.res.YiSectionDetailGetRes;
import com.maan.eway.integration.res.YiVatDetailGetRes;
import com.maan.eway.integration.service.IntegrationGetService;

@RestController
@RequestMapping("/integration")
public class PremiaTableGetController {
	
	@Autowired
	private IntegrationGetService service;
	
	
	@PostMapping("/getallpolicydetails")
	public ResponseEntity<CommonRes> getAllPolicyDetails(@RequestBody GetAllPolicy req) {
		CommonRes data = new CommonRes();
		GetallPortfolioActiveRes res = service.getAllPolicyDetails(req);

		if (res != null) {
			data.setCommonResponse(res);
			data.setErrorMessage(Collections.emptyList());
			data.setIsError(false);
			data.setMessage("Success");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	
	@PostMapping("/getyipolicydetails")
	public ResponseEntity<CommonRes> getYiPolicyDetails(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<YiPolicyDetailsGetRes> res = service.getYiPolicyDetails(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getyichargedetails")
	public ResponseEntity<CommonRes> getYiChargeDetails(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<YiChargeDetailsGetRes> res = service.getYiChargeDetails(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getyicoverdetails")
	public ResponseEntity<CommonRes> getYiCoverDetails(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<YiCoverDetailsGetRes> res = service.getYiCoverDetails(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getyipolicyapproval")
	public ResponseEntity<CommonRes> getYiPolicyApproval(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<YiPolicyApprovalGetRes> res = service.getYiPolicyApproval(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getyipremcal")
	public ResponseEntity<CommonRes> getYiPremCal(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<YiPremCalGetRes> res = service.getYiPremCal(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getyisectiondetail")
	public ResponseEntity<CommonRes> getYiSectionDetail(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<YiSectionDetailGetRes> res = service.getYiSectionDetail(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("getyivatdetails")
	public ResponseEntity<CommonRes> getYiSmiCoverDetail(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<YiVatDetailGetRes> res = service.getVatDetail(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getmotdriverdetails")
	public ResponseEntity<CommonRes> getMotDriverDetails(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<MotDriverDetailGetRes> res = service.getMotDriverDetail(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getcreditlimitdetail")
	public ResponseEntity<CommonRes> getCreditLimitDetails(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<CreditLimitDetailGetRes> res = service.getCreditLimitDetail(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getmotcommdiscountdetail")
	public ResponseEntity<CommonRes> getMotCommDiscountDetails(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<MotCommDiscountDetailGetRes> res = service.getMotCommDiscountDetail(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	
	@PostMapping("/getpgithpolriskaddlinfo")
	public ResponseEntity<CommonRes> getPgithPolRiskAddlInfo(@RequestBody PremiaGetReq req)
	{
	CommonRes data = new CommonRes();
	List<PgithPolRiskAddlInfoGetRes> res = service.getPgithPolRiskAddlInfo(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}
	@PostMapping("/getIntegrationStageByDate")
	public ResponseEntity<CommonRes> getIntegrationStageByDate(@RequestBody IntegrationStateByPolicyReq req)
	{
	CommonRes data = new CommonRes();
	List<IntegrationStatgingRes> res = service.getIntegrationStageDetails(req);
	

	if (res != null) {
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	}


}
