package com.maan.eway.document.ai.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.maan.eway.document.ai.res.RegistrationDocumentRecognitionRes;

public interface RegistrationDocumentRecognitionService {

	RegistrationDocumentRecognitionRes getDocumentResult(MultipartFile file, Object req) throws IOException;

}
