package com.maan.eway.document.ai.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.maan.eway.document.req.DocumentUploadReq;

public interface PassportRecognitionService {

	JsonNode generateAiResult(MultipartFile file) throws IOException;

	JsonNode pinCertificateAiResult(MultipartFile file) throws IOException;

	Boolean checkPinNumber(String pinNo, Object req);

	JsonNode savePassportDetails(JsonNode resp,Object request, String token);

	//JsonNode idCertificateAiResult(MultipartFile file) throws IOException;

	Boolean checkIdNumber(String idNo, Object req);

	JsonNode generateDocResultFromAI(MultipartFile file, Object req)throws IOException;

	Boolean checkLogBook(String pinNo, String regNo, String chassisNo, String engineNo, Object req);

	JsonNode CheckValidationWithAi(MultipartFile file, String documentName);

	Boolean checkValidIdNumber(String idNumber, DocumentUploadReq req);

	Boolean checkValidPinNumber(String pinNo, DocumentUploadReq req);

	Boolean checkValidLogBook(String regNo, String chassisNo, String engineNo, DocumentUploadReq req);

	Boolean checkValidCOI(String pinNo, DocumentUploadReq req);

	Boolean checkValidCR12(String pinNo, DocumentUploadReq req);

	

}
