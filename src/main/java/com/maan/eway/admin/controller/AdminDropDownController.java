package com.maan.eway.admin.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.admin.service.AdminDropDownService;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.res.DropDownRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dropdown")
@Api(tags = "MASTER : Drop Down Controller", description = "API's")

public class AdminDropDownController {

	@Autowired
	private  AdminDropDownService dropDownService;
	

	// Gender
 
	@PostMapping("/gender")
	@ApiOperation(value = "This method is to Gender Types Drop Down")
	public ResponseEntity<CommonRes> getgender(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getgender(req);
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
 
	@PostMapping("/mobilecodes")
	@ApiOperation(value = "This method is to Gender Types Drop Down")
	public ResponseEntity<CommonRes> getMobileCodes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMobileCodes(req);
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
	
 
	@PostMapping("/constmaterial")
	@ApiOperation(value = "This method is to ConstMaterial Drop Down")
	public ResponseEntity<CommonRes> getConstMaterial(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getConstMaterial(req);
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
 
	@PostMapping("/outbuildingconst")
	@ApiOperation(value = "This method is to OutbuildingConst  Drop Down")
	public ResponseEntity<CommonRes> getOutbuildingConst(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getOutbuildingConst(req);
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
 
	@PostMapping("/aboutbuilding")
	@ApiOperation(value = "This method is to AboutBuilding  Drop Down")
	public ResponseEntity<CommonRes> getAboutBuilding(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getAboutBuilding(req);
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
 
	@PostMapping("/stateextent")
	@ApiOperation(value = "This method is to StateExtent Drop Down")
	public ResponseEntity<CommonRes> getStateExtent(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getStateExtent(req);
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
 
	@PostMapping("/contentname")
	@ApiOperation(value = "This method is to Content Name Drop Down")
	public ResponseEntity<CommonRes> getContentName(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getContentName(req);
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
	
 
	@PostMapping("/propertyname")
	@ApiOperation(value = "This method is to Property Name Drop Down")
	public ResponseEntity<CommonRes> getPropertyName(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getPropertyName(req);
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
	
 
	@PostMapping("/businesstype")
	@ApiOperation(value = "This method is to Business Type  Drop Down")
	public ResponseEntity<CommonRes> getBusinessType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getBusinessType(req);
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
 
	@PostMapping("/sourcetype")
	@ApiOperation(value = "This method is to Business Type  Drop Down")
	public ResponseEntity<CommonRes> getSourceType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getSourceType(req);
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
 
	@PostMapping("/commissiontype")
	@ApiOperation(value = "This method is to Business Type  Drop Down")
	public ResponseEntity<CommonRes> getCommissionType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getCommissionType(req);
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
	
	@PostMapping("/proratatype")
	@ApiOperation(value = "This method is to Business Type  Drop Down")
	public ResponseEntity<CommonRes> getProRataType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getProRataType(req);
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
