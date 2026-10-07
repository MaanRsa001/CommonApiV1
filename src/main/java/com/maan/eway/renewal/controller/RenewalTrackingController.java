package com.maan.eway.renewal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.renewal.req.RenewalTrackReq;
import com.maan.eway.renewal.req.RenewalVehicleReq;
import com.maan.eway.renewal.req.UpdateRenewalPremiaPolicyReq;
import com.maan.eway.renewal.service.RenewalTrackingService;
import com.maan.eway.renewal.service.Validation;

import io.swagger.annotations.Api;


@RestController
@RequestMapping("/renewaltrack")
@Api(tags = "Track the renewal policy")
public class RenewalTrackingController {

	@Autowired
	private RenewalTrackingService service;
	
	@Autowired
	Validation vali;

	@PostMapping("/getdivisionbycompany")
	public ResponseEntity<?> getByCompany(@RequestBody RenewalTrackReq req) {
		if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty()) {
			return ResponseEntity.ok(service.RenewalTrackGetBranch(req));
		} else {
			return ResponseEntity.badRequest().body("CompanyId is required");
		}
	}

	@PostMapping("/getproductsbycompanyanddivision")
	public ResponseEntity<?> getByCompanyAndDivision(@RequestBody RenewalTrackReq req) {
		if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty() && req.getDivisionCode() != null
				&& !req.getDivisionCode().trim().isEmpty()) {
			return ResponseEntity.ok(service.GetRenewalDetailsByDivsion2(req));
		} else {
			return ResponseEntity.badRequest().body("CompanyId and DivisionCode are required");
		}
	}

	@PostMapping("/getsourcesbyproduct")
	public ResponseEntity<?> getByCompanyDivisionProduct(@RequestBody RenewalTrackReq req) {
		if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty() && req.getDivisionCode() != null
				&& !req.getDivisionCode().trim().isEmpty() && req.getProductCode() != null
				&& !req.getProductCode().trim().isEmpty()) {
			return ResponseEntity.ok(service.RenewalTrackAgentRes2(req));
		} else {
			return ResponseEntity.badRequest().body("CompanyId, DivisionCode, and ProductCode are required");
		}
	}

	@PostMapping("/getpolicydetails")
	public ResponseEntity<?> getByAllParams(@RequestBody RenewalTrackReq req) {
		if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty() && req.getDivisionCode() != null
				&& !req.getDivisionCode().trim().isEmpty() && req.getProductCode() != null
				&& !req.getProductCode().trim().isEmpty() && req.getSourceCode() != null
				&& !req.getSourceCode().trim().isEmpty()) {
			return ResponseEntity.ok(service.RenewalTrackPolicyDetailsBySource(req));
		} else {
			return ResponseEntity.badRequest()
					.body("CompanyId, DivisionCode, ProductCode, and SourceCode are required");
		}
	}

	@PostMapping("/getproductsbysource")
	public ResponseEntity<?> getProductsBySource(@RequestBody RenewalTrackReq req) {
		if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty() && req.getDivisionCode() != null
				&& !req.getDivisionCode().trim().isEmpty() && req.getSourceCode() != null
				&& !req.getSourceCode().trim().isEmpty()) {
			return ResponseEntity.ok(service.getProductsBySource(req));
		} else {
			return ResponseEntity.badRequest().body("CompanyId, DivisionCode and SourceCode are required");
		}
	}

	@PostMapping("/getTopTenPolicydetails")
	public ResponseEntity<?> getCoustomerDetal(@RequestBody RenewalTrackReq req) {
		if (req.getDivisionCode() != null && !req.getDivisionCode().trim().isEmpty() && req.getStartDate() != null
				&& req.getEndDate() != null) {
			return ResponseEntity.ok(service.getTop10CustomerDetails(req));
		} else {
			return ResponseEntity.badRequest().body("DivisionCode  StartDate and EndDate Required");
		}
	}

	@GetMapping("/getExpiryPolicyDetails/{divisionCode}")
	public ResponseEntity<?> getExpiryPolicyDetails(@PathVariable("divisionCode") String divisionCode) {
		if (divisionCode != null && !divisionCode.trim().isEmpty()) {
			return ResponseEntity.ok(service.getExpiryPolicyDetails(divisionCode));
		} else {
			return ResponseEntity.badRequest().body("DivisionCode");
		}
	}

	@PostMapping("/getStatusPolicyList")
	public ResponseEntity<?> getStatusPolicyList(@RequestBody RenewalTrackReq req) {
		if (req.getDivisionCode() != null && !req.getDivisionCode().trim().isEmpty() && req.getStartDate() != null
				&& req.getEndDate() != null && req.getStatus() != null) {
			return ResponseEntity.ok(service.getPolicyStatusList(req));
		} else {
			return ResponseEntity.badRequest().body("\"DivisionCode, Status , StartDate and EndDate Required\"");
		}
	}

	@PostMapping("/updaterenewpremiapolicy")
	public ResponseEntity<CommonRes> updateRenewalPremiaPolicy(@RequestBody UpdateRenewalPremiaPolicyReq req) {
		CommonRes response = service.updateRenewalPremiaPolicy(req);
		return ResponseEntity.ok(response);
	}
	@PostMapping("/top10premiumcustomers")
	public ResponseEntity<?> getTop10PremiumCustomers(@RequestBody RenewalTrackReq req) {
		return ResponseEntity.ok(service.getTopPremiumCustomerDetails(req));
	}
	@PostMapping("insertRenewVehicleInfo")
	public CommonRes insertRenewVehicleInfo(@RequestBody RenewalVehicleReq req)
	{
		CommonRes res = new CommonRes();
		List<Error>  list= new ArrayList<Error>();
		list=vali.validateReq(req);
		if (list != null && !list.isEmpty()) {
			res.setIsError(true);
			res.setMessage("failed");
			res.setErrorMessage(list);
		}
		else {
			res=service.insertVehicleInfo(req);
		}
		return res;
	}
	@GetMapping("/getRenewVehicleInfo")
	public CommonRes getRenewVehicl(@RequestParam("policyNo") String policyNo) 
	{
		CommonRes res = new CommonRes();
		if(policyNo==null)
		{	res.setIsError(true);
			res.setMessage("Enter Policy number");
		}
		else {
			res=service.getRenewVehicl(policyNo);
		}
		return res;
		}
}
