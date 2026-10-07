package com.maan.eway.document.ai.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.document.ai.res.GetVehicleDamegeReq;
import com.maan.eway.document.ai.res.getDamageResponse;
import com.maan.eway.document.req.DocumentDeleteReq;

public interface VehicleDamageRecognitinonService {

	long generateAndReturnTransactionId();

	Map<String, Object> generateReply(MultipartFile file);

	//void savevehicleDetails(MultipartFile file, Long id, String angle, Map<String, Object> response);

	void savevehicleDetails(MultipartFile file, String documentreq, Map<String, Object> response);

	getDamageResponse getDamageDetails(GetVehicleDamegeReq req);

	CommonRes deleteFile(DocumentDeleteReq req);

}
