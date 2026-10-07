package com.maan.eway.claimintimation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.claimintimation.dto.FnolIntimationRequest;
import com.maan.eway.claimintimation.dto.GetIntimationReq;
import com.maan.eway.common.res.CommonRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/fnolintimation")
@Api(tags = "INTIMATION DETAILS", description = "API's")
public class FnolIntimationController {

	@Autowired
	private FnolIntimationService fnolService;
	
	@PostMapping("/save")
	@ApiOperation(value = "This method is Insert  Intimation Details with third party infos and document details")
    public ResponseEntity<CommonRes> saveFnolIntimation(@RequestBody FnolIntimationRequest req) {
        CommonRes response = fnolService.saveFnolIntimation(req);
        return ResponseEntity.ok(response);
    }
	
	@PostMapping("/getbyIntimationno")
	@ApiOperation(value = "This method is Get  Intimation Details by Intimation Number")
	public ResponseEntity<CommonRes> getByIntimationNo(@RequestBody GetIntimationReq req){
		CommonRes response = fnolService.getByIntimationNo(req);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/getbypolicyno")
	@ApiOperation(value = "This method is Get  Intimation Details by Policy Number")
	public ResponseEntity<CommonRes> getByPolicyNo(@RequestParam String policyNo){
		CommonRes response = fnolService.getIntimationDetailsByPolicyNo(policyNo );
		return ResponseEntity.ok(response);
	}
	
}
