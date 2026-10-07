package com.maan.eway.endorsment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.EndtSectionListReq;
import com.maan.eway.common.req.EndtSectionSaveReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.endorsment.request.EndDeleteReq;
import com.maan.eway.endorsment.request.EndRequest;
import com.maan.eway.endorsment.request.EndorsementEmiReq;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.endorsment.request.EndtMaster;
import com.maan.eway.endorsment.service.EndorsementEmiService;
import com.maan.eway.endorsment.service.EndorsementService;
import com.maan.eway.error.Error;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "ENDORESMENT : Endorsment ", description = "API's")
@RequestMapping("/endorsment/")
public class EndorsementController {
	
	@Autowired
	private EndorsementService eservice;
	
	@Autowired
	private EndorsementEmiService endtEmiService;
	
	@PostMapping("/cancellation")
	public ResponseEntity<CommonRes> cancelPolicy(@RequestBody Endorsment request) {
	 	CommonRes data = eservice.cancelPolicy(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/endorsementTypes")
	public ResponseEntity<EndtMaster> endorsementTypes(@RequestBody Endorsment request) {
		EndtMaster data = eservice.getEndorsementTypes(request);
	 	if (data != null) {
			return new ResponseEntity<EndtMaster>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/datas")
	public ResponseEntity<List<EndorsementCriteriaRes>> endorsementPendingData(@RequestBody Endorsment request) {
		List<EndorsementCriteriaRes> data = eservice.endorsementPendingData(request);
	 	if (data != null) {
			return new ResponseEntity<List<EndorsementCriteriaRes>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<CommonRes> createEndorsment(@RequestBody Endorsment request) {
		CommonRes data = new CommonRes();
		List<Error> validation = eservice.validateEndtDetails(request);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			data = eservice.createEndorsment(request);
		 	if (data != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/changeEndtStatus")
	public ResponseEntity<CommonRes> changeEndtStatus(@RequestBody ChangeEndoStatusReq req) {
		CommonRes data = eservice.changeEndtStatus(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/sectionlist")
	public ResponseEntity<CommonRes> getSectionList(@RequestBody EndtSectionListReq req) {
		CommonRes data = eservice.getSectionList(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/saveendtsection")
	public ResponseEntity<CommonRes> saveEndtSection(@RequestBody EndtSectionSaveReq req) {
		CommonRes data = eservice.saveEndtSection(req);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/updateAdditionalInfo")
	public ResponseEntity<CommonRes> updateAdditionalInfo(@RequestBody EndRequest request) {
		CommonRes data = new CommonRes();
			data = eservice.updateAdditionalInfo(request);
		 	if (data != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/copyemis")
	public ResponseEntity<CommonRes> copyEmiinfos(@RequestBody EndorsementEmiReq request) {
		CommonRes data = new CommonRes();
		data = endtEmiService.copyEmiDetails(request);
		 	if (data != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/editDelete")
	public ResponseEntity<CommonRes> editDelete(@RequestBody EndDeleteReq request) {
		CommonRes data = new CommonRes();
		data = eservice.editDeleteAlltable(request);
		 	if (data != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
	}

	
}
