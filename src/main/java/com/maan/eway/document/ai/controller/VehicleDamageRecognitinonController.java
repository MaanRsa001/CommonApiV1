package com.maan.eway.document.ai.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.document.ai.res.GetVehicleDamegeReq;
import com.maan.eway.document.ai.res.getDamageResponse;
import com.maan.eway.document.ai.service.VehicleDamageRecognitinonService;
import com.maan.eway.document.req.DocumentDeleteReq;
import com.maan.eway.error.Error;

import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/vehicle")
public class VehicleDamageRecognitinonController {

	@Autowired
	private VehicleDamageRecognitinonService damageRecognitionService;

	private Logger log = LogManager.getLogger(VehicleDamageRecognitinonController.class);

	@PostMapping("/generateTransaction")
	public ResponseEntity<Long> generateTransactionId() {
		long nextId = damageRecognitionService.generateAndReturnTransactionId();
		return ResponseEntity.ok(nextId);
	}

	@PostMapping("/image")
	public ResponseEntity<CommonRes> ImageRecognitionReport(@RequestParam("File") MultipartFile file,
			@RequestParam("Req") String jsonString) {
		List<Error> errors = new ArrayList<Error>();
		log.info(jsonString);
		CommonRes res = new CommonRes();
		Map<String, Object> saveDetails = damageRecognitionService.generateReply(file);
		
		if (saveDetails.containsKey("error")) {
//			Map<String, Object> resultList=(Map<String, Object>) saveDetails.get("error");
			res.setIsError(true);
			res.setCommonResponse(Map.of("error", saveDetails.get("error")));
			errors.add(new Error("9999", "DocumentUpload", "Not a valid Image"));
			res.setErrorMessage(errors);
			// res.setErrorMessage("Not a valid Image");
		}
		else {
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) saveDetails.get("Result");
		res.setIsError(false);
		res.setCommonResponse(resultList);
		res.setMessage("Success");
		 damageRecognitionService.savevehicleDetails(file, jsonString, saveDetails);
		}
		
	
		
		return ResponseEntity.status(HttpStatus.OK).body(res);

	}

	@PostMapping("/get/damagedetails")
	public ResponseEntity<CommonRes> getDamageDetails(@RequestBody GetVehicleDamegeReq req) {
		CommonRes data = new CommonRes();
		getDamageResponse res = damageRecognitionService.getDamageDetails(req);

		if (res != null) {
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			return new ResponseEntity<>(data, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/delete")
	@ApiOperation(value = "This method is to Remove Document")
	public ResponseEntity<CommonRes> deleteFile(@RequestBody DocumentDeleteReq req) {

		CommonRes res = damageRecognitionService.deleteFile(req);
		return ResponseEntity.status(HttpStatus.OK).body(res);

	}

}
