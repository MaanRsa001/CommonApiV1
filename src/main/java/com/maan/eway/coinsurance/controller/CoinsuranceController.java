package com.maan.eway.coinsurance.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.coinsurance.req.DetailsReq;
import com.maan.eway.coinsurance.req.QuoteNoReq;
import com.maan.eway.coinsurance.service.CoinsuranceService;
import com.maan.eway.common.res.CommonRes;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/Coinsurance")
public class CoinsuranceController {

	@Autowired
	CoinsuranceService service;
	
	@PostMapping("insertCoinsuranceHeader")
	public ResponseEntity<CommonRes> insertCoInsurance(@Valid @RequestBody QuoteNoReq req)
	{
		CommonRes res = new CommonRes();
		res = service.insertCo(req);
		return ResponseEntity.ok(res);
	}

@PostMapping("insertCoinsuranceDetails")
public ResponseEntity<CommonRes> insertdetails(@Valid @RequestBody DetailsReq req)
{
	CommonRes res = new CommonRes();
	Map<Boolean, String> validationShare = service.validationShare(req);
	Map<Boolean, String> validationRole = service.validationRole(req);
	boolean isShareValid = validationShare.containsKey(true);
	boolean isRoleValid = validationRole.containsKey(true);
	if (isShareValid && isRoleValid) {
		res = service.insertCoDetails(req);
		return ResponseEntity.ok(res);
	} else {
		
		String errorMessage = "";
        if (!isShareValid) {
            errorMessage += validationShare.get(false);
        }
        if (!isRoleValid) {
            if (!errorMessage.isEmpty()) {
                errorMessage += " | ";
            }
            errorMessage += validationRole.get(false);
        }
		res.setMessage(errorMessage);
		res.setCommonResponse(null);
		res.setIsError(true);
		res.setErroCode(0);
		return ResponseEntity.ok(res);
	}
}
@PostMapping("getCoinsuranceDetails")
public ResponseEntity<CommonRes> getCoInsurance(@Valid @RequestBody QuoteNoReq req)
{
	CommonRes res = new CommonRes();
	res = service.getDetails(req);
	return ResponseEntity.ok(res);
}

}
