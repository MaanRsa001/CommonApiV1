package com.maan.eway.oman;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.master.req.StateMasterDropDownReq;


import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/oman")
public class OmanPostalCodeMasterController {

	@Autowired
	private OmanPostalCodeMasterService service;
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value="/dropdown/city",produces = "application/json")
	@ApiOperation(value = "This method is get State Master Drop Down")

	public ResponseEntity<OmanDropdownCommonRes> getOmanRegionStateMasterDropdown(@RequestBody StateMasterDropDownReq req) {

		OmanDropdownCommonRes data = new OmanDropdownCommonRes();

		// Save
		List<OmanDropDownRes> res = service.getOmanPostalCodeMasterDropdown(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<OmanDropdownCommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
}
