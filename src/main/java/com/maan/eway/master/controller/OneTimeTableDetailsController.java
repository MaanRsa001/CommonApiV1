package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.ColumnNameDropDownlReq;
import com.maan.eway.master.req.OneTimeTableReq;
import com.maan.eway.master.service.OneTimeTableDetailsService;
import com.maan.eway.res.DropDownRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dropdown")
@Api(tags = "MASTER : Drop Down Controller", description = "API's")

public class OneTimeTableDetailsController {

	@Autowired
	private OneTimeTableDetailsService service;
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("/tablename")
	@ApiOperation(value = "This method is to Table Name Drop Down")
	public ResponseEntity<CommonRes> tableName() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.tableName();
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/mastertable")
	@ApiOperation(value = "This method is to masterTable Drop Down")
	public ResponseEntity<CommonRes> masterTable(@RequestBody OneTimeTableReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.masterTable(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/eservicetable")
	@ApiOperation(value = "This method is to masterTable Drop Down")
	public ResponseEntity<CommonRes> eserviceTable(@RequestBody OneTimeTableReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.eserviceTable(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/columnname")
	@ApiOperation(value = "This method is to Column Name Drop Down")
	public ResponseEntity<CommonRes> columnName(@RequestBody ColumnNameDropDownlReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.columnName(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/integratointable")
	@ApiOperation(value = "This method is to Integration Table Name Drop Down")
	public ResponseEntity<CommonRes> integrationtable(@RequestBody OneTimeTableReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.integrationtable(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/sourcetable")
	@ApiOperation(value = "This method is to Source Table Name Drop Down")
	public ResponseEntity<CommonRes> sourcetable(@RequestBody OneTimeTableReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.sourcetable(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("/exceltable")
	@ApiOperation(value = "This method is to exceltable Table Name Drop Down")
	public ResponseEntity<CommonRes> exceltable() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.exceltable("EXCEL_UPLOAD_TABLE");
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	
	
}
