package com.maan.eway.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.GetDepositPaymentReq;
import com.maan.eway.common.req.SaveDepositeMasterReq;
import com.maan.eway.common.req.SavePaymentDepositReq;
import com.maan.eway.common.req.SavePremiumDepositReq;
import com.maan.eway.common.req.SavedepositDetailReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.DepositService;

@RestController
@RequestMapping("deposit")
public class DepositController {
	
	@Autowired
	private DepositService service;

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("save/depositMaster")
	public CommonRes saveDepositeMaster(@RequestBody SaveDepositeMasterReq req) {
		return service.saveDepositeMaster(req);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("save/premium")
	public CommonRes savePremiumDeposit(@RequestBody SavePremiumDepositReq req) {
		return service.savePremiumDeposit(req);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("save/payment")
	public CommonRes savePaymentDeposit(@RequestBody SavePaymentDepositReq req) {
		return service.savePaymentDeposit(req);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("get/CbcbyBrokerId/{loginId}")
	public CommonRes CbcbyBrokerId(@PathVariable ("loginId") String loginId) {
		return service.CbcbyBrokerId(loginId);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("get/depositMasterById")
	public CommonRes DepositMasterById(@RequestParam ("cbcNo") String cbcNo) {
		return service.DepositMasterById(cbcNo);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("get/depositDetail")
	public CommonRes GetDepositDetail() {
		return service.GetDepositDetail();
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("get/depositDetailById")
	public CommonRes GetDepositDetailById(@RequestParam ("cbcNo") String cbcNo) {
		return service.GetDepositDetailById(cbcNo);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("get/Payment")
	public CommonRes GetDepositPayment(@RequestBody GetDepositPaymentReq req) {
		return service.GetDepositPayment(req);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("save/depositDetail")
	public CommonRes savedepositDetail(@RequestBody SavedepositDetailReq req) {
		return service.savedepositDetail(req);
	}
	
}
