package com.maan.eway.whatsapp;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
@Api(tags = "WHATSAPP : Color Dropdown", description = "WhatsApp color dropdown API")
public class WhatsappColorDropdownController {

	@Autowired
	private WhatsappColorService colorService;

	@PostMapping(value = "/whatsapp/dropdown/color", produces = "application/json")
	@ApiOperation(value = "Get vehicle color dropdown for WhatsApp integration")
	public ResponseEntity<WhatsappColorDropdownCommonRes> getWhatsappColorDropdown(
			@RequestBody WhatsappColorDropdownReq req) {

		WhatsappColorDropdownCommonRes data = colorService.dropdownTop100(req);
		if (data != null && Boolean.FALSE.equals(data.getIsError())) {
			return new ResponseEntity<>(data, HttpStatus.CREATED);
		}
		if (data != null) {
			return new ResponseEntity<>(data, HttpStatus.OK);
		}
		WhatsappColorDropdownCommonRes error = new WhatsappColorDropdownCommonRes();
		error.setMessage("Failed");
		error.setIsError(true);
		error.setErrorMessage(Collections.emptyList());
		error.setResult(Collections.emptyList());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@PostMapping(value = "/whatsapp/search/color", produces = "application/json")
	@ApiOperation(value = "Search color options for WhatsApp integration")
	public ResponseEntity<WhatsappColorDropdownCommonRes> searchWhatsappColor(@RequestBody WhatsappColorDropdownReq req) {
		WhatsappColorDropdownCommonRes data = colorService.search(req);
		if (data != null && Boolean.FALSE.equals(data.getIsError())) {
			return new ResponseEntity<>(data, HttpStatus.CREATED);
		}
		if (data != null) {
			return new ResponseEntity<>(data, HttpStatus.OK);
		}
		WhatsappColorDropdownCommonRes error = new WhatsappColorDropdownCommonRes();
		error.setMessage("Failed");
		error.setIsError(true);
		error.setErrorMessage(Collections.emptyList());
		error.setResult(Collections.emptyList());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
